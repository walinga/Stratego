import java.util.*;

class Game {
  Board board;
  char turn;
  boolean started;
  char winner;

  Map<Coord, List<Piece>> lastRevealedPieces;
  Map<Coord, Piece> revealedPowers;
  List<Coord> lastMove; // Should be a pair (start, end)
  List<Piece> capturedBlues;
  List<Piece> capturedReds;

  final Piece nullPiece = new Piece(-1, '0');

  public Game(Board board) {
    this.board = board;
    capturedBlues = new ArrayList<>();
    capturedReds = new ArrayList<>();
    lastRevealedPieces = new HashMap<>();
    revealedPowers = new HashMap<>();
    lastMove = new ArrayList<>();
    started = false;
  }

  public void start() {
    turn = 'r';
    started = true;
  }

  public String getBoardForTeam(char team) {
    String fullBoard = board.toString();
    String lookup = "\\d+" + Character.toString(opposingTeam(team));
    return fullBoard.replaceAll(lookup, "?");
  }

  public Set<Coord> getValidMoves(Coord start, char team) {
    if (!started) {
      return new HashSet<>();
    }
    if (board.getPiece(start) == null || board.getPiece(start).getTeam() != team) {
      return new HashSet<>();
    }
    return board.getValidMoves(start);
  }

  public char getTurn() {
    return turn;
  }

  // TODO: May make more sense for the API to read the captured pieces as separate Red and Blue sets
  public List<Piece> getCaptured() {
    List<Piece> allCaptured = new ArrayList<>(
      capturedReds
    );
    allCaptured.addAll(capturedBlues);
    return allCaptured;
  }

  public Map<Coord, List<Piece>> getLastRevealed() {
    return lastRevealedPieces;
  }

  public Map<Coord, Piece> getLastPowerUser() {
    return revealedPowers;
  }

  public List<Coord> getLastMove() {
    return lastMove;
  }

  public char getWinner() {
    // Will return '\u0000' if the game is not over yet
    return winner;
  }

  public boolean isGameEnded() {
    // The game is over if we have a winner
    return winner == 'r' || winner == 'b';
  }

  // Called by the API to move a piece
  // Returns true iff the game is over after this move. To simplify things: there are no draws,
  // if the next player cannot make a move, that player loses
  public void makeMove(Coord start, Coord end) {
    // TODO (low priority): Also check whether the game ended (need ended boolean)
    if (!started || !board.isMoveAllowed(start, end) || !getValidMoves(start, turn).contains(end)) {
      return;
    }
    // Update the move's revealed pieces so that it stops showing if we moved onto an empty square
    lastRevealedPieces = new HashMap<Coord, List<Piece>>();
    revealedPowers = new HashMap<Coord, Piece>();
    lastMove = new ArrayList<>(List.of(start, end));

    // IDEA: For the 5's rampage, always do it if moving into an empty square with adjacent enemies

    List<Integer> magicUsers = new ArrayList<>(List.of(4, 6, 9));
    int pieceValue = board.getPiece(start).getValue();
    boolean isSpecialMove = !board.oneSpaceAway(start).contains(end);
    if (isSpecialMove) {
      // Reveal the piece to let the opponent know it's making a special move
      // NOTE: Show the end square for 2,5,7,8,10; start square for 4,6,9
      revealedPowers.put(magicUsers.contains(pieceValue) ? start : end, board.getPiece(start));
    }
    if (isSpecialMove && magicUsers.contains(pieceValue)) {
      // Special moves for 4/6/9
      // NOTE: We assume that they are attacking if the piece is one square away
      doSpecialMove(start, end);
    } else if (board.getPiece(end) == null) {
      // Move to an empty square, just move the starting piece
      board.swapPieces(start, end);
    } else {
      lastRevealedPieces.put(end, new ArrayList<>(List.of(
        board.getPiece(start), board.getPiece(end)
      )));

      performCapture(start, end);
    }

    if (capturedBlues.size() == 30 || capturedReds.size() == 30) {
      winner = capturedBlues.size() == 30 ? 'r' : 'b';
    } else if (board.getAllMoves(opposingTeam(turn)).isEmpty()) {
      // End the game if the opposing player can't make any moves
      winner = turn;
    }

    turn = opposingTeam(turn);
  }

  private void performCapture(Coord start, Coord end) {
    char curTeam = this.turn;
    char oppTeam = opposingTeam(this.turn);
    int pieceValue = board.getPiece(start).getValue();
    int attackedPieceValue = board.getPiece(end).getValue();
    if (attackedPieceValue == 11) { // FLAG
      // end the game
      winner = turn;
    } else if (attackedPieceValue == 0) { // Traps
      if (pieceValue == 3) {
        capturePiece(end, oppTeam);
        board.swapPieces(start, end);
      } else {
        capturePiece(start, curTeam);
      }
    } else if (pieceValue == 1 && attackedPieceValue == 10) { // Slayer
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

  private void doSpecialMove(Coord start, Coord end) {
    lastRevealedPieces.put(end, new ArrayList<>(List.of(
      board.getPiece(end)
    )));
    char oppTeam = opposingTeam(this.turn);
    int pieceValue = board.getPiece(start).getValue();
    int attackedPieceValue = board.getPiece(end).getValue();
    // NOTE: The 9 only reveals, nothing more
    if (pieceValue == 6 && attackedPieceValue < 6) {
      board.getPiece(end).switchTeam();
    } else if (pieceValue == 4 && attackedPieceValue < 4) {
      capturePiece(end, oppTeam);
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
