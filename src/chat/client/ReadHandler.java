package chat.client;

import java.io.DataInputStream;
import java.io.IOException;

import static util.MyLogger.log;

//서버에서 오는 메시지를 계속 읽어서 콘솔에 출력하는 역할
public class ReadHandler implements Runnable {

    private final DataInputStream input;
    private final Client client;
    public boolean closed = false;

    public ReadHandler(DataInputStream input, Client client) {
        this.input = input;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            //서버에서 오는 메시지를 계속 읽음
            while (true) {
                String received = input.readUTF();
                System.out.println(received);
            }
        } catch (IOException e) {
            log(e);
        } finally {
            client.close();
        }
    }

    public synchronized void close() {
        if (closed) {
            return;
        }
        closed = true;
        log("ReadHandler 종료");
    }
}
