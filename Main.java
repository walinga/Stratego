class Main {
  static Board board;
  static Setup setup;

  public static void main(String[] args) {
    // Initialize Board class
    board = new Board();

    // Initialize Setup class
    setup = new Setup(board);
  }
}
