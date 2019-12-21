class Main {
  static Board board;
  static Game game;
  static Setup setup;

  public static void main(String[] args) {
    board = new Board();

    game = new Game(board);
    setup = new Setup(board, game);

    // TODO: Set up the API and pass in a setup and game
  }
}
