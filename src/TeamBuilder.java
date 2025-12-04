import java.util.*;
import java.util.concurrent.*;

public class TeamBuilder {

    public static class Result {
        public ArrayList<Team> teams = new ArrayList<>();
        public ArrayList<Participant> unassigned = new ArrayList<>();
    }

    private static final Random RNG = new Random();

    public static Result buildTeams(ArrayList<Participant> participants, int teamSize) {

        Result result = new Result();
        if (participants == null || participants.isEmpty()) return result;

        // ===================== SPLIT PERSONALITIES (parallel) =====================
        List<Participant> leaders   = Collections.synchronizedList(new ArrayList<>());
        List<Participant> thinkers  = Collections.synchronizedList(new ArrayList<>());
        List<Participant> balanced  = Collections.synchronizedList(new ArrayList<>());

        participants.parallelStream().forEach(p -> {
            switch (p.getPersonalityType()) {
                case "Leader"   -> leaders.add(p);
                case "Thinker"  -> thinkers.add(p);
                case "Balanced" -> balanced.add(p);
            }
        });

        // ===================== TEAM COUNT =====================
        int totalTeams = participants.size() / teamSize;
        if (totalTeams <= 0) {
            result.unassigned.addAll(participants);
            return result;
        }

        ArrayList<Team> teams = new ArrayList<>();
        for (int i = 0; i < totalTeams; i++) teams.add(new Team(i + 1));

        // ===================== STEP 1-3 (sequential: unchanged behavior) =====================
        for (Team t : teams) addInitial(t, leaders);
        for (Team t : teams) if (t.size() < teamSize) addInitial(t, thinkers);
        for (Team t : teams) if (t.size() < teamSize) addInitial(t, balanced);

        // ===================== STEP 4 (CONCURRENCY FIXED) =====================
        List<Participant> remaining = Collections.synchronizedList(new ArrayList<>());
        remaining.addAll(leaders);
        remaining.addAll(thinkers);
        remaining.addAll(balanced);

        boolean added;
        do {
            final boolean[] localAdded = {false};   // <— atomic reference

            teams.parallelStream().forEach(t -> {
                if (t.size() >= teamSize) return;
                if (remaining.isEmpty()) return;

                Participant pick;
                synchronized (remaining) {
                    pick = pickSmartBalanced(t, remaining);
                    if (pick != null) remaining.remove(pick);
                }

                if (pick != null) {
                    synchronized (t) { t.addMember(pick); }
                    localAdded[0] = true;             // <-- notify progress
                }
            });

            added = localAdded[0] && anyTeamNotFull(teams, teamSize);

        } while (added && !remaining.isEmpty());

        result.unassigned.addAll(remaining);
        result.teams.addAll(teams);
        return result;
    }

    // ----- unchanged methods -----

    private static void addInitial(Team team, List<Participant> pool) {
        if (pool.isEmpty()) return;
        List<Participant> valid = new ArrayList<>();
        for (Participant p : pool) if (canAddInitial(team, p)) valid.add(p);
        if (valid.isEmpty()) return;
        Collections.shuffle(valid);
        Participant choice = valid.get(0);
        team.addMember(choice);
        pool.remove(choice);
    }

    private static boolean canAddInitial(Team t, Participant p) {
        if (!t.canAddGame(p.getPreferredGame())) return false;
        if (p.getPersonalityType().equals("Leader") && !t.canAddLeader()) return false;
        if (p.getPersonalityType().equals("Thinker") && !t.canAddThinker()) return false;
        return true;
    }

    private static Participant pickSmartBalanced(Team t, List<Participant> pool) {
        List<Participant> valid = new ArrayList<>();
        for (Participant p : pool) if (canAddSmart(t, p)) valid.add(p);
        if (valid.isEmpty()) return null;

        double bestVariance = Double.MAX_VALUE;
        List<Participant> best = new ArrayList<>();

        for (Participant p : valid) {
            double var = calculateVarianceWith(t, p);
            if (var < bestVariance) {
                bestVariance = var;
                best.clear();
                best.add(p);
            } else if (var == bestVariance) {
                best.add(p);
            }
        }

        return best.get(RNG.nextInt(best.size()));
    }

    private static boolean canAddSmart(Team t, Participant p) {
        if (!t.canAddGame(p.getPreferredGame())) return false;
        if (p.getPersonalityType().equals("Thinker") && !t.canAddThinker()) return false;
        if (p.getPersonalityType().equals("Leader")) {
            if (t.canAddLeader()) return true;
            return canAddExtraLeader(t, p);
        }
        return true;
    }

    private static boolean canAddExtraLeader(Team t, Participant p) {
        Set<String> roles = new HashSet<>();
        for (Participant m : t.getMembers()) roles.add(m.getPreferredRole());
        boolean improvesRole = !roles.contains(p.getPreferredRole());
        boolean improvesVar = calculateVarianceWith(t, p) <= calculateVariance(t);
        return improvesRole || improvesVar;
    }

    private static double calculateVariance(Team t) {
        if (t.getMembers().isEmpty()) return 0;
        double avg = t.getTeamAverageSkill();
        double sum = 0;
        for (Participant p : t.getMembers())
            sum += Math.pow(p.getSkillLevel() - avg, 2);
        return sum / t.getMembers().size();
    }

    private static double calculateVarianceWith(Team t, Participant p) {
        ArrayList<Participant> temp = new ArrayList<>(t.getMembers());
        temp.add(p);
        double avg = temp.stream().mapToInt(Participant::getSkillLevel).average().orElse(0);
        double sum = 0;
        for (Participant m : temp) sum += Math.pow(m.getSkillLevel() - avg, 2);
        return sum / temp.size();
    }

    private static boolean anyTeamNotFull(List<Team> teams, int teamSize) {
        for (Team t : teams) if (t.size() < teamSize) return true;
        return false;
    }
}
