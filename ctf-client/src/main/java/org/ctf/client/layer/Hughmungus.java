//Private TEST File to test code snippets
package org.ctf.client.layer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.ctf.client.state.data.map.Directions;
import org.ctf.client.state.data.map.Movement;
import org.ctf.client.state.data.map.PieceDescription;
import org.ctf.client.tools.JSON_Tools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Hughmungus {
  public static List<String> colorList = Collections.synchronizedList(new LinkedList<String>());


    public static void main(String args[]){
        @SuppressWarnings("unused")
        String jso = """
        {
            "type": "King",
            "attackPower": 1,
            "count": 0,
            "movement": {
              "directions": {
                "left": 1,
                "right": 1,
                "up": 1,
                "down": 1,
                "upLeft": 1,
                "upRight": 1,
                "downLeft": 1,
                "downRight": 1
              }
            }
          }
        """;
      
        colorList.add("red");
        colorList.add("green");
        colorList.add("yellow");
        colorList.add("white");
        colorList.add("black");
        colorList.add("blue");
        colorList.add("gray");

         System.out.println(getRandColor());
         System.out.println(getRandColor());
         System.out.println(getRandColor());
         System.out.println(getRandColor());

         System.out.println(getRandColor());
         System.out.println(getRandColor());
         System.out.println(getRandColor());
         System.out.println(getRandColor());
         System.out.println(getRandColor());
         System.out.println(getRandColor());




        PieceDescription testPiece = new PieceDescription();
        testPiece.setType("King");
        testPiece.setAttackPower(1);
        Movement kMove = new Movement();
        Directions nd = new Directions();
        nd.setLeft(1);
        nd.setRight(1);
        nd.setUp(1);
        nd.setDown(1);
        nd.setUpLeft(1);
        nd.setUpRight(1);
        nd.setDownLeft(1);
        nd.setDownRight(1);

        kMove.setDirections(nd);
        testPiece.setMovement(kMove);

        String sim = JSON_Tools.stringFromMap(testPiece);

        Gson gson = new Gson();
        Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
        String jsonPayload = gson.toJson(testPiece);
        String jsonPayloadPretty = gsonPretty.toJson(testPiece);

        @SuppressWarnings("unused")
        Boolean checkagainstNormal = sim.equals(jsonPayload);
        @SuppressWarnings("unused")
        Boolean checkagainstPrettyPrint = sim.equals(jsonPayloadPretty);
     /*    System.out.println(checkagainstNormal);
        System.out.println(checkagainstPrettyPrint);


        System.out.println(sim);
       
        System.out.println(jsonPayload);
        System.out.println(jsonPayloadPretty); */

        PieceDescription fromsim = gson.fromJson(sim, PieceDescription.class);
        @SuppressWarnings("unused")
        PieceDescription normal = gson.fromJson(jsonPayloadPretty, PieceDescription.class);
       Movement siMov =  fromsim.getMovement();
       @SuppressWarnings("unused")
      Directions siDir = siMov.getDirections();


/* {
    "type": "King",
    "attackPower": 1,
    "count": 1,
    "movement": {
      "directions": {
        "left": 1,
        "right": 1,
        "up": 1,
        "down": 1,
        "upLeft": 1,
        "upRight": 1,
        "downLeft": 1,
        "downRight": 1
      }
    }
  } */

    }

  public static String getRandColor(){
      int randSelector = randomGen(colorList.size(),1);
      if(colorList.size() >=1 && randSelector > 0 ){
        String re = colorList.get(randSelector);
        colorList.remove(randSelector);
          return re;
      } else {
          return "NO COLOR";
      }
  }

  public static int randomGen(int max, int iterations) {
    Random rand = new Random();
    int ret = 0;
    for (int i = 0; i <= iterations; i++) {
        ret = rand.nextInt(max);
    }
    return ret;
}
}