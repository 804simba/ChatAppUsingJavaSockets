package org.example.server;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.net.Socket;

public class ChatServerTest {

    private ChatServer chatServer;

    @BeforeEach
    void setUp() {
        chatServer = new ChatServer(8080);
    }

    @Test
    @DisplayName("Test the addition of a new username")
    void testAddUsername() {
        chatServer.addUsername("John");
        chatServer.addUsername("Doe");
        Assertions.assertEquals(2, chatServer.getUserNames().size());
    }

    @Test
    @DisplayName("Test the removal of a user")
    void testRemoveUser() {
        UserThread userThread = new UserThread(new Socket(), chatServer);
        chatServer.addUsername("John");
        chatServer.addUsername("Doe");
        chatServer.removeUser("John", userThread);
        Assertions.assertEquals(1, chatServer.getUserNames().size());
    }

    @Test
    @DisplayName("Test whether there are users connected")
    void testHasUsers() {
        chatServer.addUsername("John");
        Assertions.assertTrue(chatServer.hasUsers());
    }
}
