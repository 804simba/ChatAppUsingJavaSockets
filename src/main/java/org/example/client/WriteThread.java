package org.example.client;

import java.io.Console;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class WriteThread extends Thread {
    private PrintWriter output;
    private final Socket socket;
    private final ChatClient client;

    public WriteThread(Socket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;
        try {
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Error getting output stream.");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
//        Console console = System.console();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your username >>>>>> ");
        String username = scanner.nextLine();

        client.setUsername(username);
        output.println(username);

        String text;

        do {
            System.out.print("[" + username + "] >>>> ");
            text = scanner.nextLine();
            output.println(text);
        } while (!text.equals("bye"));

        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Error writing to server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
