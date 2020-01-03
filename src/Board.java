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
  public boolean isMoveAllowed(Coord start, Coord end) {
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
    return pieces.get(start).getTeam() != pieces.get(end).getTeam();
  }

  // Does not check move validity
  public Set<Coord> oneSpaceAway(Coord start) {
    Set<Coord> moves = new HashSet<>();
    List<Coord> oneSpaceAway = new ArrayList<>(
      List.of(
        new Coord(start.row+1, start.col), new Coord(start.row-1, start.col),
        new Coord(start.row, start.col+1), new Coord(start.row, start.col-1)
      )
    );

    moves.addAll(oneSpaceAway);
    return moves;
  }

  // Does not check move validity
  public Set<Coord> diagonals(Coord start) {
    Set<Coord> moves = new HashSet<>();
    moves.addAll(oneSpaceAway(start));
    List<Coord> diagonals = new ArrayList<>(
      List.of(
        new Coord(start.row+1, start.col+1), new Coord(start.row-1, start.col+1),
        new Coord(start.row+1, start.col-1), new Coord(start.row-1, start.col-1)
      )
    );

    moves.addAll(diagonals);
    return moves;
  }

  /* Special move methods */

  private Set<Coord> scoutMoves(Coord start) {
    Set<Coord> moves = new HashSet<>();
    for (int i=start.row+1; i<=numRows; i++) {
      Coord move = new Coord(i,start.col);
      if (isMoveAllowed(start, move)) moves.add(move);
      if (!isSquareEmpty(move)) break;
    }
    for (int i=start.row-1; i>0; i--) {
      Coord move = new Coord(i,start.col);
      if (isMoveAllowed(start, move)) moves.add(move);
      if (!isSquareEmpty(move)) break;
    }
    for (int i=start.col+1; i<=numCols; i++) {
      Coord move = new Coord(start.row,i);
      if (isMoveAllowed(start, move)) moves.add(move);
      if (!isSquareEmpty(move)) break;
    }
    for (int i=start.col-1; i>0; i--) {
      Coord move = new Coord(start.row, i);
      if (isMoveAllowed(start, move)) moves.add(move);
      if (!isSquareEmpty(move)) break;
    }
    return moves;
  }

  // 7 - Beast Rider, and 8 - Knight
  private Set<Coord> quicknessMoves(Coord start) {
    Set<Coord> moves = new HashSet<>();
    for (Coord move : oneSpaceAway(start)) {
      if (isSquareEmpty(move)) {
        for (Coord moveTwo : oneSpaceAway(move)) {
          if (isMoveAllowed(start, moveTwo)) {
            moves.add(moveTwo);
          }
        }
      }
    }
    return moves;
  }

  // Two spaces away: diagonally, vert, horiz, or any combo
  private Set<Coord> magicMoves(Coord start) {
    Set<Coord> moves = new HashSet<>();
    for (Coord move : diagonals(start)) {
      for (Coord moveTwo : diagonals(move)) {
        if (isMoveAllowed(start, moveTwo) && !isSquareEmpty(moveTwo)) {
          moves.add(moveTwo);
        }
      }
    }
    return moves;
  }

  // Can fly over any number of pieces in a row, then (optionally) attack an adjacent enemy
  private Set<Coord> dragonMoves(Coord start) {
    Set<Coord> moves = new HashSet<>();
    for (int i=start.row+1; i<=numRows; i++) {
      Coord move = new Coord(i,start.col);
      if (forbiddenZones.contains(move)) break;
      if (isSquareEmpty(move)) {
        // NOTE: Disqualify the first square in the loop since we can't attack from there
        if (i != start.row+1) moves.add(move);
        break;
      }
    }
    for (int i=start.row-1; i>0; i--) {
      Coord move = new Coord(i,start.col);
      if (forbiddenZones.contains(move)) break;
      if (isSquareEmpty(move)) {
        if (i != start.row-1) moves.add(move);
        break;
      }
    }
    for (int i=start.col+1; i<=numCols; i++) {
      Coord move = new Coord(start.row,i);
      if (forbiddenZones.contains(move)) break;
      if (isSquareEmpty(move)) {
        if (i != start.col+1) moves.add(move);
        break;
      }
    }
    for (int i=start.col-1; i>0; i--) {
      Coord move = new Coord(start.row, i);
      if (forbiddenZones.contains(move)) break;
      if (isSquareEmpty(move)) {
        if (i != start.col-1) moves.add(move);
        break;
      }
    }
    // Loop through moves (should be <= 4) and check for enemies on adjacent squares
    Set<Coord> attackMoves = new HashSet<Coord>();
    for (Coord move : moves) {
      for (Coord move2 : oneSpaceAway(move)) {
        if (isMoveAllowed(start, move2) && !isSquareEmpty(move2)) {
          attackMoves.add(move2);
        }
      }
    }
    moves.addAll(attackMoves);
    return moves;
  }

  private Set<Coord> rampageMoves(Coord start) {
    Set<Coord> moves = new HashSet<>();
    for (Coord move : oneSpaceAway(start)) {
      if (isSquareEmpty(move)) {
        for (Coord move2 : diagonals(move)) {
          // Add any enemy pieces that are adjacent to neighbouring squares
          if (isMoveAllowed(start, move2) && !isSquareEmpty(move2)) {
            moves.add(move2);
          }
        }
      }
    }
    return moves;
  }

  public Set<Coord> getValidMoves(Coord start) {
    Set<Coord> moves = new HashSet<>();
    if (pieces.get(start) == null) {
      return moves;
    }
    int pieceValue = pieces.get(start).getValue();

    // "normal" one-space-away moves
    for (Coord move : oneSpaceAway(start)) {
      if (isMoveAllowed(start, move)) {
        moves.add(move);
      }
    }

    // Scouts - enumerate all possible lateral moves
    if (pieceValue == 2) {
      moves.addAll(scoutMoves(start));
    }

    if (pieceValue == 5) {
       moves.addAll(rampageMoves(start));
    }

    if (pieceValue == 7 || pieceValue == 8) {
      moves.addAll(quicknessMoves(start));
    }

    List<Integer> magicUsers = new ArrayList<>(List.of(4, 6, 9));
    if (magicUsers.contains(pieceValue)) {
      moves.addAll(magicMoves(start));
    }

    if (pieceValue == 10) {
      moves.addAll(dragonMoves(start));
    }

    return moves;
  }

  // TODO: We just need a boolean: whether there are exactly 0
  public Set<Coord> getAllMoves(char team) {
    Set<Coord> moves = new HashSet<>();
    pieces.forEach((pos, piece) -> {
      if (piece != null && piece.getTeam() == team) {
        moves.addAll(getValidMoves(pos));
      }
    });
    return moves;
  }

  public boolean isSquareEmpty(Coord pos) {
    return pieces.get(pos) == null && !forbiddenZones.contains(pos);
  }

  // Debugging utility method
  public String toString() {
    String base = "";
    for (int i=1; i<=numRows; i++) {
      for (int j=1; j<=numCols; j++) {
        Piece piece = pieces.get(new Coord(i,j));
        if (forbiddenZones.contains(new Coord(i,j))) {
          base += "x  ";
        } else if (piece == null) {
          base += "O  ";
        } else {
          base += (piece.toString() + " ");
        }
      }
      base += "\n";
    }
    return base;
  }
}
