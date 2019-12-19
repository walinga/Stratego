class Coord {
  public int x;
  public int y;

  public Coord(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public boolean equals(Object obj) {
    if (obj instanceof Coord) {
      Coord c2 = (Coord) obj;
      return this.x == c2.x && this.y == c2.y;
    } else {
      return false;
    }
  }

  public int hashCode() {
    return this.x * 100 + this.y;
  }
}
