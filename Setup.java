class Setup {
  Board board;
  Game game;

  public Setup(Board board) {
    // Take in a board class from Main
    this.board = board;

    // TODO: Wait until the user finishes setting up

    // Initialize Game class
    game = new Game(board);
  }
}
