import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVWriter {

    public static boolean saveTeams(String path, List<Team> teams) {

        if (path == null || path.trim().isEmpty()) {
            System.out.println("Invalid file path.");
            return false;
        }
        if (teams == null || teams.isEmpty()) {
            System.out.println("No teams available to save.");
            return false;
        }

        try (FileWriter fw = new FileWriter(path)) {

            fw.write("Team,Name,Email,Game,Role,Skill,Personality\n");

            for (Team t : teams) {
                for (Participant p : t.getMembers()) {
                    fw.write(t.getTeamId() + "," +
                            p.getName() + "," +
                            p.getPreferredGame() + "," +
                            p.getPreferredRole() + "," +
                            p.getSkillLevel() + "," +
                            p.getPersonalityType() + "\n");
                }
            }

            System.out.println("Teams saved successfully to file: " + path);
            return true;

        } catch (IOException e) {
            System.out.println("Error writing to file. Check file permissions or path.");
            return false;
        }
    }
}
