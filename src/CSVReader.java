import java.io.*;
import java.util.*;

public class CSVReader {

    public static ArrayList<Member> readCSV(String filePath) throws Exception {
        ArrayList<Member> members = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;

        br.readLine(); // skip header

        while((line = br.readLine()) != null) {
            String[] data = line.split(",");
            try {
                Member p = new Member(
                        data[0],
                        data[1],
                        data[2],
                        Integer.parseInt(data[3]),
                        Integer.parseInt(data[4])
                );
                members.add(p);
            } catch (Exception e) {
                System.out.println("Invalid row ignored: " + line);
            }
        }
        br.close();
        return members;
    }
}
