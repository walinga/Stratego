class Setup {
  Board board;
  Game game;
  boolean blueSetUp = false;
  boolean redSetUp = false;
  boolean started = false;

  // TODO: Disallow having a setup where one team cannot move
  public Setup(Board board, Game game) {
    this.board = board;
    this.game = game;
    board.setup();
  }

  private boolean isTeamPiece(Coord p, char t) {
    return board.getPiece(p) != null && board.getPiece(p).getTeam() == t;
  }

  // Called by the API to indicate two pieces being swapped
  // Assumes that the method is being called only before the game has started
  public void swapPieces(Coord a, Coord b, char team) {
    if (started) return;
    if (isTeamPiece(a,team) && isTeamPiece(b,team)) {
      board.swapPieces(a, b);
    }
  }

  // Called by the API to indicate that a team is done setting up
  // Returns true if the game started
  public boolean submitTeam(char team) {
    if (team == 'r') {
      redSetUp = true;
    } else if (team == 'b') {
      blueSetUp = true;
    }

    if (blueSetUp && redSetUp && !started) {
      game.start();
      started = true;
    }
    return started;
  }

  public void displayBoard() {
    System.out.println(board.toString());
  }
}
