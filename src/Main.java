import java.util.*;
import java.util.concurrent.*;

public class Main {

    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {

        List<Participant> allParticipants = Collections.synchronizedList(new ArrayList<>());
        List<Team> formedTeams = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        boolean isLoggedIn = Login.login(scanner);

        int option;
        do {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. Add Participant (Runs in Background)");

            if (isLoggedIn) {
                System.out.println("2. Import Participants from CSV");
                System.out.println("3. Build Teams (Async)");
                System.out.println("4. Save Teams");
            } else {
                System.out.println("(Login failed — Only option 1 available)");
            }
            System.out.println("0. Exit");

            option = getOption(scanner, isLoggedIn);

            switch (option) {

                //---------------------------------------------------------
                // 1. PARTICIPANT INPUT (MULTITHREADED)
                //---------------------------------------------------------
                case 1 -> {
                    addParticipant(scanner, allParticipants);
                    System.out.println("✔ Processing participant in background...");
                }

                //---------------------------------------------------------
                // 2. CSV IMPORT
                //---------------------------------------------------------
                case 2 -> {
                    System.out.print("Enter CSV path: ");
                    String path = scanner.nextLine();

                    executor.submit(() -> {
                        try {
                            ArrayList<Participant> imported = CSVReader.readCSV(path);
                            synchronized (allParticipants) {
                                allParticipants.addAll(imported);
                            }
                            System.out.println("✔ Imported " + imported.size() + " participants (async)");
                        } catch (Exception e) {
                            System.out.println("❌ Failed to import CSV.");
                        }
                    });
                }

                //---------------------------------------------------------
                // 3. TEAM BUILDING (ASYNC MODE)
                //---------------------------------------------------------
                case 3 -> {
                    System.out.print("Enter team size: ");
                    int size = Integer.parseInt(scanner.nextLine());

                    executor.submit(() -> {
                        synchronized (allParticipants) {
                            if (allParticipants.size() < size) {
                                System.out.println("⚠ Need more participants first.");
                                return;
                            }

                            TeamBuilder.Result result =
                                    TeamBuilder.buildTeams(new ArrayList<>(allParticipants), size);

                            formedTeams.clear();
                            formedTeams.addAll(result.teams);

                            System.out.println("\n🔥 Teams built asynchronously:");
                            formedTeams.forEach(System.out::println);

                            if (!result.unassigned.isEmpty()) {
                                System.out.println("\nUnassigned Participants:");
                                result.unassigned.forEach(p -> System.out.println(" - " + p));
                            }
                        }
                    });
                }

                //---------------------------------------------------------
                // 4. SAVE TEAMS
                //---------------------------------------------------------
                case 4 -> {
                    if (formedTeams.isEmpty()) {
                        System.out.println("⚠ No teams to save.");
                        break;
                    }
                    try {
                        CSVWriter.saveTeams("teams.csv", formedTeams);
                        System.out.println("💾 Teams saved to teams.csv");
                    } catch (Exception e) {
                        System.out.println("❌ Failed to save.");
                    }
                }

                //---------------------------------------------------------
                // 0. EXIT
                //---------------------------------------------------------
                case 0 -> {
                    executor.shutdown();
                    System.out.println("Exiting... Background tasks finishing.");
                }
            }

        } while (option != 0);

        scanner.close();
    }

    // ================== INPUT HANDLER (Runs in Thread) ======================
    public static void addParticipant(Scanner scanner, List<Participant> all) {
        try {
            System.out.print("\nName: ");
            String name = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("Preferred game: ");
            String game = scanner.nextLine();

            int skill = getNumber(scanner, "Skill level (1-10): ", 1, 10);

            // ====== Collect survey answers ======
            int q1 = getRating(scanner, "I enjoy taking the lead...");
            int q2 = getRating(scanner, "I analyze situations...");
            int q3 = getRating(scanner, "I collaborate well...");
            int q4 = getRating(scanner, "I stay calm...");
            int q5 = getRating(scanner, "I adapt quickly...");

            // ====== PROCESS SURVEY IN BACKGROUND THREAD ======
            Future<Integer> surveyFuture = executor.submit(new SurveyProcessor(q1, q2, q3, q4, q5));

            System.out.println("⏳ Processing survey data in background...");

            // Continue asking input while processing survey
            String role = chooseRole(scanner);

            // Get processed personality score from background thread
            int totalScore = surveyFuture.get(); // <-- waits only if needed

            // Create participant
            Participant p = new Participant(name, email, game, skill, role, totalScore);

            synchronized (all) {
                all.add(p);
            }

            System.out.println("✔ Participant added: " + p.getName());
            System.out.println("   Personality Type: " + p.getPersonalityType());

        } catch (Exception e) {
            System.out.println("❌ Failed to register participant.");
            e.printStackTrace();
        }
    }


    // ===================== UTIL METHODS =====================
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
