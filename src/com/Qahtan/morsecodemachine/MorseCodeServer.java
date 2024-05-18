// definitely start the server first before you start the MorseCodeGUI application, otherwise connecting to a server to exchange morse wouldn't work.

// created a package based on the nomenclature of how they are supposed to be named!
package com.Qahtan.morsecodemachine;

import java.io.*;
import java.net.*;
import java.util.*;

//used an arbitary PORT that is not a system reserved, (same port number from the homework). but any port after 8000 i think? should be ok!
public class MorseCodeServer {
    private static final int PORT = 9898;
    private static List<PrintWriter> clients = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Morse Code Server is running..."); 		// added those for debugging, and wasn't interested in doing this in a GUI, but that can be done similar to in the homework!
        try (ServerSocket listener = new ServerSocket(PORT)) {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } catch (IOException e) {
            System.out.println("Server failed: " + e.getMessage());	// for debugging in case server failed (such as if you try to start the server on an already used port (if you try to do the server twice)
            e.printStackTrace();
        }
    }

    private static class Handler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                synchronized (clients) {
                    clients.add(out);
                }

                out.println("Welcome to the Morse Code Server!"); 		// welcoming the client coming in to make sure they are connected

                broadcastMessage("Server: A new client has joined."); 	// send this to all the clients (broadcasted).

                String input;
                while ((input = in.readLine()) != null) {
                    broadcastMessage(input);
                }
            } catch (IOException e) {
                System.out.println("Error handling client: " + e.getMessage()); // to catch exceptions and for debugging
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Couldn't close a socket"); // for debugging
                }
                synchronized (clients) {
                    clients.remove(out);
                }
                
                broadcastMessage("Server: A client has disconnected."); // to inform all the clients when a client has left
            }
        }
        
        // to broadcast the morse code recieved by one client and send it to the other client, or clients.
        private void broadcastMessage(String message) {
            synchronized (clients) {
                for (PrintWriter writer : clients) {
                    writer.println(message);
                }
            }
        }
    }
}
