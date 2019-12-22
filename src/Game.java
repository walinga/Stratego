import java.util.*;

class Game {
  Board board;
  char turn;
  boolean started;
  char winner;

  Piece lastCapture;
  List<Piece> capturedBlues;
  List<Piece> capturedReds;

  public Game(Board board) {
    this.board = board;
    capturedBlues = new ArrayList<>();
    capturedReds = new ArrayList<>();
    started = false;
  }

  public void start() {
    turn = 'r';
    started = true;
  }

  // Don't check the started boolean here. We may eventually want to call this method
  // before the board is set up (eg. to check for a setup where no moves are possible)
  public Set<Coord> getValidMoves(Coord start) {
    return board.getValidMoves(start);
  }

  public char getTurn() {
    return turn;
  }

  public List<Piece> getCaptured() {
    List<Piece> allCaptured = new ArrayList<>(
      capturedReds
    );
    allCaptured.addAll(capturedBlues);
    return allCaptured;
  }

  public Piece getLastCapture() {
    return lastCapture;
  }

  // Called by the API to move a piece
  // Returns true iff the game is over after this move. To simplify things: there are no draws,
  // if the next player cannot make a move, that player loses
  public boolean makeMove(Coord start, Coord end) {
    if (!started
      || !board.isMoveAllowed(start, end)
      // If the move is allowed, we know that there is a piece on start
      || board.getPiece(start).getTeam() != turn
      || !getValidMoves(start).contains(end)
    ) {
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
      winner = turn;
      return true;
    }

    performCapture(start, end, pieceValue, attackedPieceValue);

    if (capturedBlues.size() == 30 || capturedReds.size() == 30) {
      winner = capturedBlues.size() == 30 ? 'r' : 'b';
      return true;
    }

    // End the game if the opposing player can't make any moves
    if (board.getAllMoves(opposingTeam(turn)).isEmpty()) {
      winner = turn;
      return true;
    }

    turn = opposingTeam(turn);
    return false;
  }

  public char getWinner() {
    // Will return '\u0000' if the game is not over yet
    return winner;
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
    lastCapture = board.getPiece(pos);
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
