import java.util.Scanner;

public class Login {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "1234";

    public static boolean login(Scanner scanner) {
        System.out.println("=== Login ===");
        System.out.print("Username: ");
        String user = scanner.nextLine();

        System.out.print("Password: ");
        String pass = scanner.nextLine();

        return user.equals(USERNAME) && pass.equals(PASSWORD);
    }
}
