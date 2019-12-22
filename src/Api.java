class Api {
  // TODO notes:
  // - api should only return red pieces for the red team and blue pieces for the blue team
  // - after each move, should return list of captured pieces (and winner if applicable)
  // - in the payload for each move need to return:
  //    - whether the game is over (and who the winner is)
  //    - reveal the attacked opposing piece if applicable
  //    - current board state
  Setup setup;
  Game game;

  public Api(Setup setup, Game game) {
    this.setup = setup;
    this.game = game;
    startListening();
  }

  private void startListening() {
  }
}
