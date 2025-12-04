import java.util.*;

public class TeamBuilder {

    public static class Result {
        public ArrayList<Team> teams = new ArrayList<>();
        public ArrayList<Participant> unassigned = new ArrayList<>();
    }

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
        for (int i = 0; i < totalTeams; i++) {
            teams.add(new Team(i + 1));
        }

        // ------------------------------------------------
        // STEP 1: ADD 1 LEADER TO EACH TEAM (IF POSSIBLE)
        // ------------------------------------------------
        for (Team t : teams) {
            addOneFromPool(t, leaders);
        }

        // ------------------------------------------------
        // STEP 2: ADD 1 THINKER TO EACH TEAM (IF POSSIBLE)
        // ------------------------------------------------
        for (Team t : teams) {
            if (t.size() >= teamSize) continue;
            addOneFromPool(t, thinkers);
        }

        // ------------------------------------------------
        // STEP 3: ADD 1 BALANCED TO EACH TEAM (IF POSSIBLE)
        // ------------------------------------------------
        for (Team t : teams) {
            if (t.size() >= teamSize) continue;
            addOneFromPool(t, balanced);
        }

        // ------------------------------------------------
        // STEP 4: FILL THE REST OF SLOTS WITH ANY REMAINING
        // (leaders + thinkers + balanced combined)
        // ------------------------------------------------
        ArrayList<Participant> remaining = new ArrayList<>();
        remaining.addAll(leaders);
        remaining.addAll(thinkers);
        remaining.addAll(balanced);

        boolean added;
        do {
            added = false;

            for (Team t : teams) {
                if (t.size() >= teamSize) continue;
                Participant chosen = pickAnyValid(t, remaining);
                if (chosen != null) {
                    t.addMember(chosen);
                    remaining.remove(chosen);
                    added = true;
                }
            }

        } while (added && !remaining.isEmpty() && anyTeamNotFull(teams, teamSize));

        // whatever is still left is unassigned
        result.teams.addAll(teams);
        result.unassigned.addAll(remaining);

        return result;
    }

    // =====================================================
    // HELPERS
    // =====================================================

    // Try to add exactly ONE participant from the given pool into team
    private static void addOneFromPool(Team t, List<Participant> pool) {
        for (int i = 0; i < pool.size(); i++) {
            Participant p = pool.get(i);
            if (canAdd(t, p)) {
                t.addMember(p);
                pool.remove(i);
                return;
            }
        }
    }

    // Pick any valid participant from remaining for this team
    private static Participant pickAnyValid(Team t, List<Participant> pool) {
        for (Participant p : pool) {
            if (canAdd(t, p)) {
                return p;
            }
        }
        return null;
    }

    private static boolean anyTeamNotFull(List<Team> teams, int teamSize) {
        for (Team t : teams) {
            if (t.size() < teamSize) return true;
        }
        return false;
    }

    // Respect personality + game cap (max 2 per game)
    private static boolean canAdd(Team t, Participant p) {

        // game cap: max 2 per game per team (Team already has MAX_PER_GAME = 2)
        if (!t.canAddGame(p.getPreferredGame())) return false;

        // personality caps from your Team class
        String type = p.getPersonalityType();
        if ("Leader".equals(type) && !t.canAddLeader()) return false;
        if ("Thinker".equals(type) && !t.canAddThinker()) return false;

        return true; // Balanced has no special personality limit
    }
}
