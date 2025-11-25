import java.util.ArrayList;

public class Team {
    private ArrayList<Member> members = new ArrayList<>();
    private String gameType;

    public Team(String gameType) {
        this.gameType = gameType;
    }

    public void addMember(Member m) {
        members.add(m);
    }

    public ArrayList<Member> getMembers() { return members; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Team (" + gameType + "):\n");
        for (Member m : members) {
            sb.append("   - ").append(m.toString()).append("\n");
        }
        return sb.toString();
    }
}
