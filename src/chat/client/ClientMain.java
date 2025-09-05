package chat.client;

import java.io.IOException;

//클라이언트를 실행하는 클래스
public class ClientMain {

    //서버와 동일해야 연결 가능
    private static final int PORT = 12345;

    public static void main(String[] args) throws IOException {
        Client client = new Client("localhost", PORT);
        client.start();
    }
}
