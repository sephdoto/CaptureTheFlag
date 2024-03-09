//Private TEST File to test code snippets
package org.ctf.client.layer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.ctf.client.state.data.map.Directions;
import org.ctf.client.state.data.map.MapTemplate;
import org.ctf.client.state.data.map.Movement;
import org.ctf.client.state.data.map.PieceDescription;
import org.ctf.client.tools.JSON_Tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Hughmungus {
  public static List<String> colorList = Collections.synchronizedList(new LinkedList<String>());


    public static void main(String args[]) throws Exception{

      BufferedReader br = null;
        
      br = new BufferedReader(new FileReader("F:\\VS Code Repo\\cfp14\\ctf-Client\\src\\test\\java\\org\\ctf\\Client\\10x10_2teams_example.json"));
    

       Gson gson = new GsonBuilder().setPrettyPrinting().create();
       MapTemplate template = gson.fromJson(br, MapTemplate.class);
      
      // Converting back
      System.out.println(gson.toJson(template));
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