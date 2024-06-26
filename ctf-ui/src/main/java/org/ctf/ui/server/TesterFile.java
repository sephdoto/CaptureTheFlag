package org.ctf.ui.server;

public class TesterFile {
    public static void main(String[] args) {
        ServerContainer sc = new ServerContainer();
        boolean t = sc.startServer("8888");
        System.out.println(t);
    }
}
