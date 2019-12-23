import java.io.*;
import java.net.Socket;
import java.util.Random;

public class GameHandler extends Thread {
    private Socket client1, client2;
    private boolean turn;

    GameHandler(Socket client1, Socket client2) {
        this.client1 = client1;
        this.client2 = client2;
    }

    public void run() {
        try {
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));

            BufferedWriter writer1 = new BufferedWriter(new OutputStreamWriter(client1.getOutputStream()));
            BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(client2.getOutputStream()));

            writer1.write(MSG.GAME_START+"\n");
            writer1.flush();
            writer2.write(MSG.GAME_START+"\n");
            writer2.flush();

            String name1 = reader1.readLine();
            String name2 = reader2.readLine();

            writer1.write(name2+"\n");
            writer1.flush();
            writer2.write(name1+"\n");
            writer2.flush();

            Random random = new Random();
            this.turn = random.nextBoolean();
            if (turn) {
                writer1.write(MSG.FIRST_TURN+"\n");
                writer1.flush();
                writer2.write(MSG.SECOND_TURN+"\n");
                writer2.flush();
            } else {
                writer2.write(MSG.FIRST_TURN+"\n");
                writer2.flush();
                writer1.write(MSG.SECOND_TURN+"\n");
                writer1.flush();
            }

            while (true) {
                if (turn) {
                    String input = reader1.readLine();
                    writer2.write(input+"\n");
                    writer2.flush();
                } else {
                    String input = reader2.readLine();
                    writer1.write(input+"\n");
                    writer1.flush();
                }
                turn = !turn;
            }
        } catch (IOException e) {
            System.out.printf("IOException:\n%s\n", e.getMessage());
        }
    }
}
