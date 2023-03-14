package org.example.server;

import lombok.Getter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

@Getter
public class UserThread extends Thread {
    private final Socket socket;
    private final ChatServer server;
    private PrintWriter output;

    public UserThread(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }
    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            printUsers();

            String username = input.readLine();
            server.addUsername(username);

            String serverMessage = "New user connected: " + username;
            server.broadCast(serverMessage, this);

            String clientMessage;

            do {
                clientMessage = input.readLine();
                serverMessage = "[" + username + "]: " + clientMessage;
                server.broadCast(serverMessage, this);
            } while (!clientMessage.equals("bye"));

            try {
                server.removeUser(username,this);
                socket.close();

                serverMessage = username + " left the chat.";
                server.broadCast(serverMessage, this);
            } catch (SocketException e) {
                System.err.println("Session ended.");
            }

        } catch (IOException e) {
            System.out.println("User thread got interrupted!" + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("User thread terminated: " + e.getMessage());
            e.printStackTrace();
        }
    }
    void printUsers() {
        if (server.hasUsers()) {
            output.println("Connected users: " + server.getUserNames());
        } else {
            output.println("No other users connected...");
        }
    }
    public void sendMessage(String message) {
        output.println(message);
    }
}
