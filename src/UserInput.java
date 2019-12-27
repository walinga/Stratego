import java.util.*;

// NOTE: This is a simple instance of a front end for debugging use from the console
class UserInput {
  Setup setup;
  Game game;

  public UserInput(Setup setup, Game game) {
    this.setup = setup;
    this.game = game;
    startListening();
  }

  private String teamName(char t) {
    if (t=='r') return "RED";
    else return "BLUE";
  }

  private void startListening() {
    Scanner in = new Scanner(System.in);
    // Wait for setup to complete
    while (true) {
      setup.displayBoard();
      String s = in.nextLine();
      if (s.contains("submit")) {
        setup.submitTeam('r');
        setup.submitTeam('b'); // For now, leave blue team as is
        break;
      }
      // INPUT format: r,c r,c
      try {
        String[] inputCoords = s.split(" ");
        String[] c1 = inputCoords[0].split(",");
        String[] c2 = inputCoords[1].split(",");
        Coord a = new Coord(Integer.parseInt(c1[0]), Integer.parseInt(c1[1]));
        Coord b = new Coord(Integer.parseInt(c2[0]), Integer.parseInt(c2[1]));
        setup.swapPieces(a, b, 'r');
      } catch (Exception e) {
        System.out.println("Incorrect format");
      }
    }

    // Input moves
    while (true) {
      try {
        setup.displayBoard();
        char turn = game.getTurn();
        System.out.println(teamName(turn) + "'s turn");

        String s = in.nextLine();
        if (s.contains("v")) {
          String[] inputCoords = s.split(" ");
          String[] c2 = inputCoords[1].split(",");
          Coord a = new Coord(Integer.parseInt(c2[0]), Integer.parseInt(c2[1]));
          System.out.println(game.getValidMoves(a, turn));
          continue;
        }

        String[] inputCoords = s.split(" ");
        String[] c1 = inputCoords[0].split(",");
        String[] c2 = inputCoords[1].split(",");
        Coord a = new Coord(Integer.parseInt(c1[0]), Integer.parseInt(c1[1]));
        Coord b = new Coord(Integer.parseInt(c2[0]), Integer.parseInt(c2[1]));
        game.makeMove(a, b);
        boolean ended = game.isGameEnded();
        if (ended) {
          System.out.println("Winner: " + teamName(game.getWinner()));
          break;
        }
      } catch (Exception e) {
        System.out.println("Incorrect format");
      }
    }
  }
}
