import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        ArrayList<Participant> allParticipants = new ArrayList<>();
        ArrayList<Team> formedTeams = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        int option;

        do {
            System.out.println("\n" +
                    "1. Participant Enrollment\n" +
                    "2. Import Participants data\n" +
                    "3. Form Teams \n" +
                    "4. Save Teams\n" +
                    "0. Exit");

            option = getOption(scanner);

            System.out.println("________________________");

            switch (option) {

                case 1:
                    System.out.print("Enter name: ");
                    String name = scanner.nextLine().trim();
                    if (name.isEmpty()) { System.out.println("Name cannot be empty."); break; }

                    System.out.print("Enter email: ");
                    String email = scanner.nextLine().trim();
                    if (email.isEmpty()) { System.out.println("Email cannot be empty."); break; }

                    System.out.print("Enter preferred game: ");
                    String preferredGame = scanner.nextLine().trim();
                    if (preferredGame.isEmpty()) { System.out.println("Preferred game cannot be empty."); break; }

                    System.out.print("Enter skill level (1–10): ");
                    String skillStr = scanner.nextLine().trim();
                    if (skillStr.isEmpty()) { System.out.println("Skill level cannot be empty."); break; }
                    int skillLevel;

                    try {
                        skillLevel = Integer.parseInt(skillStr);
                        if (skillLevel < 1 || skillLevel > 10) {
                            System.out.println("Skill level must be between 1 and 10.");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Skill must be a number.");
                        break;
                    }

                    int q1 = getRating(scanner, "I enjoy taking the lead during group activities.");
                    int q2 = getRating(scanner, "I prefer analyzing situations and coming up with solutions.");
                    int q3 = getRating(scanner, "I work well with others and enjoy collaboration.");
                    int q4 = getRating(scanner, "I stay calm under pressure and help maintain morale.");
                    int q5 = getRating(scanner, "I make quick decisions and adapt to dynamic situations.");

                    int totalScore = (q1 + q2 + q3 + q4 + q5) * 4;

                    PersonalityClassifier classifier = new PersonalityClassifier();
                    String personalityType = classifier.classify(totalScore);


                    System.out.println("Choose a role:");
                    System.out.println("1. Strategist");
                    System.out.println("2. Attacker");
                    System.out.println("3. Defender");
                    System.out.println("4. Supporter");
                    System.out.println("5. Coordinator");

                    String roleInput = scanner.nextLine().trim();
                    if (roleInput.isEmpty()) { System.out.println("Role cannot be empty."); break; }

                    String preferredRole = null;

                    switch (roleInput) {
                        case "1": preferredRole = "Strategist"; break;
                        case "2": preferredRole = "Attacker"; break;
                        case "3": preferredRole = "Defender"; break;
                        case "4": preferredRole = "Supporter"; break;
                        case "5": preferredRole = "Coordinator"; break;
                        default: System.out.println("Invalid role."); break;
                    }

                    Participant p = new Participant(
                            name, email, preferredGame, skillLevel,
                            preferredRole, totalScore
                    );
                    p.setPersonalityType(personalityType);
                    allParticipants.add(p);
                    System.out.println("\nParticipant created:");
                    System.out.println(p);

                    break;

                case 2:
                    try {
                        System.out.print("Enter file path: ");
                        String path = scanner.nextLine();
                        if (path.isEmpty()) {
                            System.out.println("File path cannot be empty.");
                            break;
                        }

                        ArrayList<Participant> participants = CSVReader.readCSV(path);
                        allParticipants.addAll(participants);
                        System.out.println("Imported " + participants.size() + " participants.");
                    } catch (Exception e) {
                        System.out.println("Error reading CSV file.");
                    }
                    break;
                case 3:
                    if (allParticipants.isEmpty()) {
                        System.out.println("No participants available to form teams.");
                        break;
                    }

                    System.out.print("Enter team size: ");
                    int teamSize = Integer.parseInt(scanner.nextLine());

                    TeamBuilder.Result result = TeamBuilder.buildTeams(allParticipants, teamSize);

                    System.out.println("\n==== VALID TEAMS ====");
                    result.validTeams.forEach(System.out::println);

                    System.out.println("\n==== UNFINISHED TEAMS (failed role/game constraints) ====");
                    result.unfinishedTeams.forEach(System.out::println);

                    System.out.println("\n==== UNASSIGNED PARTICIPANTS ====");
                    result.unassigned.forEach(p2 -> System.out.println(" - " + p2));

                    break;   // <-- IMPORTANT

                case 4:
                    if (formedTeams.isEmpty()) {
                        System.out.println("No teams formed yet. Please form teams first.");
                        break;
                    }

                    try {
                        CSVWriter.saveTeams("teams.csv", formedTeams);
                    } catch (Exception e) {
                        System.out.println("Error saving CSV: " + e.getMessage());
                    }
                    break;

                case 0:
                    System.out.println("Exiting...");

                    break;

                default:
                    System.out.println("Feature not implemented yet.");
            }

        } while (option != 0);

        scanner.close();
    }


    private static int getOption(Scanner scanner) {
        while (true) {
            System.out.print("Enter your option: ");
            String input = scanner.nextLine().trim();

            try {
                int value = Integer.parseInt(input);
                if (value >= 0 && value <= 7)
                    return value;
                else
                    System.out.println("Please enter a number in the range 0–7.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
            }
        }
    }

    public static int getRating(Scanner scanner, String question) {
        while (true) {
            System.out.println("\nRate 1 (Strongly Disagree) to 5 (Strongly Agree)");
            System.out.println(question);
            System.out.print("Your rating: ");

            String input = scanner.nextLine().trim();

            try {
                int rating = Integer.parseInt(input);
                if (rating >= 1 && rating <= 5)
                    return rating;
            } catch (NumberFormatException ignored) {}

            System.out.println("Please enter a number between 1 and 5.");
        }
    }
}
