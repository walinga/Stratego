package com.mwalinga.stratego;

class Main {
  static Board board;
  static Game game;
  static Setup setup;
  static UserInput ui;
  static Api api;

  public static void main(String[] args) {
    board = new Board();

    game = new Game(board);
    setup = new Setup(board, game);
    api = new Api(setup, game);

    // DEBUG: Console i/o
    //ui = new UserInput(setup, game);
  }
}
