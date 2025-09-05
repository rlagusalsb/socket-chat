package chat.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static util.MyLogger.log;

//사용자가 콘솔에 입력한 내용을 서버로 전송하는 역할
public class WriteHandler implements Runnable {

    private static final String DELIMITER = "|"; //메시지 구분자

    private final DataOutputStream output;
    private final Client client;

    private boolean closed = false;

    public WriteHandler(DataOutputStream output, Client client) {
        this.output = output;
        this.client = client;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        try {
            //처음에 입력을 입력 받아 서버에 전달
            String username = inputUsername(scanner);
            output.writeUTF("/join" + DELIMITER + username);

            //입력을 계속 읽어서 서버로 전송
            while (true) {
                String toSend = scanner.nextLine();

                //빈 입력 무시
                if (toSend.isEmpty()) {
                    continue;
                }

                if (toSend.equals("/exit")) {
                    output.writeUTF(toSend);
                    break;
                }

                // "/"로 시작하면 명령어로 간주
                if (toSend.startsWith("/")) {
                    output.writeUTF(toSend);
                } else {
                    //일반 메시지는 전송
                    output.writeUTF("/message" + DELIMITER + toSend);
                }
            }
        } catch (IOException | NoSuchElementException e) {
            log(e);
        } finally {
            client.close();
        }
    }

    private static String inputUsername(Scanner scanner) {
        System.out.println("이름을 입력하세요.");
        String username;

        do {
            username = scanner.nextLine();
        }
        while (username.isEmpty());

        return username;
    }

    public synchronized void close() {
        if (closed) {
            return;
        }

        try {
            System.in.close();
        } catch (IOException e) {
            log(e);
        }

        closed = true;

        log("WriteHandler 종료");
    }
}
