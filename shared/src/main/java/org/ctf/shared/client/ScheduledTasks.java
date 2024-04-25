package org.ctf.shared.client;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledTasks {
  public static void main(String[] args) {
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
    try {

      Callable<Integer> task2 =
          new Callable<Integer>() {
            public Integer call() {
              try {
                Thread.sleep(5000);
              } catch (InterruptedException ex) {
                ex.printStackTrace();
              }

              return 1000000;
            }
          };

      Callable<Integer> task =
          new Callable<Integer>() {
            public Integer call() {
              if (true) {
                Future<Integer> result2 = scheduler.schedule(task2, 5, TimeUnit.SECONDS);
                try {
                  System.out.println(result2.get() + "of sresult 2");
                } catch (InterruptedException | ExecutionException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
              }
              try {
                Thread.sleep(5000);
              } catch (InterruptedException ex) {
                ex.printStackTrace();
              }

              return 1000000;
            }
          };

      int delay = 5;

      Future<Integer> result = scheduler.schedule(task, delay, TimeUnit.SECONDS);

      try {

        Integer value = result.get();

        System.out.println("value = " + value);

      } catch (InterruptedException | ExecutionException ex) {
        ex.printStackTrace();
      }

      scheduler.shutdown();
    } catch (Exception e) {
      // TODO: handle exception
    }
  }
}
