package com.sajoniks.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Server {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        try(ServerSocket s = new ServerSocket(PORT))
        {
            try(Socket incoming = s.accept())
            {
                InputStream inputStream = incoming.getInputStream();
                OutputStream outputStream = incoming.getOutputStream();

                try(Scanner in = new Scanner(new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
                {
                    PrintWriter printWriter = new PrintWriter(
                            new OutputStreamWriter(outputStream, StandardCharsets.UTF_8),
                            /*auto flush*/ true
                    );

                    printWriter.println("Connection established. Use EXIT to close connection");
                    while(in.hasNextLine())
                    {
                        String input = in.nextLine().trim();

                        printWriter.println(String.format("You entered: %s", input));

                        if (input.compareToIgnoreCase("EXIT") == 0) break;
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace(System.out);
                }
            }
        }
        catch (IOException e)
        {
            System.out.printf("Failed to start server at port %d%n", PORT);
        }
    }
}
