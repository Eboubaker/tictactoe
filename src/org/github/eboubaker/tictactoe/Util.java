package org.github.eboubaker.tictactoe;

import java.net.InetSocketAddress;

public class Util {
    public static String repeat(String str, int count){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < count; i++){
            sb.append(str);
        }
        return sb.toString();
    }

    public static boolean ask(String label){
        while(true){
            System.out.print(label + "(Yes/No):");
            String answer = Game.cli.nextLine();
            if(answer.equalsIgnoreCase("yes") || answer.equals("y")){
                return true;
            }else if(answer.equalsIgnoreCase("no") || answer.equals("n")){
                return false;
            }
        }
    }

    public static InetSocketAddress toSocketAddress(String hostname) throws IllegalArgumentException{
        if(hostname == null){
            throw new IllegalArgumentException("hostname cannot be null");
        }
        String[] parts = hostname.split(":");
        return new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
    }
}
