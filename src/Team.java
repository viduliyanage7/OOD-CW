import java.util.*;

public class Team {

    private int teamId;
    private ArrayList<Participant> members;

    private Map<String, Integer> gameCount;
    private Map<String, Integer> roleCount;
    private Map<String, Integer> personalityCount;
    private int totalSkill;


    public Team(int teamId) {
        this.teamId = teamId;
        this.members = new ArrayList<>();
        this.gameCount = new HashMap<>();
        this.roleCount = new HashMap<>();
        this.personalityCount = new HashMap<>();
        this.totalSkill = 0;
    }

    public int getTeamId() { return teamId; }
    public ArrayList<Participant> getMembers() { return members; }
    public int size() { return members.size(); }

    // ---- RULE CHECKERS ----

    public boolean canAddGame(String game) {
        return gameCount.getOrDefault(game, 0) < 2;
    }

    public boolean canAddLeader() {
        return personalityCount.getOrDefault("Leader", 0) < 1;
    }

    public boolean canAddThinker() {
        return personalityCount.getOrDefault("Thinker", 0) < 2;
    }

    // ---- ADD MEMBER ----

    public void addMember(Participant p) {
        members.add(p);

        totalSkill += p.getSkillLevel();
        gameCount.merge(p.getPreferredGame(), 1, Integer::sum);
        roleCount.merge(p.getPreferredRole(), 1, Integer::sum);
        personalityCount.merge(p.getPersonalityType(), 1, Integer::sum);
    }

    // ---- SKILL ----

    public double getTeamAverageSkill() {
        if (members.size() == 0) return 0;
        return (double) totalSkill / members.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\nTeam " + teamId + ":\n");
        sb.append(" Avg Skill: ").append(String.format("%.2f", getTeamAverageSkill())).append("\n");
        sb.append(" Roles: ").append(roleCount).append("\n");
        sb.append(" Personality: ").append(personalityCount).append("\n");
        sb.append(" Games: ").append(gameCount).append("\n");
        sb.append(" Members:\n");

        for (Participant p : members) {
            sb.append("  - ").append(p).append("\n");
        }

        return sb.toString();
    }
}
