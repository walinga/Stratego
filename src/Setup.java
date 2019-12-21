class Setup {
  Board board;
  Game game;
  boolean blueSetUp = false;
  boolean redSetUp = false;
  boolean started = false;

  public Setup(Board board, Game game) {
    this.board = board;
    this.game = game;
    board.setup();

    // DEBUG. Move this to a testing class
    board.swapPieces(new Coord(2,1), new Coord(3,1));
    board.printBoard();
    submitTeam('r');
    submitTeam('b');
    System.out.println(board.getPiece(new Coord(3,1)));
    System.out.println(game.getValidMoves(new Coord(3,1)));
    game.makeMove(new Coord(3,1), new Coord(6,1), 'r');
    System.out.println(game.getCaptured());
    board.printBoard();
    // END DEBUG
  }

  private boolean isTeamPiece(Coord p, char t) {
    return board.getPiece(p) != null && board.getPiece(p).getTeam() == t;
  }

  // Called by the API to indicate two pieces being swapped
  // Assumes that the method is being called only before the game has started
  public void swapPieces(Coord a, Coord b, char team) {
    if (isTeamPiece(a,team) && isTeamPiece(b,team)) {
      board.swapPieces(a, b);
    }
  }

  // Called by the API to indicate that a team is done setting up
  public void submitTeam(char team) {
    if (team == 'r') {
      redSetUp = true;
    } else if (team == 'b') {
      blueSetUp = true;
    }

    if (blueSetUp && redSetUp && !started) {
      game.start();
      started = true;
    }
  }
}
