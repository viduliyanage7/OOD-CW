import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        ArrayList<Participant> allParticipants = new ArrayList<>();
        ArrayList<Team> formedTeams = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        // ===== LOGIN CHECK =====
        boolean isLoggedIn = Login.login(scanner);

        int option;

        do {
            // ===== MENU DISPLAY =====
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. Participant Enrollment");

            if (isLoggedIn) {
                System.out.println("2. Import Participants data");
                System.out.println("3. Form Teams");
                System.out.println("4. Save Teams");
            } else {
                System.out.println("(Login failed - only option 1 available)");
            }

            System.out.println("0. Exit");

            option = getOption(scanner, isLoggedIn);
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
                    System.out.println("1. Strategist\n2. Attacker\n3. Defender\n4. Supporter\n5. Coordinator");
                    String roleInput = scanner.nextLine().trim();
                    String preferredRole = switch (roleInput) {
                        case "1" -> "Strategist";
                        case "2" -> "Attacker";
                        case "3" -> "Defender";
                        case "4" -> "Supporter";
                        case "5" -> "Coordinator";
                        default -> { System.out.println("Invalid role."); yield null; }
                    };
                    if (preferredRole == null) break;

                    Participant p = new Participant(name, email, preferredGame, skillLevel, preferredRole, totalScore);
                    p.setPersonalityType(personalityType);
                    allParticipants.add(p);

                    System.out.println("\nParticipant created:");
                    System.out.println(p);
                    break;

                case 2:
                    System.out.print("Enter file path: ");
                    try {
                        String path = scanner.nextLine();
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
                    formedTeams = result.teams;

                    System.out.println("\n==== FINISHED TEAMS ====");
                    if (formedTeams.isEmpty()) {
                        System.out.println("No complete teams could be formed.");
                    } else {
                        formedTeams.forEach(System.out::println);
                    }

                    System.out.println("\n==== UNASSIGNED PARTICIPANTS ====");
                    if (result.unassigned.isEmpty()) {
                        System.out.println("None");
                    } else {
                        result.unassigned.forEach(u -> System.out.println(" - " + u));
                    }
                    break;

                case 4:
                    if (formedTeams.isEmpty()) {
                        System.out.println("No teams formed yet.");
                        break;
                    }
                    try {
                        CSVWriter.saveTeams("teams.csv", formedTeams);
                        System.out.println("Teams saved.");
                    } catch (Exception e) {
                        System.out.println("Error saving CSV.");
                    }
                    break;

                case 0:
                    System.out.println("Exiting...");
                    break;
            }

        } while (option != 0);

        scanner.close();
    }

    private static int getOption(Scanner scanner, boolean isLoggedIn) {
        while (true) {
            System.out.print("Enter option: ");
            try {
                int value = Integer.parseInt(scanner.nextLine());

                if (isLoggedIn && value >= 0 && value <= 4) return value;
                if (!isLoggedIn && (value == 0 || value == 1)) return value;

                System.out.println("Invalid option.");
            } catch (Exception e) {
                System.out.println("Enter a valid number.");
            }
        }
    }

    public static int getRating(Scanner scanner, String question) {
        while (true) {
            System.out.println("\nRate 1-5");
            System.out.println(question);
            System.out.print("Your rating: ");
            try {
                int r = Integer.parseInt(scanner.nextLine());
                if (r >= 1 && r <= 5) return r;
            } catch (Exception ignored) {}
            System.out.println("Enter number 1-5.");
        }
    }
}
