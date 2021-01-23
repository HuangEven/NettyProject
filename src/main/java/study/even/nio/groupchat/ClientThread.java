package study.even.nio.groupchat;

import java.io.IOException;

import static java.lang.Thread.sleep;

public class ClientThread implements Runnable {
    private GroupChatClient groupChatClient;

    public ClientThread(GroupChatClient client) {
        groupChatClient = client;
    }

    @Override
    public void run() {
        while(true){
            try {
                groupChatClient.readInfo();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                sleep(3000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }
}
