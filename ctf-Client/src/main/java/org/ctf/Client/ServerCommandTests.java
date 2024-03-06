package org.ctf.client;

import java.io.IOException;
import java.net.URISyntaxException;

import org.ctf.TL.data.map.MapTemplate;
import org.ctf.TL.layer.CommLayer;
import org.ctf.TL.state.Team;
import org.ctf.TL.data.wrappers.GameSessionResponse;
import org.ctf.TL.data.wrappers.JoinGameResponse;
import org.ctf.TL.exceptions.Accepted;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



/**
 * Tests for the layer and the responses it gives out.
 */
public class ServerCommandTests {
    
    /* Notes while testing
     * on successful request. Returns gamesessionID, gameover flag and winners;
     * if flags for ALTERNATE game modes are set in the map, then also returns data
     * on fail/malformed request: returns GameOver AND 500. The swagger UI is wrong
     * malformed always returns gameOver true which DOES NOT depend on the isGameOver method in the game engine. The return is being calculated elsewhere. +
     * which is a VERY weird behaviour
     * 
     */


    public static void main (String[] args){
      //Uncomment to do invidivual tests
        testConnection();
        //testConnectionTimedGameMode();
        //testMalformedConnection();
        //testConnectionTimedMoveMode();
     
        //join();
        //joinNDelete();

    }

  public static void testConnection(){
        String jsonPayload = """
            {
                "gridSize": [10, 10],
                "teams": 2,
                "flags": 1,
                "blocks": 0,
                "pieces": [
                  {
                    "type": "Pawn",
                    "attackPower": 1,
                    "count": 10,
                    "movement": {
                      "directions": {
                        "left": 0,
                        "right": 0,
                        "up": 1,
                        "down": 0,
                        "upLeft": 1,
                        "upRight": 1,
                        "downLeft": 0,
                        "downRight": 0
                      }
                    }
                  },
                  {
                    "type": "Rook",
                    "attackPower": 5,
                    "count": 2,
                    "movement": {
                      "directions": {
                        "left": 2,
                        "right": 2,
                        "up": 2,
                        "down": 2,
                        "upLeft": 0,
                        "upRight": 0,
                        "downLeft": 0,
                        "downRight": 0
                      }
                    }
                  },
                  {
                    "type": "Knight",
                    "attackPower": 3,
                    "count": 2,
                    "movement": {
                      "shape": {
                        "type": "lshape"
                      }
                    }
                  },
                  {
                    "type": "Bishop",
                    "attackPower": 3,
                    "count": 2,
                    "movement": {
                      "directions": {
                        "left": 0,
                        "right": 0,
                        "up": 0,
                        "down": 0,
                        "upLeft": 2,
                        "upRight": 2,
                        "downLeft": 2,
                        "downRight": 2
                      }
                    }
                  },
                  {
                    "type": "Queen",
                    "attackPower": 5,
                    "count": 1,
                    "movement": {
                      "directions": {
                        "left": 2,
                        "right": 2,
                        "up": 2,
                        "down": 2,
                        "upLeft": 2,
                        "upRight": 2,
                        "downLeft": 2,
                        "downRight": 2
                      }
                    }
                  },
                  {
                    "type": "King",
                    "attackPower": 1,
                    "count": 1,
                    "movement": {
                      "directions": {
                        "left": 1,
                        "right": 1,
                        "up": 1,
                        "down": 1,
                        "upLeft": 1,
                        "upRight": 1,
                        "downLeft": 1,
                        "downRight": 1
                      }
                    }
                  }
                ],
                "placement": "symmetrical",
                "totalTimeLimitInSeconds": -1,
                "moveTimeLimitInSeconds": -1
              }
            """;
            
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            MapTemplate test = gson.fromJson(jsonPayload, MapTemplate.class);
    
            TestClient client = new TestClient();
            client.connect("http://localhost:8080", test);
            System.out.println(client.getSessionID());

            client.joinGame("team1");
            System.out.println(client.getSecretID());
            client.deleteSession();
    }
          
  public static void testConnectionBothAltModesEnabled(){
      String jsonPayload = """
          {
              "gridSize": [10, 10],
              "teams": 2,
              "flags": 1,
              "blocks": 0,
              "pieces": [
                {
                  "type": "Pawn",
                  "attackPower": 1,
                  "count": 10,
                  "movement": {
                    "directions": {
                      "left": 0,
                      "right": 0,
                      "up": 1,
                      "down": 0,
                      "upLeft": 1,
                      "upRight": 1,
                      "downLeft": 0,
                      "downRight": 0
                    }
                  }
                },
                {
                  "type": "Rook",
                  "attackPower": 5,
                  "count": 2,
                  "movement": {
                    "directions": {
                      "left": 2,
                      "right": 2,
                      "up": 2,
                      "down": 2,
                      "upLeft": 0,
                      "upRight": 0,
                      "downLeft": 0,
                      "downRight": 0
                    }
                  }
                },
                {
                  "type": "Knight",
                  "attackPower": 3,
                  "count": 2,
                  "movement": {
                    "shape": {
                      "type": "lshape"
                    }
                  }
                },
                {
                  "type": "Bishop",
                  "attackPower": 3,
                  "count": 2,
                  "movement": {
                    "directions": {
                      "left": 0,
                      "right": 0,
                      "up": 0,
                      "down": 0,
                      "upLeft": 2,
                      "upRight": 2,
                      "downLeft": 2,
                      "downRight": 2
                    }
                  }
                },
                {
                  "type": "Queen",
                  "attackPower": 5,
                  "count": 1,
                  "movement": {
                    "directions": {
                      "left": 2,
                      "right": 2,
                      "up": 2,
                      "down": 2,
                      "upLeft": 2,
                      "upRight": 2,
                      "downLeft": 2,
                      "downRight": 2
                    }
                  }
                },
                {
                  "type": "King",
                  "attackPower": 1,
                  "count": 1,
                  "movement": {
                    "directions": {
                      "left": 1,
                      "right": 1,
                      "up": 1,
                      "down": 1,
                      "upLeft": 1,
                      "upRight": 1,
                      "downLeft": 1,
                      "downRight": 1
                    }
                  }
                }
              ],
              "placement": "symmetrical",
              "totalTimeLimitInSeconds": 20000,
              "moveTimeLimitInSeconds": 10
            }
          """;
          
          Gson gson = new GsonBuilder().setPrettyPrinting().create();
          MapTemplate test = gson.fromJson(jsonPayload, MapTemplate.class);
  
          TestClient client = new TestClient();
          client.connect("http://localhost:8080", test);
  }  

  public static void testConnectionTimedGameMode(){
      String jsonPayload = """
          {
              "gridSize": [10, 10],
              "teams": 2,
              "flags": 1,
              "blocks": 0,
              "pieces": [
                {
                  "type": "Pawn",
                  "attackPower": 1,
                  "count": 10,
                  "movement": {
                    "directions": {
                      "left": 0,
                      "right": 0,
                      "up": 1,
                      "down": 0,
                      "upLeft": 1,
                      "upRight": 1,
                      "downLeft": 0,
                      "downRight": 0
                    }
                  }
                },
                {
                  "type": "Rook",
                  "attackPower": 5,
                  "count": 2,
                  "movement": {
                    "directions": {
                      "left": 2,
                      "right": 2,
                      "up": 2,
                      "down": 2,
                      "upLeft": 0,
                      "upRight": 0,
                      "downLeft": 0,
                      "downRight": 0
                    }
                  }
                },
                {
                  "type": "Knight",
                  "attackPower": 3,
                  "count": 2,
                  "movement": {
                    "shape": {
                      "type": "lshape"
                    }
                  }
                },
                {
                  "type": "Bishop",
                  "attackPower": 3,
                  "count": 2,
                  "movement": {
                    "directions": {
                      "left": 0,
                      "right": 0,
                      "up": 0,
                      "down": 0,
                      "upLeft": 2,
                      "upRight": 2,
                      "downLeft": 2,
                      "downRight": 2
                    }
                  }
                },
                {
                  "type": "Queen",
                  "attackPower": 5,
                  "count": 1,
                  "movement": {
                    "directions": {
                      "left": 2,
                      "right": 2,
                      "up": 2,
                      "down": 2,
                      "upLeft": 2,
                      "upRight": 2,
                      "downLeft": 2,
                      "downRight": 2
                    }
                  }
                },
                {
                  "type": "King",
                  "attackPower": 1,
                  "count": 1,
                  "movement": {
                    "directions": {
                      "left": 1,
                      "right": 1,
                      "up": 1,
                      "down": 1,
                      "upLeft": 1,
                      "upRight": 1,
                      "downLeft": 1,
                      "downRight": 1
                    }
                  }
                }
              ],
              "placement": "symmetrical",
              "totalTimeLimitInSeconds": 5000,
              "moveTimeLimitInSeconds": -1
            }
          """;
          
          Gson gson = new GsonBuilder().setPrettyPrinting().create();
            MapTemplate test = gson.fromJson(jsonPayload, MapTemplate.class);
    
            TestClient client = new TestClient();
            client.connect("http://localhost:8080", test);
  }

  public static void testConnectionTimedMoveMode(){
    String jsonPayload = """
        {
            "gridSize": [10, 10],
            "teams": 2,
            "flags": 1,
            "blocks": 0,
            "pieces": [
              {
                "type": "Pawn",
                "attackPower": 1,
                "count": 10,
                "movement": {
                  "directions": {
                    "left": 0,
                    "right": 0,
                    "up": 1,
                    "down": 0,
                    "upLeft": 1,
                    "upRight": 1,
                    "downLeft": 0,
                    "downRight": 0
                  }
                }
              },
              {
                "type": "Rook",
                "attackPower": 5,
                "count": 2,
                "movement": {
                  "directions": {
                    "left": 2,
                    "right": 2,
                    "up": 2,
                    "down": 2,
                    "upLeft": 0,
                    "upRight": 0,
                    "downLeft": 0,
                    "downRight": 0
                  }
                }
              },
              {
                "type": "Knight",
                "attackPower": 3,
                "count": 2,
                "movement": {
                  "shape": {
                    "type": "lshape"
                  }
                }
              },
              {
                "type": "Bishop",
                "attackPower": 3,
                "count": 2,
                "movement": {
                  "directions": {
                    "left": 0,
                    "right": 0,
                    "up": 0,
                    "down": 0,
                    "upLeft": 2,
                    "upRight": 2,
                    "downLeft": 2,
                    "downRight": 2
                  }
                }
              },
              {
                "type": "Queen",
                "attackPower": 5,
                "count": 1,
                "movement": {
                  "directions": {
                    "left": 2,
                    "right": 2,
                    "up": 2,
                    "down": 2,
                    "upLeft": 2,
                    "upRight": 2,
                    "downLeft": 2,
                    "downRight": 2
                  }
                }
              },
              {
                "type": "King",
                "attackPower": 1,
                "count": 1,
                "movement": {
                  "directions": {
                    "left": 1,
                    "right": 1,
                    "up": 1,
                    "down": 1,
                    "upLeft": 1,
                    "upRight": 1,
                    "downLeft": 1,
                    "downRight": 1
                  }
                }
              }
            ],
            "placement": "symmetrical",
            "totalTimeLimitInSeconds": -1,
            "moveTimeLimitInSeconds": 50
          }
        """;
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
           MapTemplate test = gson.fromJson(jsonPayload, MapTemplate.class);
    
            TestClient client = new TestClient();
            client.connect("http://localhost:8080", test);
}

  public static void testMalformedConnection(){
      String jsonPayload = """
        {
            "teams": 2,
            "flags": 1,
            "blocks": 0,
            "pieces": [
              {
                "type": "Pawn",
                "attackPower": 1,
                "count": 10,
                "movement": {
                  "directions": {
                    "left": 0,
                    "right": 0,
                    "up": 1,
                    "down": 0,
                    "upLeft": 1,
                    "upRight": 1,
                    "downLeft": 0,
                    "downRight": 0
                  }
                }
              },
              {
                "type": "Rook",
                "attackPower": 5,
                "count": 2,
                "movement": {
                  "directions": {
                    "left": 2,
                    "right": 2,
                    "up": 2,
                    "down": 2,
                    "upLeft": 0,
                    "upRight": 0,
                    "downLeft": 0,
                    "downRight": 0
                  }
                }
              },
              {
                "type": "Knight",
                "attackPower": 3,
                "count": 2,
                "movement": {
                  "shape": {
                    "type": "lshape"
                  }
                }
              },
              {
                "type": "Bishop",
                "attackPower": 3,
                "count": 2,
                "movement": {
                  "directions": {
                    "left": 0,
                    "right": 0,
                    "up": 0,
                    "down": 0,
                    "upLeft": 2,
                    "upRight": 2,
                    "downLeft": 2,
                    "downRight": 2
                  }
                }
              },
              {
                "type": "Queen",
                "attackPower": 5,
                "count": 1,
                "movement": {
                  "directions": {
                    "left": 2,
                    "right": 2,
                    "up": 2,
                    "down": 2,
                    "upLeft": 2,
                    "upRight": 2,
                    "downLeft": 2,
                    "downRight": 2
                  }
                }
              },
              {
                "type": "King",
                "attackPower": 1,
                "count": 1,
                "movement": {
                  "directions": {
                    "left": 1,
                    "right": 1,
                    "up": 1,
                    "down": 1,
                    "upLeft": 1,
                    "upRight": 1,
                    "downLeft": 1,
                    "downRight": 1
                  }
                }
              }
            ],
            "placement": "symmetrical",
            "totalTimeLimitInSeconds": -1,
            "moveTimeLimitInSeconds": -1
          }
        """;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        MapTemplate test = gson.fromJson(jsonPayload, MapTemplate.class);

        TestClient client = new TestClient();
        client.connect("http://localhost:8080", test);
        System.out.println(client.getSessionID());

        client.joinGame("team1");
        System.out.println(client.getSecretID());
    }

  public static void join() {
    String jsonPayload = """
      {
          "teams": 2,
          "flags": 1,
          "blocks": 0,
          "pieces": [
            {
              "type": "Pawn",
              "attackPower": 1,
              "count": 10,
              "movement": {
                "directions": {
                  "left": 0,
                  "right": 0,
                  "up": 1,
                  "down": 0,
                  "upLeft": 1,
                  "upRight": 1,
                  "downLeft": 0,
                  "downRight": 0
                }
              }
            },
            {
              "type": "Rook",
              "attackPower": 5,
              "count": 2,
              "movement": {
                "directions": {
                  "left": 2,
                  "right": 2,
                  "up": 2,
                  "down": 2,
                  "upLeft": 0,
                  "upRight": 0,
                  "downLeft": 0,
                  "downRight": 0
                }
              }
            },
            {
              "type": "Knight",
              "attackPower": 3,
              "count": 2,
              "movement": {
                "shape": {
                  "type": "lshape"
                }
              }
            },
            {
              "type": "Bishop",
              "attackPower": 3,
              "count": 2,
              "movement": {
                "directions": {
                  "left": 0,
                  "right": 0,
                  "up": 0,
                  "down": 0,
                  "upLeft": 2,
                  "upRight": 2,
                  "downLeft": 2,
                  "downRight": 2
                }
              }
            },
            {
              "type": "Queen",
              "attackPower": 5,
              "count": 1,
              "movement": {
                "directions": {
                  "left": 2,
                  "right": 2,
                  "up": 2,
                  "down": 2,
                  "upLeft": 2,
                  "upRight": 2,
                  "downLeft": 2,
                  "downRight": 2
                }
              }
            },
            {
              "type": "King",
              "attackPower": 1,
              "count": 1,
              "movement": {
                "directions": {
                  "left": 1,
                  "right": 1,
                  "up": 1,
                  "down": 1,
                  "upLeft": 1,
                  "upRight": 1,
                  "downLeft": 1,
                  "downRight": 1
                }
              }
            }
          ],
          "placement": "symmetrical",
          "totalTimeLimitInSeconds": -1,
          "moveTimeLimitInSeconds": -1
        }
      """;
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      MapTemplate test = gson.fromJson(jsonPayload, MapTemplate.class);

      TestClient testL = new TestClient();
      testL.connect("http://localhost:8080", test);

  }

}