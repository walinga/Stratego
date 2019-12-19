class Piece {
  int value; // value of 0 means trap. 1 means slayer/assassin
  char team; // 'r' = Red team. 'b' = Blue team
  boolean switchedTeam;

  public Piece(int value, char team) {
    this.value = value;
    this.team = team;
    switchedTeam = false;
  }

  public void switchTeam() {
    if (!switchedTeam) {
      switchedTeam = true;
      if (team == 'r') {
        team = 'b';
      } else {
        team = 'r';
      }
    }
  }

  public String toString() {
    return Integer.toString(this.value)+ this.team;
  }
}
