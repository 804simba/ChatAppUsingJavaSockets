package org.example.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserThreadTest {

    private ChatServer chatServer;
    private UserThread userThread1;
    private UserThread userThread2;

    @BeforeEach
    public void setup() {
        chatServer = new ChatServer(5555);
        new Thread(() -> {
            chatServer.execute();
        }).start();

        try {
            Thread.sleep(1000); // wait for server to start
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Socket socket1 = null;
        Socket socket2 = null;
        try {
            socket1 = new Socket("localhost", 5555);
            socket2 = new Socket("localhost", 5555);
        } catch (IOException e) {
            e.printStackTrace();
        }

        userThread1 = new UserThread(socket1, chatServer);
        userThread2 = new UserThread(socket2, chatServer);
        userThread1.start();
        userThread2.start();
    }

    @Test
    public void testUserThread() {
        try {
            BufferedReader input1 = new BufferedReader(new InputStreamReader(userThread1.getSocket().getInputStream()));
            PrintWriter output1 = new PrintWriter(userThread1.getSocket().getOutputStream(), true);

            BufferedReader input2 = new BufferedReader(new InputStreamReader(userThread2.getSocket().getInputStream()));
            PrintWriter output2 = new PrintWriter(userThread2.getSocket().getOutputStream(), true);

            // userThread1 sends a message to userThread2
            output1.println("Hello, userThread2!");

            // check if userThread2 receives the message
            String receivedMessage = input2.readLine();
            assertEquals("Hello, userThread2!", receivedMessage);

            // userThread2 sends a message to userThread1
            output2.println("Hi, userThread1!");

            // check if userThread1 receives the message
            receivedMessage = input1.readLine();
            assertEquals("Hi, userThread1!", receivedMessage);

            // userThread1 sends "bye" to userThread2
            output1.println("bye");

            // check if userThread2 receives the "userThread1 left the chat" message
            receivedMessage = input2.readLine();
            assertEquals("userThread1 left the chat.", receivedMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void tearDown() {
        userThread1.interrupt();
        userThread2.interrupt();
//        chatServer.stop();
    }
}
