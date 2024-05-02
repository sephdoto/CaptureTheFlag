package org.ctf.shared.tools;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.ctf.shared.ai.TestValues;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.wave.WaveFunctionCollapse;
import org.ctf.shared.wave.WaveGrid;
import org.junit.jupiter.api.Test;
/**
 * @author ysiebenh
 */
class WaveFunctionCollapseTest {

  @Test
  void gridToImgTest() {
    WaveFunctionCollapse wfc = new WaveFunctionCollapse(TestValues.getTestState().getGrid());
    //WaveFunctionCollapse wfc = new WaveFunctionCollapse(new String[40][40]);
    try {
      int[][] test = new int[40][40];
      test[20][15] = 24;
      test[20][16] = 24;
      test[20][17] = 24;
      test[20][18] = 24;
      test[20][19] = 24;
      test[20][20] = 24;
      test[21][15] = 24;
      test[21][16] = 24;
      test[21][17] = 24;
      test[21][18] = 24;
      test[21][19] = 24;
      test[21][20] = 24;

 
      //wfc.generateBackgroundRecursive(new WaveGrid(test, WaveFunctionCollapse.IMAGES_AMOUNT));
      ImageIO.write(wfc.getBackground(), "png", new File(Constants.toUIResources + "grid.png"));
     
      
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
