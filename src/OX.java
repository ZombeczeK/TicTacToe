import java.io.*;
import java.net.*;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

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
                        String row, col;
                        int row_i, col_i;
                        try{
                            System.out.print("Podaj wiersz (0-2): ");
                            row = scanner.nextLine();
                            System.out.print("Podaj kolumne (0-2): ");
                            col = scanner.nextLine();
                            row_i = parseInt(row);
                            col_i = parseInt(col);
                        }
                        catch(Exception e){
                            row_i = 3;
                            col_i = 3;
                        }
                        output.writeInt(row_i);
                        output.writeInt(col_i);
                    } else if (message.equals("Czekaj na ruch przeciwnika...")) {
                        Thread.sleep(100);
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
