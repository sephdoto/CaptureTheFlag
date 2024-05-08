package org.ctf.shared.tools;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.ctf.shared.ai.TestValues;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums;
import org.ctf.shared.wave.WaveFunctionCollapse;
import org.ctf.shared.wave.WaveGrid;
import org.junit.jupiter.api.Test;
/**
 * @author ysiebenh
 */
class WaveFunctionCollapseTest {

  @Test
  void gridToImgTest() {
    WaveFunctionCollapse wfc = new WaveFunctionCollapse(TestValues.getTestState().getGrid(), Enums.Themes.LOTR);
    
    //WaveFunctionCollapse wfc = new WaveFunctionCollapse(new String[3][3]);
    try {
 
      //wfc.generateBackgroundRecursive(new WaveGrid(test, WaveFunctionCollapse.IMAGES_AMOUNT));
      ImageIO.write(wfc.getBackground(), "png", new File(Constants.toUIResources + "grid.png"));
     
      
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
  
  @Test
  void randomWithWeightsTest() {
    int total = 0;
    for(int i = 0 ; i <= 10000; i++) {
      int y =  (int) (Math.random() * 5) ;
      int x = WaveFunctionCollapse.randomWithWeights(5, new int[] {1,1,1,1,1});
      total += x;
    }
    
    System.out.println("The total is" + (double)total / 10000.0);
    
  }
  

}
