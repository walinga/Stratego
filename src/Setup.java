class Setup {
  Board board;
  Game game;

  public Setup(Board board) {
    // Take in a board class from Main
    this.board = board;
    board.setup();

    // TODO: Wait until the user finishes setting up
    while (true /* Get user input */) {
      //board.swapPieces(/* from user */ );
      break;
    }

    // Initialize Game class
    game = new Game(board);
  }
}
