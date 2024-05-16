package org.ctf.shared.tools;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.ctf.shared.ai.TestValues;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums;
import org.ctf.shared.wave.WaveFunctionCollapse;
import org.junit.jupiter.api.Test;
/**
 * @author ysiebenh
 */
class WaveFunctionCollapseTest {

  @Test
  void gridToImgTest() {
    WaveFunctionCollapse wfc1 = new WaveFunctionCollapse(TestValues.getTestState().getGrid(), Enums.Themes.LOTR);
    WaveFunctionCollapse wfc2 = new WaveFunctionCollapse(TestValues.getTestState().getGrid(), Enums.Themes.STARWARS);
    WaveFunctionCollapse wfc3 = new WaveFunctionCollapse(TestValues.getTestState().getGrid(), Enums.Themes.BAYERN);
    
    //WaveFunctionCollapse wfc = new WaveFunctionCollapse(new String[3][3]);
    try {
 
      //wfc.generateBackgroundRecursive(new WaveGrid(test, WaveFunctionCollapse.IMAGES_AMOUNT));
      ImageIO.write(wfc1.getBackground(), "png", new File(Constants.toUIResources + "gridLOTR.png"));
      ImageIO.write(wfc2.getBackground(), "png", new File(Constants.toUIResources + "gridSTARWARS.png"));
      ImageIO.write(wfc3.getBackground(), "png", new File(Constants.toUIResources + "gridBAYERN.png"));
     
      
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
}
