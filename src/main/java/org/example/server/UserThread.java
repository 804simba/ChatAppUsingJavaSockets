package org.example.server;

import org.example.server.ChatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 * This code represents a single thread that handles the connection between the server and a single client.
 * To enable the server handle multiple clients simultaneously.
 *
 * The `Socket` represents the socket connection between the server and a single client.
 * While the chatServer object represents the server object that manages all clients connections.
 *
 * The getInputStream() and getOutputStream() methods are used to obtain the input and output streams of the
 * socket connection.
 *
 * A BufferedReader is used to read input from the client, while the PrintWriter is used to write output to
 * the client.
 * */
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
    // sends a list of online users to the newly connected user.
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
