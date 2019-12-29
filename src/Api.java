import java.net.*;
import java.util.*;
import java.io.*;
import com.sun.net.httpserver.*;
//import com.github.cliftonlabs.json_simple.JsonObject;

class Api {
  Setup setup;
  Game game;
  final int port = 8051;

  // TODO: Add a param "gameId" to all api methods to allow for multiple in-progress games
  // Add an endpoint newGame that will start a new game and return a gameId
  // In order for this to work, we would need to create the board, setup and game from the Api
  public Api(Setup setup, Game game) {
    this.setup = setup;
    this.game = game;
    startListening();
  }

  /* API methods */
  // (GET) getBoard(team) - returns a board with opposing team's pieces hidden
  // (UPDATE) swapPieces(pos1, pos2, team) - used during setup. returns new board
  // (UPDATE) submitTeam(team) - used to indicate setup is done. returns whether the game started
  // (GET) getValidMoves(pos, team) - returns possible moves for a specific piece and team
  // (UPDATE) makeMove(pos1, pos2, team) - makes the move. returns lots of stuff (see above)

  private void startListening() {
    try {
      HttpServer hs = HttpServer.create(new InetSocketAddress(port), 0);
      // API Endpoints
      // TODO: Use JSON. May requiring setting up a pom.xml for the JSON parser
      // TODO: Consider sending 400s for bad requests (eg. swapping incorrect pieces)
      hs.createContext("/newGame",  httpExchange -> {
        // TODO: Need to store multiple game sessions if we want to allow concurrent games
        Board b = new Board();
        game = new Game(b);
        setup = new Setup(b, game);
        String response = setup.getGameId();
        sendResponse(httpExchange, response);
      });

      hs.createContext("/getBoard",  httpExchange -> {
        httpExchange.getRequestURI().getQuery();
        // Should be either 'r' or 'b'
        char team = readRequest(httpExchange).toCharArray()[0];
        String response = game.getBoardForTeam(team);
        sendResponse(httpExchange, response);
      });

      hs.createContext("/swapPieces",  httpExchange -> {
        // Format: r,c r,c t
        String raw_coords = readRequest(httpExchange);
        String[] inputCoords = raw_coords.split(" ");
        String[] c1 = inputCoords[0].split(",");
        String[] c2 = inputCoords[1].split(",");
        Coord a = new Coord(Integer.parseInt(c1[0]), Integer.parseInt(c1[1]));
        Coord b = new Coord(Integer.parseInt(c2[0]), Integer.parseInt(c2[1]));

        char team = inputCoords[2].toCharArray()[0];
        setup.swapPieces(a, b, team);
        String response = game.getBoardForTeam(team);
        sendResponse(httpExchange, response);
      });

      hs.createContext("/submitTeam",  httpExchange -> {
        // Should be either 'r' or 'b'
        char team = readRequest(httpExchange).toCharArray()[0];
        boolean started = setup.submitTeam(team);
        String response = Boolean.toString(started);
        sendResponse(httpExchange, response);
      });

      hs.createContext("/getValidMoves",  httpExchange -> {
        // Format: r,c t
        String raw_coords = readRequest(httpExchange);
        String[] inputCoords = raw_coords.split(" ");
        String[] c1 = inputCoords[0].split(",");
        Coord a = new Coord(Integer.parseInt(c1[0]), Integer.parseInt(c1[1]));

        char team = inputCoords[1].toCharArray()[0];
        String response = game.getValidMoves(a, team).toString();
        sendResponse(httpExchange, response);
      });

      // - in the payload for each move need to return:
      //    - whether the game is over (and who the winner is)
      //    - reveal the attacked opposing piece if applicable
      //    - a full list of captured pieces (for completeness)
      //    - current board state
      hs.createContext("/makeMove",  httpExchange -> {
        // Format: r,c r,c t
        String raw_coords = readRequest(httpExchange);
        String[] inputCoords = raw_coords.split(" ");
        String[] c1 = inputCoords[0].split(",");
        String[] c2 = inputCoords[1].split(",");
        Coord a = new Coord(Integer.parseInt(c1[0]), Integer.parseInt(c1[1]));
        Coord b = new Coord(Integer.parseInt(c2[0]), Integer.parseInt(c2[1]));

        char team = inputCoords[2].toCharArray()[0];
        game.makeMove(a, b);
        boolean ended = game.isGameEnded();
        // NOTE: pseudo-json until we convert things to real json
        String response = "{\"gameOver\": \"" + Boolean.toString(ended) + "\",";
        if (!game.getLastRevealed().isEmpty()) {
          response += "\"revealedPiece\": \"" + game.getLastRevealed().toString() + "\",";
        }
        if (!game.getLastPowerUser().isEmpty()) {
          response += "\"lastPowerUser\": \"" + game.getLastPowerUser().toString() + "\",";
        }
        if (!game.getLastMove().isEmpty()) {
          response += "\"lastMove\": \"" + game.getLastMove().toString() + "\",";
        }
        if (ended) {
          response += "\"Winner\": \"" + Character.toString(game.getWinner()) + "\"}";
        } else {
          String escapedBoard = game.getBoardForTeam(team).replaceAll("\n", "\\\\n");
          response += "\"capturedPieces\": \"" + game.getCaptured().toString()
            + "\", \"boardState\": \"" + escapedBoard + "\"}";
        }

        sendResponse(httpExchange, response);
      });

      hs.start();
      System.out.println("API started! Listening on port " + Integer.toString(port));
    } catch (Exception e) {
      System.out.println("Error starting server: " + e.toString());
    }
  }

  private String readRequest(HttpExchange httpExchange) {
    try {
      InputStream in = httpExchange.getRequestBody();
      byte[] request = new byte[1024];
      in.read(request);
      in.close();
      return new String(request);
    } catch (Exception e) {
      // TODO: Send a 400 here
      System.out.println("Error reading request: " + e.toString());
      return "";
    }
  }

  private void sendResponse(HttpExchange httpExchange, String resp) {
    try {
      byte response[] = resp.getBytes("UTF-8");

      httpExchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
      httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
      //httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "http://localhost:3000");
      //httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "http://192.168.0.23:3000");
      // TODO: Add actual website URL as well
      httpExchange.sendResponseHeaders(200, response.length);

      OutputStream out = httpExchange.getResponseBody();
      out.write(response);
      out.close();
    } catch (Exception e) {
      // TODO: Send a 500 here
      System.out.println("Error sending response: " + e.toString());
    }
  }
}
