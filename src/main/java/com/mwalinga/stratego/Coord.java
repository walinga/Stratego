package com.mwalinga.stratego;

class Coord {
  public int row;
  public int col;

  public Coord(int row, int col) {
    this.row = row;
    this.col = col;
  }

  public boolean equals(Object obj) {
    if (obj instanceof Coord) {
      Coord c2 = (Coord) obj;
      return this.col == c2.col && this.row == c2.row;
    } else {
      return false;
    }
  }

  public int hashCode() {
    return this.col * 100 + this.row;
  }

  public String toString() {
    return "(" + Integer.toString(this.row) + "," + Integer.toString(this.col) + ")";
  }
}
