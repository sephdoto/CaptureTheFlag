package org.ctf.shared.wave;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.ctf.shared.ai.TestValues;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.wave.WaveFunctionCollapse;
import org.junit.jupiter.api.Test;

class WaveFunctionCollapseTest {

  @Test
  void gridToImgTest() {
    WaveFunctionCollapse wfc = new WaveFunctionCollapse(TestValues.getTestState().getGrid());
    try {
      int[][] test = new int[30][30];
      test[0][0] = 1;
      test[0][1] = 5;
      test[1][1] = 4;
      test[1][2] = 2;
      int[][] grid = wfc
          .generateBackground(test);
      ImageIO.write(wfc.gridToImg(grid), "png", new File(Constants.toUIResources + "grid.png"));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
