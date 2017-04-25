package student;

import main.TXTmain;
import org.junit.*;
import static org.junit.Assert.*;

public class Test {
  @Test
  public void fullTest() {
    TXTmain testGame = new TXTmain();
    for (int i = 0; i < 1000; i++) {
      long startTime = System.currentTimeMillis();
      testGame.main();
      long endTime = System.currentTimeMillis();
      assertTrue(endTime - startTime < 10000);
    }
  }
}
