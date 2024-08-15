package org.ctf.ui.data;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Testing if the fixed Stack works
 * 
 * @author sistumpf
 */
class FixedStackTest {
  @Test
  void testPopT() {
    FixedStack<Integer> stack = new FixedStack<Integer>(10);
    for(int i=0; i<10; i++)
      stack.push(i);
    
    for(int i=9; i>=0; i--)
      assertTrue(stack.pop() == i);
    
    assertTrue(stack.size() == 0);
  }
  
  @Test
  void testPushT() {
    FixedStack<Integer> stack = new FixedStack<Integer>(3);
    for(int i=0; i<10; i++)
      stack.push(i);
    
    assertTrue(stack.size() == 3);
    
    for(Integer i : stack)
      System.out.println(i);
  }

  @Test
  void testMaxSize() {
    int maxSize = (int) (10 * Math.random());
    FixedStack<Integer> stack = new FixedStack<Integer>(maxSize);
    assertTrue(stack.maxSize() == maxSize);
  }

}
