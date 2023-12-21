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

            // Send username and password to server
            PrintStream output = new PrintStream(socket.getOutputStream());
            output.println(username + " " + password);

            // Create a separate thread to continuously receive server responses
            startResponseReaderThread(socket);

            while (true) {
                String command = scanner.nextLine();
                if (command.equals("exit")) {
                    break;
                }
                // Send command to server
                output.println(command);
            }
        }
    }

    private static void startResponseReaderThread(Socket socket) {
        Thread responseThread = new Thread(() -> {
            try {
                Scanner serverResponse = new Scanner(socket.getInputStream());
                while (serverResponse.hasNextLine()) {
                    String response = serverResponse.nextLine();
                    System.out.println(response);
                }
            } catch (IOException e) {
                System.out.println("Server disconnected");
            }
        });
        responseThread.start();
    }
}
