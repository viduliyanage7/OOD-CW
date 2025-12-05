import java.util.Scanner;

public class Login {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "1234";

    public static boolean login(Scanner scanner) {
        System.out.println("\nLogin\n");

        String user;
        while (true) {
            System.out.print("Username: ");
            user = scanner.nextLine().trim();
            if (!user.isEmpty()) break;
            System.out.println("Username cannot be empty. Try again.");
        }

        String pass;
        while (true) {
            System.out.print("Password: ");
            pass = scanner.nextLine().trim();
            if (!pass.isEmpty()) break;
            System.out.println("Password cannot be empty. Try again.");
        }

        return user.equals(USERNAME) && pass.equals(PASSWORD);
    }
}
