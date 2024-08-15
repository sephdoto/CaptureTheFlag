package org.ctf.ui.data;

import java.util.Stack;

/**
 * A Stack implementation with a fixed size.
 * If the max size is reached, the first element gets deleted from the Stack.
 * 
 * @author sistumpf
 * @param <T> Object to be stored in the Stack
 */
public class FixedStack<T> extends Stack<T> {
  private static final long serialVersionUID = -1493719939850970802L;
  private int maxSize;
  
  public FixedStack(int maxSize) {
    this.maxSize = maxSize;
  }
  
  @Override
  public T push(T object) {
      while (this.size() >= maxSize) {
          this.remove(0);
      }
      return super.push(object);
  }

  /**
   * @return the Stacks max size
   */
  public int maxSize() {
    return this.maxSize;
  }
}
