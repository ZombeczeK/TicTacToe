import java.io.*;
import java.net.*;
import java.util.Scanner;

public class OX {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Podaj adres serwera: ");
        String serverAddress = scanner.nextLine();

        Socket socket = new Socket(serverAddress, 12345);
        DataInputStream input = new DataInputStream(socket.getInputStream());
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());

        new Thread(() -> {
            try {
                while (true) {
                    String message = input.readUTF();
                    System.out.println(message);
                    if (message.equals("Twoja kolej")) {
                        System.out.print("Podaj wiersz (0-2): ");
                        int row = scanner.nextInt();
                        System.out.print("Podaj kolumne (0-2): ");
                        int col = scanner.nextInt();

                        output.writeInt(row);
                        output.writeInt(col);
                    } else if (message.equals("Czekaj na ruch przeciwnika...")) {
                        Thread.sleep(100); // Dodanie opóźnienia, aby nie powtarzać zbyt szybko
                    }
                      else if (message.startsWith("Jezeli chcesz zagrac")) {
                        int choice = scanner.nextInt();
                        output.writeInt(choice);
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
