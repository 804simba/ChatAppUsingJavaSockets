package org.example.server;

import lombok.Getter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

@Getter
public class ChatServer {
    private final int port;
    private final Set<String> userNames;
    private final Set<UserThread> userThreads;

    public ChatServer(int port) {
        this.port = port;
        this.userNames = new HashSet<>();
        this.userThreads = new HashSet<>();
    }
    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Chat server is listening on port: " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New user connected >>>>");

                UserThread newUser = new UserThread(socket, this);
                userThreads.add(newUser);
                newUser.start();
            }
        } catch (IOException e) {
            System.out.println("Error occurred in the server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Syntax: java ChatServer <port-number>");
            System.exit(0);
        }

        int port = Integer.parseInt(args[2]);
        ChatServer server = new ChatServer(port);
        server.execute();
    }
    public void broadCast (String message, UserThread sender) {
        for (UserThread userThread : userThreads) {
            if (!userThread.equals(sender)) {
                userThread.sendMessage(message);
            }
        }
    }
    public void addUsername(String userName) {
        userNames.add(userName);
    }
    public void removeUser(String userName, UserThread userThread) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(userThread);
            System.out.println(userName + " session terminated.");
        }
    }
    public boolean hasUsers() {
        return !this.userNames.isEmpty();
    }
}
