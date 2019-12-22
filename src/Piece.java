class Piece {
  int value; // value of 0 means trap. 1 means slayer/assassin. 11 means FLAG.
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

  public int getValue() {
    return value;
  }

  public char getTeam() {
    return team;
  }

  // Mainly including for ease of debugging
  public boolean equals(Object obj) {
    if (!(obj instanceof Piece)) return false;
    Piece p2 = (Piece) obj;
    return this.value == p2.value && this.team == p2.team && this.switchedTeam == p2.switchedTeam;
  }
}
