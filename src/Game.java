import java.util.*;

class Game {
  Board board;
  char turn;
  List<Piece> capturedBlues;
  List<Piece> capturedReds;

  public Game(Board board) {
    this.board = board;
    capturedBlues = new ArrayList<>();
    capturedReds = new ArrayList<>();
  }

  public void start() {
    turn = 'r';
  }

  public Set<Coord> getValidMoves(Coord start) {
    Set<Coord> moves = new HashSet<>();
    if (board.getPiece(start) == null) {
      return moves;
    }
    int pieceValue = board.getPiece(start).getValue();
    char team = board.getPiece(start).getTeam();

    List<Coord> oneSpaceAway = new ArrayList<>(
      List.of(
        new Coord(start.row+1, start.col), new Coord(start.row-1, start.col),
        new Coord(start.row, start.col+1), new Coord(start.row, start.col-1)
      )
    );
    // "normal" one-space-away moves
    for (Coord move : oneSpaceAway) {
      if (board.isMoveAllowed(start, move, team)) {
        moves.add(move);
      }
    }

    // Scouts - enumerate all possible lateral moves
    if (pieceValue == 2) {
      moves.addAll(board.scoutMoves(start, team));
    }

    return moves;
  }

  public List<Piece> getCaptured() {
    List<Piece> allCaptured = new ArrayList<>(
      capturedReds
    );
    allCaptured.addAll(capturedBlues);
    return allCaptured;
  }

  // Called by the API to move a piece
  // Returns true iff the game is over after this move
  public boolean makeMove(Coord start, Coord end, char team) {
    // TODO: Consider removing team as a param since we can check the piece
    // on start against which team's turn it is
    if (team != turn) {
      return false;
    }
    if (!board.isMoveAllowed(start, end, team)) {
      return false;
    }

    if (!getValidMoves(start).contains(end)) {
      return false;
    }

    if (board.getPiece(end) == null) {
      // Move to an empty square, just move the starting piece
      board.swapPieces(start, end);
      turn = opposingTeam(turn);
      return false;
    }

    // At this point, both start and end are valid pieces.
    int pieceValue = board.getPiece(start).getValue();
    int attackedPieceValue = board.getPiece(end).getValue();

    // FLAG
    if (attackedPieceValue == 11) {
      // end the game
      return true;
    }

    performCapture(start, end, pieceValue, attackedPieceValue);
    // TODO: Need some concept of *who* won the game
    if (capturedBlues.size() == 30 || capturedReds.size() == 30) {
      // end the game
      return true;
    }

    turn = opposingTeam(turn);
    return false;
  }

  private void performCapture(Coord start, Coord end, int pieceValue, int attackedPieceValue) {
    char curTeam = this.turn;
    char oppTeam = opposingTeam(this.turn);
    // Traps
    if (attackedPieceValue == 0) {
      if (pieceValue == 3) {
        capturePiece(end, oppTeam);
        board.swapPieces(start, end);
      } else {
        capturePiece(start, curTeam);
      }
      return;
    }

    if (pieceValue == 1 && attackedPieceValue == 10) { // Slayer
      capturePiece(end, oppTeam);
      board.swapPieces(start, end);
    } else if (pieceValue > attackedPieceValue) {
      capturePiece(end, oppTeam);
      board.swapPieces(start, end);
    } else if (pieceValue == attackedPieceValue) {
      capturePiece(start, curTeam);
      capturePiece(end, oppTeam);
    } else /* pieceValue < attackedPieceValue */ {
      capturePiece(start, curTeam);
    }
  }

  // Wrapper for board.removePiece. Also updates captured piece fields
  private void capturePiece(Coord pos, char team) {
    if (team == 'r') {
      capturedReds.add(board.getPiece(pos));
    } else {
      capturedBlues.add(board.getPiece(pos));
    }
    board.removePiece(pos);
  }


  /* Helper methods */
  private char opposingTeam(char team) {
    if (team == 'r') {
      return 'b';
    } else {
      return 'r';
    }
  }
}
