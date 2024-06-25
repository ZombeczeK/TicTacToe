import java.io.*;
import java.net.*;
import java.util.concurrent.locks.*;

public class OXS {
    private static char[][] board = new char[3][3];
    private static final Lock lock = new ReentrantLock();
    private static boolean playerOneTurn = true;
    private static DataOutputStream player1Output;
    private static DataOutputStream player2Output;
    public static int moves = 0;
    public static boolean wygranko = false;
    public static int koniec = 0;
    public static int counter = 0;
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Serwer czeka na polaczenie...");

        Socket player1Socket = serverSocket.accept();
        System.out.println("Gracz 1 polaczony.");

        DataInputStream player1Input = new DataInputStream(player1Socket.getInputStream());
        player1Output = new DataOutputStream(player1Socket.getOutputStream());
        player1Output.writeUTF("Gracz 1 polaczony. Czekaj na przeciwnika...");

        Socket player2Socket = serverSocket.accept();
        System.out.println("Gracz 2 polaczony.");

        DataInputStream player2Input = new DataInputStream(player2Socket.getInputStream());
        player2Output = new DataOutputStream(player2Socket.getOutputStream());
        player2Output.writeUTF("Gracz 2 polaczony. Mozesz zaczac gre.");

        new Thread(() -> handlePlayer(player1Input, player1Output, 'X')).start();
        new Thread(() -> handlePlayer(player2Input, player2Output, 'O')).start();
    }

    private static void handlePlayer(DataInputStream input, DataOutputStream output, char symbol) {
        try {
            output.writeUTF("Czekaj na ruch przeciwnika..."); 

            while (true) {
                if(wygranko && koniec == 0){
                    output.writeUTF("Przegranko!");
                    koniec++;
                }
                if (moves >= 9 && koniec == 0){
                    output.writeUTF("Gra zakonczona remisem!");
                    koniec++;
                }
                if(koniec != 0){
                    output.writeUTF("Jezeli chcesz zagrac jeszcze raz wpisz 0");
                    koniec = input.readInt();
                    if(koniec == 0){
                        wygranko = false;
                        moves = 0;
                        reset();
                    }
                    else{
                        return;
                    }
                }
                if (symbol == 'X' && playerOneTurn || symbol == 'O' && !playerOneTurn) {
                    output.writeUTF("Twoja kolej");

                    try {
                        int row = input.readInt();
                        int col = input.readInt();

                        lock.lock();
                        if (board[row][col] == '\0') {
                            board[row][col] = symbol;
                            playerOneTurn = !playerOneTurn;
                            broadcastBoard();
                        } else {
                            output.writeUTF("Nieprawidlowy ruch, sprobuj ponownie.");
                        }
                    }
                    catch (Exception e) {
                        output.writeUTF("Nieprawidlowy ruch, sprobuj ponownie.");
                        moves--;
                    }
                    finally {
                        lock.unlock();
                        moves++;
                        if(board[0][0] == symbol && board[0][1] == symbol && board[0][2] == symbol) {
                            output.writeUTF("Wygranko!");
                            wygranko = true;
                        }
                        if(board[1][0] == symbol && board[1][1] == symbol && board[1][2] == symbol) {
                            output.writeUTF("Wygranko!");
                            wygranko = true;
                        }
                        if(board[2][0] == symbol && board[2][1] == symbol && board[2][2] == symbol) {
                            output.writeUTF("Wygranko!");
                            wygranko = true;
                        }
                        if(board[0][0] == symbol && board[1][0] == symbol && board[2][0] == symbol) {
                            output.writeUTF("Wygranko!");
                            wygranko = true;
                        }
                        if(board[0][1] == symbol && board[1][1] == symbol && board[2][1] == symbol) {
                            output.writeUTF("Wygranko!");
                            wygranko = true;
                        }
                        if(board[0][2] == symbol && board[1][2] == symbol && board[2][2] == symbol) {
                            output.writeUTF("Wygranko!");
                            wygranko = true;
                        }
                        if(board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol) {
                            output.writeUTF("Wygranko!");
                            wygranko = true;
                        }
                        if(board[0][2] == symbol && board[1][1] == symbol && board[2][0] == symbol) {
                            output.writeUTF("Wygranko!");
                            wygranko = true;
                        }
                        if(wygranko){
                            koniec++;
                        }
                    }
                } else {
                    counter++;
                    if(counter == 100){
                    output.writeUTF("Czekaj na ruch przeciwnika");
                    counter = 0;
                    }
                    Thread.sleep(100);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void broadcastBoard() {
        try {
            StringBuilder boardState = new StringBuilder();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    boardState.append(board[i][j] == '\0' ? '-' : board[i][j]);
                }
                boardState.append("\n");
            }
            String boardString = boardState.toString();
            player1Output.writeUTF(boardString);
            player2Output.writeUTF(boardString);
        } catch (IOException e) {
                e.printStackTrace();
        }
    }
    private static void reset(){

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    board[i][j] = '\0';
                }
            }
    }
}
