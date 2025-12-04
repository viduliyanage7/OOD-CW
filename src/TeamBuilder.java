import java.util.*;

public class TeamBuilder {

    public static class Result {
        public ArrayList<Team> teams = new ArrayList<>();
        public ArrayList<Participant> unassigned = new ArrayList<>();
    }

    private static final Random RNG = new Random();

    public static Result buildTeams(ArrayList<Participant> participants, int teamSize) {

        Result result = new Result();

        if (participants == null || participants.isEmpty()) {
            return result;
        }

        // ===== SPLIT BY PERSONALITY =====
        List<Participant> leaders = new ArrayList<>();
        List<Participant> thinkers = new ArrayList<>();
        List<Participant> balanced = new ArrayList<>();

        for (Participant p : participants) {
            switch (p.getPersonalityType()) {
                case "Leader" -> leaders.add(p);
                case "Thinker" -> thinkers.add(p);
                case "Balanced" -> balanced.add(p);
            }
        }

        // ===== HOW MANY TEAMS? =====
        int totalTeams = participants.size() / teamSize;
        if (totalTeams <= 0) {
            result.unassigned.addAll(participants);
            return result;
        }

        ArrayList<Team> teams = new ArrayList<>();
        for (int i = 0; i < totalTeams; i++)
            teams.add(new Team(i + 1));

        // ------------------------------------------------
        // STEP 1: 1 LEADER PER TEAM
        // ------------------------------------------------
        for (Team t : teams)
            addInitial(t, leaders);

        // ------------------------------------------------
        // STEP 2: 1 THINKER PER TEAM
        // ------------------------------------------------
        for (Team t : teams)
            if (t.size() < teamSize)
                addInitial(t, thinkers);

        // ------------------------------------------------
        // STEP 3: 1 BALANCED PER TEAM
        // ------------------------------------------------
        for (Team t : teams)
            if (t.size() < teamSize)
                addInitial(t, balanced);

        // =======================================================
        // STEP 4: FILL REMAINING SLOTS (with near-optimal skill balance)
        // =======================================================
        ArrayList<Participant> remaining = new ArrayList<>();
        remaining.addAll(leaders);
        remaining.addAll(thinkers);
        remaining.addAll(balanced);

        boolean added;
        do {
            added = false;

            for (Team t : teams) {
                if (t.size() >= teamSize || remaining.isEmpty()) continue;

                Participant p = pickSmartBalanced(t, remaining);

                if (p != null) {
                    t.addMember(p);
                    remaining.remove(p);
                    added = true;
                }
            }

        } while (added && anyTeamNotFull(teams, teamSize));

        result.unassigned.addAll(remaining);
        result.teams.addAll(teams);

        return result;
    }

    // -----------------------------------------------------------
    // STEP 1–3: Add initial (Leader/Thinker/Balanced) in order
    // -----------------------------------------------------------
    private static void addInitial(Team team, List<Participant> pool) {
        if (pool.isEmpty()) return;

        List<Participant> valid = new ArrayList<>();

        for (Participant p : pool)
            if (canAddInitial(team, p)) valid.add(p);

        if (valid.isEmpty()) return;

        // random only between valid ones
        Collections.shuffle(valid);
        Participant choice = valid.get(0);

        team.addMember(choice);
        pool.remove(choice);
    }

    private static boolean canAddInitial(Team t, Participant p) {

        // ※ GAME CAP
        if (!t.canAddGame(p.getPreferredGame())) return false;

        // ※ PERSONALITY CAPS
        if (p.getPersonalityType().equals("Leader") && !t.canAddLeader())
            return false;

        if (p.getPersonalityType().equals("Thinker") && !t.canAddThinker())
            return false;

        return true;
    }

    // -----------------------------------------------------------
    // STEP 4: SMART BALANCED PICK — variance minimizer
    // -----------------------------------------------------------
    private static Participant pickSmartBalanced(Team t, List<Participant> pool) {

        List<Participant> valid = new ArrayList<>();

        for (Participant p : pool) {
            if (canAddSmart(t, p)) valid.add(p);
        }

        if (valid.isEmpty()) return null;

        // Evaluate variance impact
        double bestVariance = Double.MAX_VALUE;
        List<Participant> bestCandidates = new ArrayList<>();

        for (Participant p : valid) {
            double var = calculateVarianceWith(t, p);

            if (var < bestVariance) {
                bestVariance = var;
                bestCandidates.clear();
                bestCandidates.add(p);
            }
            else if (var == bestVariance) {
                bestCandidates.add(p);
            }
        }

        // Random choice only among SAME-VARIANCE candidates
        return bestCandidates.get(RNG.nextInt(bestCandidates.size()));
    }

    // -----------------------------------------------------------
    // Smart constraints for extra players
    // -----------------------------------------------------------
    private static boolean canAddSmart(Team t, Participant p) {

        // ※ GAME CAP
        if (!t.canAddGame(p.getPreferredGame())) return false;

        // Thinker limit still applies
        if (p.getPersonalityType().equals("Thinker") && !t.canAddThinker())
            return false;

        // Leader logic
        if (p.getPersonalityType().equals("Leader")) {

            // If team has no leader → ok
            if (t.canAddLeader()) return true;

            // Otherwise → smart extra leader rules
            return canAddExtraLeader(t, p);
        }

        return true; // Balanced always ok
    }

    // -----------------------------------------------------------
    // EXTRA LEADER LOGIC — Soft balancing & diversity
    // -----------------------------------------------------------
    private static boolean canAddExtraLeader(Team t, Participant p) {

        // Role diversity
        Set<String> roles = new HashSet<>();
        for (Participant m : t.getMembers())
            roles.add(m.getPreferredRole());

        boolean improvesRoleDiversity = !roles.contains(p.getPreferredRole());

        // Variance comparison
        double beforeVar = calculateVariance(t);
        double afterVar = calculateVarianceWith(t, p);

        boolean improvesSkillBalance = (afterVar <= beforeVar);

        // Accept if ANY of the conditions hold
        return improvesRoleDiversity || improvesSkillBalance;
    }

    // -----------------------------------------------------------
    // Skill variance helpers
    // -----------------------------------------------------------
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

        for (Participant m : temp)
            sum += Math.pow(m.getSkillLevel() - avg, 2);

        return sum / temp.size();
    }

    // -----------------------------------------------------------
    // Utility: Check if any team is not full
    // -----------------------------------------------------------
    private static boolean anyTeamNotFull(List<Team> teams, int teamSize) {
        for (Team t : teams)
            if (t.size() < teamSize) return true;
        return false;
    }
}
