package org.github.eboubaker.tictactoe;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Game {
    static boolean useUnicodeRendering = false;
    static boolean clearScreen = false;

    public static Scanner cli = new Scanner(System.in);

    public static void main(String[] args) {
        if (args.length == 2) {
            String command = args[0];
            String hostname = args[1];
            if (!command.equalsIgnoreCase("create") && !command.equalsIgnoreCase("join")) {
                System.err.println("Invalid command\nUsage: tictactoe.jar [create|join] [hostname:port]");
                System.exit(1);
            }
            InetSocketAddress address = null;
            try {
                address = Util.toSocketAddress(hostname);
            } finally {
                if (address == null) {
                    fatal("Invalid hostname: " + hostname + "\nUsage: tictactoe.jar [create|join] [hostname:port]");
                }
            }
            new Board().print(true);
            System.out.println("if you see a normal 3x3 grid answer no");
            if (!Util.ask("do you see weird glyphs/question-marks?")) {
                useUnicodeRendering = true;
            }
            System.out.println("\u001B[2J\u001B[H");
            System.out.println("if you see a weird text above this message answer no");
            if (Util.ask("was the console cleared?")) {
                clearScreen = true;
            }
            if (command.equalsIgnoreCase("create")) {
                runAsServer(address);
            } else {
                runAsClient(address);
            }
        } else {
            fatal("Usage: tictactoe.jar [create|join] [hostname:port]");
        }
    }

    public static void fatal(String s) {
        System.err.println(s);
        System.exit(1);
    }

    public static void runAsClient(InetSocketAddress address) {
        Socket socket;
        DataInputStream in = null;
        DataOutputStream out = null;
        try {
            System.out.println("Connecting to " + address.getHostName() + ":" + address.getPort());
            socket = new Socket();
            socket.setSoTimeout(5_000);
            socket.connect(address);
            socket.setSoTimeout(60_000);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Player " + socket.getInetAddress().getHostName() + " connected");
        } catch (IOException e) {
            fatal("Failed to connect to host " + address.getHostName() + ":" + address.getPort() + " due to: " + e.getMessage());
        }
        assert in != null && out != null;
        Board board = new Board();
        do {
            if (board.turn != 0) {
                System.out.println("Waiting for opponent response...");
            }
            board.newGame();
            try {
                out.writeInt(board.hashCode());// server will make sure the state is the same
                in.readBoolean(); // server must respond yes
            } catch (IOException e) {
                fatal("Game Synchronization error: " + e.getMessage());
            }
            board.print(useUnicodeRendering);
            char winner;
            do {
                String input = "";
                if (board.turn % 2 == 1) {
                    System.out.print("Enter a move: ");
                    input = cli.nextLine();
                    try {
                        out.writeUTF(input);
                    } catch (IOException e) {
                        fatal("Network Communication failed: " + e.getMessage());
                    }
                } else {
                    System.out.println("Waiting for opponent move...");
                    try {
                        input = in.readUTF();
                    } catch (IOException e) {
                        fatal("Network Communication failed: " + e.getMessage());
                    }
                }
                if (input.length() != 2) {
                    System.out.println("Invalid coordinate, coordinates must be 2 characters long");
                } else {
                    int col = input.charAt(1) - '1';
                    int row = input.toUpperCase().charAt(0) - 'A';
                    if (col < 0 || col > 2 || row < 0 || row > 2) {
                        System.out.println("Invalid coordinate, coordinate must be between A1 and C3");
                    } else if (board.isOccupied(col, row)) {
                        System.out.println("Invalid coordinate, coordinate " + input + " is already occupied");
                    } else {
                        if (clearScreen) System.out.println("\u001B[2J\u001B[H");
                        board.endTurn(col, row);
                        board.print(useUnicodeRendering);
                    }
                }
                winner = board.findWinner();
            } while (winner == '\0');
            if (winner == 'X') {
                System.out.println("X wins!");
            } else if (winner == 'O') {
                System.out.println("O wins!");
            } else {
                System.out.println("Draw!");
            }
        } while (Util.ask("Play again?"));
    }

    public static void runAsServer(InetSocketAddress address) {
        Socket socket = null;
        DataInputStream in = null;
        DataOutputStream out = null;
        try {
            ServerSocket server = new ServerSocket();
            server.setSoTimeout(60_000);
            server.bind(address);
            System.out.println("Waiting for a player to connect to " + address.getHostName() + ":" + address.getPort());
            socket = server.accept();
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Player " + socket.getInetAddress().getHostName() + " connected");
        } catch (IOException e) {
            fatal("Failed to create server: " + e.getMessage());
        }
        assert in != null && out != null;
        Board board = new Board();
        do {
            if (board.turn != 0) {
                System.out.println("Waiting for opponent response...");
            }
            board.newGame();
            try {
                if (in.readInt() != board.hashCode()) {// make sure the state is the same
                    fatal("Game Synchronization error");
                }
                out.writeBoolean(true);// ok signal
            } catch (IOException e) {
                fatal("Game Synchronization error: " + e.getMessage());
            }
            board.print(useUnicodeRendering);
            char winner;
            do {
                String input = "";
                if (board.turn % 2 == 0) {
                    System.out.print("Enter a move: ");
                    input = cli.nextLine();
                    try {
                        out.writeUTF(input);
                    } catch (IOException e) {
                        fatal("Network Communication failed: " + e.getMessage());
                    }
                } else {
                    System.out.println("Waiting for opponent move...");
                    try {
                        input = in.readUTF();
                    } catch (IOException e) {
                        fatal("Network Communication failed: " + e.getMessage());
                    }
                }
                if (input.length() != 2) {
                    System.out.println("Invalid coordinate, coordinates must be 2 characters long");
                } else {
                    int col = input.charAt(1) - '1';
                    int row = input.toUpperCase().charAt(0) - 'A';
                    if (col < 0 || col > 2 || row < 0 || row > 2) {
                        System.out.println("Invalid coordinate, coordinate must be between A1 and C3");
                    } else if (board.isOccupied(col, row)) {
                        System.out.println("Invalid coordinate, coordinate " + input + " is already occupied");
                    } else {
                        if (clearScreen) System.out.println("\u001B[2J\u001B[H");
                        board.endTurn(col, row);
                        board.print(useUnicodeRendering);
                    }
                }
                winner = board.findWinner();
            } while (winner == '\0');
            if (winner == 'X') {
                System.out.println("X wins!");
            } else if (winner == 'O') {
                System.out.println("O wins!");
            } else {
                System.out.println("Draw!");
            }
        } while (Util.ask("Play again?"));
    }
}
