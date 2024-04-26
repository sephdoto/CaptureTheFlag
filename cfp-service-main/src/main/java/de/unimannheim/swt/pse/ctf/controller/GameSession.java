package de.unimannheim.swt.pse.ctf.controller;

import de.unimannheim.swt.pse.ctf.game.Game;
import de.unimannheim.swt.pse.ctf.game.GameEngine;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple class that manages a game session via a synchronized {@link Map}.
 *
 * <p>Contains simple check for anti-cheat.
 */
public class GameSession {

  private final Game game;
  private final Map<String, String> teamSecrets;
  private static final Logger LOG = LoggerFactory.getLogger(GameSession.class); // REMOVE THIS LATER

  public GameSession(Game game) {
    this.game = game;
    this.teamSecrets = Collections.synchronizedMap(new HashMap<>());
  }

  public Game getGame() {
    return game;
  }

  /**
   * Create team secret
   *
   * @param teamId Team ID
   * @return generated secret
   */
  public String createTeamSecret(String teamId) {
    String teamSecret = UUID.randomUUID().toString();
    this.teamSecrets.put(teamId, teamSecret);

    return teamSecret;
  }

  /**
   * Is team allowed to do something?
   *
   * @param teamId Team ID
   * @param teamSecret given secret
   * @return true if team secret is valid, false otherwise
   */
  public boolean isAllowed(String teamId, String teamSecret) {
    Iterator it = this.teamSecrets.keySet().iterator();
    while(it.hasNext()){
        LOG.info("Keys " + it.next());
    }
    LOG.info("giveUp request from " + teamId);
    return this.teamSecrets.containsKey(teamId)
        && StringUtils.equals(this.teamSecrets.get(teamId), teamSecret);
  }
}
