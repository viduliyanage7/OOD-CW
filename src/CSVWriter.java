import java.io.FileWriter;
import java.util.List;

public class CSVWriter {

    public static void saveTeams(String path, List<Team> teams) throws Exception {
        FileWriter fw = new FileWriter(path);

        fw.write("Team,Name,Game,Role,Skill,Personality\n");

        for(Team t : teams) {
            for(Member m : t.getMembers()) {
                fw.write(t.toString().split(":")[0] + "," +
                        m.getName() + "," +
                        m.getGame() + "," +
                        m.getRole() + "," +
                        m.getSkillLevel() + "," +
                        m.getPersonalityType() + "\n");
            }
        }

        fw.close();
    }
}
