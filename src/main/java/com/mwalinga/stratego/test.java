package com.mwalinga.stratego;

import java.util.*;

class test {
  static Board board;
  static Game game;
  static Setup setup;

  // NOTE: These are all "integration tests". Consider writing some unit tests
  // TODO: Move to the test directory
  public static void main(String[] args) {
    List<Boolean> testResults = new ArrayList<>();
    testResults.add(basicTest1());
    testResults.add(basicTest2());
    testResults.add(captureFlagTest());
    testResults.add(noValidMovesTest());
    testResults.add(quicknessTest());
    testResults.add(magicTest());
    testResults.add(dragonTest());
    for (int i=0; i<testResults.size(); i++) {
      System.out.println("Test #" + Integer.toString(i+1) + ": "
        + (testResults.get(i) ? "Passed!" : "FAILED")
      );
    }
  }

  public static void setup_test() {
    board = new Board();
    game = new Game(board);
    setup = new Setup(board, game);
  }

  final static String initBoard = "0r 0r 0r 0r 0r 0r 1r 7r 7r 7r \n" +
                                  "3r 2r 2r 2r 6r 6r 8r 8r 9r 10r \n" +
                                  "2r 3r 3r 3r 3r 4r 4r 5r 5r 11r \n" +
                                  "O  O  x  x  O  O  x  x  O  O  \n" +
                                  "O  O  x  x  O  O  x  x  O  O  \n" +
                                  "3b 3b 3b 3b 3b 4b 4b 5b 5b 11b \n" +
                                  "2b 2b 2b 2b 6b 6b 8b 8b 9b 10b \n" +
                                  "0b 0b 0b 0b 0b 0b 1b 7b 7b 7b \n";

  final static String afterBoard =  "0r 0r 0r 0r 0r 0r 1r 7r 7r 7r \n" +
                                    "3r 2r 2r 2r 6r 6r 8r 8r 9r 10r \n" +
                                    "O  3r 3r 3r 3r 4r 4r 5r 5r 11r \n" +
                                    "O  O  x  x  O  O  x  x  O  O  \n" +
                                    "O  O  x  x  O  O  x  x  O  O  \n" +
                                    "3b 3b 3b 3b 3b 4b 4b 5b 5b 11b \n" +
                                    "2b 2b 2b 2b 6b 6b 8b 8b 9b 10b \n" +
                                    "0b 0b 0b 0b 0b 0b 1b 7b 7b 7b \n";

  public static boolean basicTest1() {
    setup_test();
    setup.swapPieces(new Coord(2,1), new Coord(3,1), 'r');
    if (!board.toString().equals(initBoard)) {
      System.out.println(board);
      System.out.println(initBoard);
      return false;
    }
    setup.submitTeam('r');
    setup.submitTeam('b');
    boolean cond1 = board.getPiece(new Coord(3,1)).toString().equals("2r");
    boolean cond2 = game.getValidMoves(new Coord(3,1), 'r').equals(new HashSet<>(
      List.of(new Coord(4,1), new Coord(5,1), new Coord(6,1))
    ));
    game.makeMove(new Coord(3,1), new Coord(6,1));
    boolean cond3 = game.getCaptured().equals(new ArrayList<>(
      List.of(new Piece(2, 'r'))
    ));
    boolean cond4 = board.toString().equals(afterBoard);
    return cond1 && cond2 && cond3 && cond4;
  }

  public static boolean basicTest2() {
    setup_test();
    setup.submitTeam('r');
    setup.submitTeam('b');
    game.makeMove(new Coord(3,1), new Coord(4,1));
    boolean cond1 = game.getTurn() == 'b';
    game.makeMove(new Coord(6,1), new Coord(5,1));
    boolean cond2 = game.getTurn() == 'r';
    game.makeMove(new Coord(4,1), new Coord(5,1));
    boolean cond3 = game.getCaptured().equals(new ArrayList<>(
      List.of(new Piece(3, 'r'), new Piece(3, 'b'))
    ));
    boolean cond4 = game.getLastRevealed().containsValue(new ArrayList<>(
      List.of(new Piece(3, 'r'), new Piece(3, 'b'))
    ));
    return cond1 && cond2 && cond3 && cond4;
  }

  public static boolean captureFlagTest() {
    setup_test();
    board.swapPieces(new Coord(2,1), new Coord(3,10));
    setup.submitTeam('r');
    setup.submitTeam('b');
    game.makeMove(new Coord(3,10), new Coord(6,10));
    boolean gameOver = game.isGameEnded();
    return gameOver && game.getWinner() == 'r';
  }

  public static boolean noValidMovesTest() {
    setup_test();
    board.swapPieces(new Coord(8,1), new Coord(6,10));
    board.swapPieces(new Coord(8,2), new Coord(6,9));
    board.swapPieces(new Coord(8,3), new Coord(6,6));
    board.swapPieces(new Coord(8,4), new Coord(6,5));
    board.swapPieces(new Coord(8,5), new Coord(6,2));
    board.swapPieces(new Coord(8,6), new Coord(6,1));
    board.swapPieces(new Coord(7,10), new Coord(7,8));
    setup.submitTeam('b');
    board.swapPieces(new Coord(2,1), new Coord(3,1));
    setup.submitTeam('r');
    game.makeMove(new Coord(3,1), new Coord(6,1));
    return game.getWinner() == 'r';
  }

  // Test the powers for 7 and 8
  public static boolean quicknessTest() {
    setup_test();
    board.swapPieces(new Coord(1,10), new Coord(3,10));
    boolean cond1 = board.getValidMoves(new Coord(3,10)).equals(new HashSet<>(
      List.of(new Coord(4,10), new Coord(5,10), new Coord(4,9))
    ));
    return cond1;
  }

  // Test the powers for 4, 6, and 9
  public static boolean magicTest() {
    setup_test();
    board.swapPieces(new Coord(2,5), new Coord(3,5)); // Move the 6
    board.swapPieces(new Coord(2,9), new Coord(3,9)); // Move the 9
    setup.submitTeam('r');
    setup.submitTeam('b');
    // TEST the 4
    game.makeMove(new Coord(3,6), new Coord(4,6));
    game.makeMove(new Coord(6,5), new Coord(5,5));
    boolean cond1 = board.getValidMoves(new Coord(4,6)).equals(new HashSet<>(
      List.of(new Coord(6,8), new Coord(6,7), new Coord(6,6), new Coord(6,4),
              new Coord(5,5), new Coord(5,6), new Coord(4,5), new Coord(3,6))
    ));
    // TEST the 6
    game.makeMove(new Coord(3,5), new Coord(4,5));
    game.makeMove(new Coord(6,4), new Coord(6,5));
    boolean cond2 = board.getValidMoves(new Coord(4,5)).equals(new HashSet<>(
      List.of(new Coord(3,5), new Coord(5,5), new Coord(6,3), new Coord(6,5),
              new Coord(6,6), new Coord(6,7))
    ));
    // TEST the 9
    game.makeMove(new Coord(3,9), new Coord(4,9));
    boolean cond3 = board.getValidMoves(new Coord(4,9)).equals(new HashSet<>(
      List.of(new Coord(3,9), new Coord(4,10), new Coord(5,9), new Coord(6,10),
              new Coord(6,7), new Coord(6,8), new Coord(6,9))
    ));

    return cond1 && cond2 && cond3;
  }

  public static boolean dragonTest() {
    setup_test();
    board.swapPieces(new Coord(3,9), new Coord(3,10));
    setup.submitTeam('r');
    setup.submitTeam('b');

    game.makeMove(new Coord(3,10), new Coord(4,10));
    game.makeMove(new Coord(7,10), new Coord(5, 10));
    game.makeMove(new Coord(2,10), new Coord(3,10));
    boolean cond1 = board.getValidMoves(new Coord(5,10)).equals(new HashSet<>(
      List.of(new Coord(1,10), new Coord(2,10), new Coord(2,9), new Coord(3,10),
            new Coord(4,10), new Coord(5,9), new Coord(7,10))
    ));

    return cond1;
  }

  public static boolean rampageTest() {
    return true;
  }
}
