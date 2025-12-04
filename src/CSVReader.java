import java.io.*;
import java.util.*;

public class CSVReader {

    public static ArrayList<Participant> readCSV(String filePath) {

        ArrayList<Participant> participants = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {   // handles close automatically

            String line = br.readLine(); // header skip

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length < 7) {
                    System.out.println("⚠ Skipped row (not enough columns): " + line);
                    continue;
                }

                try {
                    Participant p = new Participant(
                            data[1].trim(),
                            data[2].trim(),
                            data[3].trim(),
                            Integer.parseInt(data[4].trim()),
                            data[5].trim(),
                            Integer.parseInt(data[6].trim())
                    );

                    if (data.length > 7 && !data[7].trim().isEmpty())
                        p.setPersonalityType(data[7].trim());

                    participants.add(p);

                } catch (Exception ex) {
                    System.out.println("⚠ Skipped invalid row: " + line);
                }
            }

            System.out.println("✔ Loaded " + participants.size() + " participants.");

        } catch (FileNotFoundException e) {
            System.out.println("❌ File not found: " + filePath);
        } catch (IOException e) {
            System.out.println("❌ Error reading file: " + e.getMessage());
        }

        return participants;
    }
}
