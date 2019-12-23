import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int SERVER_PORT = 12367;
    private static Socket waitingSocket;

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(SERVER_PORT);

        while (true) {
            Socket socket = server.accept();
            socket.setKeepAlive(true);
            if (waitingSocket != null) {
                GameHandler handler = new GameHandler(waitingSocket, socket);
                waitingSocket = null;
                handler.start();
            } else {
                waitingSocket = socket;
            }
        }
    }
}
