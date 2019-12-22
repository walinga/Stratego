class Main {
  static Board board;
  static Game game;
  static Setup setup;
  static UserInput ui;

  public static void main(String[] args) {
    board = new Board();

    game = new Game(board);
    setup = new Setup(board, game);

    // TODO: Set up the API and pass in a setup and game
    // For now, we'll just listen through the keyboard
    ui = new UserInput(setup, game);
  }
}
