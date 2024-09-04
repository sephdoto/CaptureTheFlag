package org.ctf.ui.highscore;

public class testClass {
  public static void main(String[] args) {
    String toTest = "aaaaaaaaaadsdasdadasd";
    boolean b = toTest.matches("(?i).*ai.*");
    System.out.println(b);
  }
}
