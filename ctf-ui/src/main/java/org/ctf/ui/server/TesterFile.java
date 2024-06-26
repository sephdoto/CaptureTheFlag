package org.ctf.ui.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TesterFile {
    public static void main(String[] args) {
        ServerContainer sc = new ServerContainer();
        try {
            boolean t = sc.startServer("9010");
            System.out.println(t);
        } catch (Exception e) {
            System.out.println("Port in Use");
        }
    
    }
}
