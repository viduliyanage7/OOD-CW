import java.util.*;

public class TeamBuilder {

    public static class Result {
        public ArrayList<Team> validTeams;
        public ArrayList<Team> unfinishedTeams;
        public ArrayList<Participant> unassigned;

        Result(ArrayList<Team> v, ArrayList<Team> u, ArrayList<Participant> rest) {
            validTeams = v;
            unfinishedTeams = u;
            unassigned = rest;
        }
    }

    // ============= ROLE RULE =================
    private static boolean canAddRole(Team team, Participant p) {
        return team.getUniqueRoleCount() < 3 ||
                !team.getMembers().stream()
                        .map(Participant::getPreferredRole)
                        .toList().contains(p.getPreferredRole());
    }

    // ============= MAIN BUILD FUNCTION =================
    public static Result buildTeams(ArrayList<Participant> participants, int teamSize) {

        ArrayList<Team> validTeams = new ArrayList<>();
        ArrayList<Team> unfinishedTeams = new ArrayList<>();
        ArrayList<Team> teams = new ArrayList<>();

        ArrayList<Participant> unassigned = new ArrayList<>();

        List<Participant> leaders = new ArrayList<>();
        List<Participant> thinkers = new ArrayList<>();
        List<Participant> balanced = new ArrayList<>();

        // Classification
        for (Participant p : participants) {
            switch (p.getPersonalityType()) {
                case "Leader" -> leaders.add(p);
                case "Thinker" -> thinkers.add(p);
                case "Balanced" -> balanced.add(p);
            }
        }

        int totalTeams = Math.min(
                Math.min(leaders.size(), thinkers.size()),
                participants.size() / teamSize
        );

        if (totalTeams == 0) {
            System.out.println("Not enough leaders/thinkers to form teams.");
            return new Result(validTeams, unfinishedTeams, participants);
        }

        Collections.shuffle(leaders);
        Collections.shuffle(thinkers);
        Collections.shuffle(balanced);

        for (int i = 0; i < totalTeams; i++) teams.add(new Team(i + 1));

        // --- Add Leader ---
        for (int i = 0; i < totalTeams; i++) {
            Participant p = leaders.remove(0);
            if (teams.get(i).canAddGame(p.getPreferredGame()) && canAddRole(teams.get(i), p))
                teams.get(i).addMember(p);
            else balanced.add(p);
        }

        // --- Add Thinker ---
        for (int i = 0; i < totalTeams; i++) {
            Participant p = thinkers.remove(0);
            if (teams.get(i).canAddGame(p.getPreferredGame()) && canAddRole(teams.get(i), p))
                teams.get(i).addMember(p);
            else balanced.add(p);
        }

        // Optional 2nd thinker
        for (int i = 0; i < totalTeams; i++) {
            if (!thinkers.isEmpty()) {
                Participant p = thinkers.get(0);
                if (teams.get(i).canAddGame(p.getPreferredGame()) && canAddRole(teams.get(i), p)) {
                    teams.get(i).addMember(p);
                    thinkers.remove(0);
                }
            }
        }

        // Fill with Balanced
        int idx = 0;
        for (Participant b : balanced) {
            Team t = teams.get(idx);

            if (t.size() < teamSize &&
                    t.canAddGame(b.getPreferredGame()) &&
                    canAddRole(t, b)) {
                t.addMember(b);
            } else {
                unassigned.add(b);
            }

            idx = (idx + 1) % totalTeams;
        }

        // ==== FINAL SEPARATION ====
        for (Team t : teams) {
            if (t.getUniqueRoleCount() >= 3) validTeams.add(t);
            else unfinishedTeams.add(t);
        }

        return new Result(validTeams, unfinishedTeams, unassigned);
    }
}
