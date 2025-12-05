import java.util.*;
import java.util.concurrent.*;

public class Main {

    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {

        List<Participant> allParticipants = Collections.synchronizedList(new ArrayList<>());
        List<Team> formedTeams = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        while (true) {

            boolean isLoggedIn = Login.login(scanner); // ask for login

            int option;
            do {
                System.out.println("\nMAIN MENU");
                if (isLoggedIn) {
                    System.out.println("1. Add Participant (Runs in Background)");
                    System.out.println("2. Import Participants from CSV");
                    System.out.println("3. Build Teams");
                    System.out.println("4. Save Teams");
                } else {
                    System.out.println("\n(Login failed — Only option 1 available)");
                    System.out.println("1. Add Participant (Runs in Background)");
                }

                System.out.println("0. Logout & Return to Login");

                System.out.print("Input : ");

                option = getOption(scanner, isLoggedIn);

                switch (option) {

                    // 1. Add Participant (Background Thread)
                    case 1 -> {
                        addParticipant(scanner, allParticipants);
                        System.out.println("Processing participant in background...");
                    }

                    // 2. CSV Import
                    case 2 -> {
                        System.out.print("Enter CSV path: ");
                        String path = scanner.nextLine();

                        executor.submit(() -> {
                            try {
                                ArrayList<Participant> imported = CSVReader.readCSV(path);
                                synchronized (allParticipants) {
                                    allParticipants.addAll(imported);
                                }
                                System.out.println("Imported " + imported.size() + " participants (async)");
                            } catch (Exception e) {
                                System.out.println("Failed to import CSV.");
                            }
                        });
                    }

                    // 3. Build Teams Asynchronously
                    case 3 -> {
                        System.out.print("Enter team size: ");
                        String input = scanner.nextLine();

                        // ----------- VALIDATION ADDED (only additions, nothing removed) -----------
                        if (input == null || input.isBlank()) {
                            System.out.println("Team size cannot be empty.");
                            break;
                        }

                        // validate numeric
                        if (!input.matches("\\d+")) {
                            System.out.println("Invalid input. Please enter a positive number.");
                            break;
                        }

                        int size = Integer.parseInt(input);

                        if (size <= 0) {
                            System.out.println("Team size must be greater than 0.");
                            break;
                        }

                        if (allParticipants.isEmpty()) {
                            System.out.println("No participants available. Add participants first.");
                            break;
                        }
                        // -------------------------------------------------------------------------

                        executor.submit(() -> {
                            synchronized (allParticipants) {
                                // existing check (kept intact)
                                if (allParticipants.size() < size) {
                                    System.out.println("Need more participants first.");
                                    return;
                                }

                                TeamBuilder.Result result =
                                        TeamBuilder.buildTeams(new ArrayList<>(allParticipants), size);

                                formedTeams.clear();
                                formedTeams.addAll(result.teams);

                                System.out.println("\nTeams built asynchronously:");
                                formedTeams.forEach(System.out::println);

                                if (!result.unassigned.isEmpty()) {
                                    System.out.println("\nUnassigned Participants:");
                                    result.unassigned.forEach(p -> System.out.println(" - " + p));
                                }
                            }
                        });
                    }


                    // 4. Save Teams
                    case 4 -> {
                        if (formedTeams.isEmpty()) {
                            System.out.println("No teams to save.");
                            break;
                        }
                        try {
                            CSVWriter.saveTeams("teams.csv", formedTeams);
                            System.out.println("Teams saved to teams.csv");
                        } catch (Exception e) {
                            System.out.println("Failed to save.");
                        }
                        break;
                    }

                    case 0 -> {
                        System.out.println("Logging out... Returning to login.");
                    }
                }

            } while (option != 0);
        }
    }


    public static void addParticipant(Scanner scanner, List<Participant> all) {
        try {
            String name;
            while (true) {
                System.out.print("\nName: ");
                name = scanner.nextLine().trim();

                if (!name.isEmpty()) {
                    break;
                }
                System.out.println("Name cannot be empty. Please enter a valid name.");
            }

            String game;
            while (true) {
                System.out.print("Preferred game: ");
                game = scanner.nextLine().trim();

                if (!game.isEmpty()) {
                    break;
                }
                System.out.println("Preferred game cannot be empty. Please enter a valid game.");
            }


            int skill = getNumber(scanner, "Skill level (1-10): ", 1, 10);

            int q1 = getRating(scanner, "I enjoy taking the lead...");
            int q2 = getRating(scanner, "I analyze situations...");
            int q3 = getRating(scanner, "I collaborate well...");
            int q4 = getRating(scanner, "I stay calm...");
            int q5 = getRating(scanner, "I adapt quickly...");

            Future<Integer> surveyFuture = executor.submit(new SurveyProcessor(q1, q2, q3, q4, q5));
            System.out.println("Processing survey data in background...");

            String role = chooseRole(scanner);
            int totalScore = surveyFuture.get();

            Participant p = new Participant(name, game, skill, role, totalScore);

            synchronized (all) {
                all.add(p);
            }

            System.out.println("Participant added: " + p.getName());
            System.out.println("   Personality Type: " + p.getPersonalityType());

        } catch (Exception e) {
            System.out.println("Failed to register participant.");
            e.printStackTrace();
        }
    }

    private static int getOption(Scanner sc, boolean logged) {
        while (true) {
            try {
                int x = Integer.parseInt(sc.nextLine());
                if (logged && x>=0 && x<=4) return x;
                if (!logged && (x==0||x==1)) return x;
            } catch (Exception ignored) {}
            System.out.println("Invalid option.");
        }
    }

    public static int getRating(Scanner sc, String q) {
        return getNumber(sc, q + " (1-5): ", 1, 5);
    }

    public static int getNumber(Scanner sc, String msg, int min, int max) {
        while(true){
            System.out.print(msg);
            try{
                int x=Integer.parseInt(sc.nextLine());
                if(x>=min && x<=max) return x;
            }catch(Exception ignored){}
            System.out.println("Enter a number "+min+"-"+max);
        }
    }

    public static String chooseRole(Scanner sc) {
        while(true){
            System.out.println("""
                Select role:
                1. Strategist
                2. Attacker
                3. Defender
                4. Supporter
                5. Coordinator
            """);
            System.out.print("Role: ");
            switch(sc.nextLine()){
                case "1": return "Strategist";
                case "2": return "Attacker";
                case "3": return "Defender";
                case "4": return "Supporter";
                case "5": return "Coordinator";
            }
            System.out.println("Invalid role.");
        }
    }
}
