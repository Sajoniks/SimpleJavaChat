package com.sajoniks.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Queue;

public class Server {

    private static final int PORT = 8080;
    private static final int MAX_CONNECTIONS = 16;
    private static final Queue<ClientThread> Connections = new ArrayDeque<>(MAX_CONNECTIONS);

    public static void main(String[] args) {
        try(ServerSocket s = new ServerSocket(PORT)) {
            while(true) {
                try {
                    Socket sock = s.accept();
                    ClientThread clientThread = new ClientThread(sock);
                    Thread thread = new Thread(clientThread);

                    Connections.add(clientThread);

                    thread.start();
                }
                catch (Exception e) {

                }
            }

        }
        catch (IOException e) {
            System.out.printf("Failed to start server at port %d%n", PORT);
        }
    }

    public static void onClientExit(ClientThread th) {
        synchronized(Connections) {
            Connections.remove(th);
        }
    }

    private static void multicastMessage(ClientThread th, String message, boolean receiveSelf) {
        synchronized (Connections) {
            for (ClientThread con : Connections) {
                if (con == th && !receiveSelf) continue;
                con.receiveMulticastMessage(th, message);
            }
        }
    }

    public static void onClientMessageMulticast(ClientThread th, String message, boolean receiveSelf) {
        multicastMessage(th, String.format("%s > %s", th.getNickname(), message), receiveSelf);
    }

    public static void onWelcomeUserMulticast(ClientThread th) {
        multicastMessage(th, String.format("%s entered chat!", th.getNickname()), false);
    }
}
