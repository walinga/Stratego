import java.util.*;

class Board {
  // Board is 1-indexed. Row 1 is the red army side.
  Map<Coord, Piece> pieces;
  Set<Coord> forbiddenZones;

  final int numRows = 8;
  final int numCols = 10;

  final int highestPiece = 10;

  public void setup() {
    forbiddenZones = new HashSet<>(
      List.of(new Coord(4,3), new Coord(4,4),
              new Coord(5,3), new Coord(5,4),
              new Coord(4,7), new Coord(4,8),
              new Coord(5,7), new Coord(5,8)));

    pieces = new HashMap<>();

    // Traps
    for (int i=1; i<=6; i++) {
      pieces.put(new Coord(1,i), new Piece(0,'r'));
      pieces.put(new Coord(numRows,i), new Piece(0,'b'));
    }

    // Assassin
    pieces.put(new Coord(1,7), new Piece(1, 'r'));
    pieces.put(new Coord(numRows,7), new Piece(1, 'b'));

    // Scouts
    for (int i=1; i<=4; i++) {
      pieces.put(new Coord(2,i), new Piece(2,'r'));
      pieces.put(new Coord(numRows-1,i), new Piece(2,'b'));
    }

    // Dwarves
    for (int i=1; i<=5; i++) {
      pieces.put(new Coord(3,i), new Piece(3,'r'));
      pieces.put(new Coord(numRows-2,i), new Piece(3,'b'));
    }

    // Elves
    for (int i=1; i<=2; i++) {
      pieces.put(new Coord(3,i+5), new Piece(4,'r'));
      pieces.put(new Coord(numRows-2,i+5), new Piece(4,'b'));
    }

    // Yeti / Lava Beast
    for (int i=1; i<=2; i++) {
      pieces.put(new Coord(3,i+7), new Piece(5,'r'));
      pieces.put(new Coord(numRows-2,i+7), new Piece(5,'b'));
    }

    // Sorcerer
    for (int i=1; i<=2; i++) {
      pieces.put(new Coord(2,i+4), new Piece(6,'r'));
      pieces.put(new Coord(numRows-1,i+4), new Piece(6,'b'));
    }

    // Beast Riders
    for (int i=1; i<=3; i++) {
      pieces.put(new Coord(1,i+7), new Piece(7,'r'));
      pieces.put(new Coord(numRows,i+7), new Piece(7,'b'));
    }

    // Knights
    for (int i=1; i<=2; i++) {
      pieces.put(new Coord(2,i+6), new Piece(8,'r'));
      pieces.put(new Coord(numRows-1,i+6), new Piece(8,'b'));
    }

    // Wizard
    pieces.put(new Coord(2,9), new Piece(9, 'r'));
    pieces.put(new Coord(numRows-1,9), new Piece(9, 'b'));

    // Dragon
    pieces.put(new Coord(2,10), new Piece(10, 'r'));
    pieces.put(new Coord(numRows-1,10), new Piece(10, 'b'));

    // Flag
    pieces.put(new Coord(3,10), new Piece(11, 'r'));
    pieces.put(new Coord(numRows-2,10), new Piece(11, 'b'));
  }

  /* Acceessor methods */

  public Piece getPiece(Coord pos) {
    return pieces.get(pos);
  }

  /* UPDATE methods */

  // Move the piece on posA to posB and vice-versa
  public void swapPieces(Coord posA, Coord posB) {
    Piece temp = pieces.get(posA);
    pieces.put(posA, pieces.get(posB));
    pieces.put(posB, temp);
  }

  public void removePiece(Coord pos) {
    pieces.put(pos, null);
  }

  // Checks whether a move can be performed (ie. not into a forbidden zone, and either into an empty
  //  space or onto an opposing team's piece).
  // This method does NOT check:
  // - whether the squares are one space apart (normal moves)
  // - which piece will die (if any)
  // - special moves/powers
  public boolean isMoveAllowed(Coord start, Coord end, char team) {
    if (end.col < 1 || end.col > numCols || end.row < 1 || end.row > numRows) {
      return false;
    }
    if (forbiddenZones.contains(end)) {
      return false;
    }
    // Traps and flags can't move
    if (pieces.get(start) == null || pieces.get(start).getValue() == 0
        || pieces.get(start).getValue() == 11) {
      return false;
    }
    if (pieces.get(end) == null) {
      return true;
    }
    return pieces.get(start).getTeam() == team && pieces.get(end).getTeam() != team;
  }

  // Special move methods
  public Set<Coord> scoutMoves(Coord start, char team) {
    // *experimental*. Find scoutStart, scoutEnd
    /*int scoutStart = 1;
    int scoutEnd = start.row;
    for (int i=1; i<=board.numCols; i++) {
      if (board.getPiece(move) != null) {
        scoutStart++;
      }
    }*/
    Set<Coord> moves = new HashSet<>();
    for (int i=start.row+1; i<=numRows; i++) {
      Coord move = new Coord(i,start.col);
      if (isMoveAllowed(start, move, team)) {
        moves.add(move);
      }
      if (pieces.get(move) != null) {
        break;
      }
    }
    for (int i=start.row-1; i>0; i--) {
      Coord move = new Coord(i,start.col);
      if (isMoveAllowed(start, move, team)) {
        moves.add(move);
      }
      if (pieces.get(move) != null) {
        break;
      }
    }
    for (int i=start.col+1; i<numCols; i++) {
      Coord move = new Coord(start.row,i);
      if (isMoveAllowed(start, move, team)) {
        moves.add(move);
      }
      if (pieces.get(move) != null) {
        break;
      }
    }
    for (int i=start.col-1; i>0; i--) {
      Coord move = new Coord(start.row, i);
      if (isMoveAllowed(start, move, team)) {
        moves.add(move);
      }
      if (pieces.get(move) != null) {
        break;
      }
    }
    return moves;
  }

  // Debugging utility method
  public void printBoard() {
    for (int i=1; i<=numRows; i++) {
      for (int j=1; j<=numCols; j++) {
        Piece piece = pieces.get(new Coord(i,j));
        if (piece == null) {
          System.out.printf("O ");
        } else {
          System.out.printf(piece.toString() + " ");
        }
      }
      System.out.printf("\n");
    }
  }
}
