import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("127.0.0.1", 1211)) {
            System.out.println("Client connected: " + socket.getInetAddress());
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter username:");
            String username = scanner.nextLine();
            System.out.println("Enter password:");
            String password = scanner.nextLine();

            PrintStream output = new PrintStream(socket.getOutputStream());
            output.println(username + " " + password);

            while (true) {
                System.out.println("Enter command:");
                String command = scanner.nextLine();
                if (command.equals("exit")) {
                    break;
                }
                output.println(command);
            }
        }
    }

}
