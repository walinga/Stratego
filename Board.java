import java.util.*;

class Board {
  // Board is 1-indexed. Row 1 is the red army side.
  Map<Coord, Piece> pieces;
  List<Coord> forbiddenZones;

  final int numRows = 8;
  final int numCols = 10;

  final int highestPiece = 10;

  public Board() {
    this.setup();
  }

  public void setup() {
    // TODO: Verify that these are the right number of each piece
    // Initialize several Piece classes
    forbiddenZones = new ArrayList<>(
      List.of(new Coord(4,3), new Coord(4,4),
              new Coord(5,3), new Coord(5,4),
              new Coord(4,7), new Coord(4,8),
              new Coord(5,7), new Coord(5,8)));

    pieces = new HashMap<>();

    // TODO: Remove. Really only needed for debugging
    for (int i=1; i<=numRows; i++) {
      for (int j=1; j<=numCols; j++) {
        pieces.put(new Coord(i,j), null);
      }
    }

    // Traps
    for (int i=1; i<=6; i++) {
      pieces.put(new Coord(1,i), new Piece(0,'r'));
      pieces.put(new Coord(numRows,i), new Piece(0,'b'));
    }

    // Assassin
    pieces.put(new Coord(1,7), new Piece(1, 'r'));
    pieces.put(new Coord(numRows,7), new Piece(1, 'b'));

    // Scouts. TODO: is there only 5?
    for (int i=1; i<=6; i++) {
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
      pieces.put(new Coord(2,i+6), new Piece(6,'r'));
      pieces.put(new Coord(numRows-1,i+6), new Piece(6,'b'));
    }

    // Beast Riders
    for (int i=1; i<=2; i++) {
      pieces.put(new Coord(2,i+8), new Piece(7,'r'));
      pieces.put(new Coord(numRows-1,i+8), new Piece(7,'b'));
    }

    // Knights
    for (int i=1; i<=2; i++) {
      pieces.put(new Coord(1,i+7), new Piece(8,'r'));
      pieces.put(new Coord(numRows,i+7), new Piece(8,'b'));
    }

    // Wizard
    pieces.put(new Coord(1,10), new Piece(9, 'r'));
    pieces.put(new Coord(numRows,10), new Piece(9, 'b'));

    // Dragon
    pieces.put(new Coord(3,10), new Piece(10, 'r'));
    pieces.put(new Coord(numRows-2,10), new Piece(10, 'b'));

    // DEBUG
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

  // Used when the user is setting up their side of the board
  public void swapPieces(Coord posA, Coord posB) {

  }
}
