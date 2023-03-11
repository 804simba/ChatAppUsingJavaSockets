package org.example.client;

import lombok.Data;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

@Data
public class ChatClient {
    private String hostname;
    private int port;
    private String username;

    public ChatClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);

            System.out.println("Connected to the chat server.");
            new ReadThread(socket, this).start();
            new WriteThread(socket, this).start();
        }
        catch (UnknownHostException e) {
            System.err.println("Host not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            return;
        }
        String hostname = args[2];
        int port = Integer.parseInt(args[3]);

        ChatClient client = new ChatClient(hostname, port);
        client.execute();
    }
}
