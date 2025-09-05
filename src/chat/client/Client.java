package chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static chat.tcp.SocketCloseUtil.closeAll;
import static util.MyLogger.log;

//서버에 접속해서 메시지를 주고받는 클라이언트
public class Client {

    private final String host; //서버 주소
    private final int port; //포트 번호

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    private ReadHandler readHandler;
    private WriteHandler writeHandler;
    private boolean closed = false;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws IOException {
        log("클라이언트 시작");
        socket = new Socket(host, port);

        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());

        readHandler = new ReadHandler(input, this);
        writeHandler = new WriteHandler(output, this);

        //각 핸들러를 스레드로 실행
        Thread readThread = new Thread(readHandler, "ReadHandler");
        Thread writeThread = new Thread(writeHandler, "WriteHandler");

        readThread.start();
        writeThread.start();
    }

    public synchronized void close() {
        if (closed) {
            return;
        }

        writeHandler.close();
        readHandler.close();

        closeAll(socket, input, output);

        closed = true;

        log("연결 종료: " + socket);
    }
}
