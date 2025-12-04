import java.io.*;
import java.util.*;

public class CSVReader {

    public static ArrayList<Participant> readCSV(String filePath) throws Exception {

        ArrayList<Participant> participants = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filePath));

        String line;

        br.readLine();

        while ((line = br.readLine()) != null) {

            String[] data = line.split(",");

            System.out.println(Arrays.toString(data));

            try {
                Participant m = new Participant(
                        data[1],
                        data[2],
                        data[3],
                        Integer.parseInt(data[4]),
                        data[5],
                        Integer.parseInt(data[6])
                );

                if (data.length > 7) {
                    m.setPersonalityType(data[7]);
                }

                participants.add(m);

            } catch (Exception e) {
                System.out.println("Invalid row ignored: " + line);
            }
        }

        br.close();
        return participants;
    }
}
