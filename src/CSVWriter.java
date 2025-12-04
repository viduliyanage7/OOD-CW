import java.io.FileWriter;
import java.util.List;

public class CSVWriter {

    public static void saveTeams(String path, List<Team> teams) throws Exception {

        FileWriter fw = new FileWriter(path);
        fw.write("Team, Name, Email, Game, Role, Skill, Personality\n");

        for (Team t : teams) {
            for (Participant p : t.getMembers()) {
                fw.write(t.getTeamId() + "," +
                        p.getName() + "," +
                        p.getEmail() + "," +
                        p.getPreferredGame() + "," +
                        p.getPreferredRole() + "," +
                        p.getSkillLevel() + "," +
                        p.getPersonalityType() + "\n");
            }
        }

        fw.close();
    }
}
