package org.ctf.client.lib;

public class ServerDetails {
  private String host;
  private String port;

  public ServerDetails(String host, String port) {
    this.host = host;
    this.port = port;
  }

  public String getHost(){
    return host;
  }
  
  public String getPort(){
    return port;
  }

  public void setHost(String host){
    this.host = host;
  }

  public void setPort(String port){
    this.port = port;
  }


  public boolean isLocalhost(){
    if(host.equals("localhost")){
        return true;
    } else {
        return false;
    }
  }
}
