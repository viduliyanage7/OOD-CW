// LoginManager.java
import java.util.Scanner;

public class Login {

    // You can later load these from file or database
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "1234";

    // Handles login process
    public static boolean login(Scanner scanner) {
        System.out.println("=== Login ===");
        System.out.print("Username: ");
        String user = scanner.nextLine();

        System.out.print("Password: ");
        String pass = scanner.nextLine();

        return user.equals(USERNAME) && pass.equals(PASSWORD);
    }
}
