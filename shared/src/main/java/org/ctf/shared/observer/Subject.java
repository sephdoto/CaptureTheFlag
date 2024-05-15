package org.ctf.shared.observer;

/**
 * Subject for the Observer Pattern
 *
 * @author rsyed
 */
public interface Subject {
  // register an observer to a subject
  void register(Observer observer);

  // unregister an observer from a subject
  void unregister(Observer observer);

  // notify all observers for the change in subject's state
  void notifyObservers();

  // method to get update from subject by the observer
  Object getUpdate(Observer observer);

  // post message to observers
  void postMessage(String message);
}
