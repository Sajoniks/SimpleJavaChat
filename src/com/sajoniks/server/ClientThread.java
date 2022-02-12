package com.sajoniks.server;

import com.sun.istack.internal.NotNull;
import com.sun.security.ntlm.Client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.UUID;

public class ClientThread implements Runnable{

    private Socket socket;
    private PrintWriter out;
    private String nickname;

    public ClientThread(@NotNull Socket clientSock) {
        this.socket = clientSock;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public void run() {
        try(
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream()
        )
        {
            try(Scanner in = new Scanner(new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
            {
                out = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);
                out.println("Connection established");
                out.println("Enter your nickname:");

                while(in.hasNextLine()) {
                    nickname = in.nextLine().trim();
                    if (!nickname.isEmpty()) break;
                }
                Server.onWelcomeUserMulticast(this);
                out.println(String.format("Welcome to chat, %s", nickname));

                while (in.hasNextLine()) {
                    String input = in.nextLine().trim();
                    if (input.compareToIgnoreCase("EXIT") == 0) break;

                    Server.onClientMessageMulticast(this, input, false);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Server.onClientExit(this);
    }

    public void receiveMulticastMessage(ClientThread th, String message) {
        if (out == null) return;
        out.println(message);
    }
}
