package org.ctf.ui.server;

/**
 * Defines what functionality the server class has to provide
 * 
 * @author Raffay Syed
 */
public interface ServerInterface {

    public boolean startServer();

    public int checkStatus();

    public boolean restartServer();

    public boolean stopServer();

    public boolean killServer();
}
