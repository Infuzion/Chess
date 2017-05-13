package me.infuzion.chess.piece;

import me.infuzion.chess.board.ChessPosition;
import me.infuzion.chess.piece.movement.MoveTypes;

public class King extends ChessPiece {

    public King(Color color, ChessPosition position) {
        super(color, position);
        setMovementTypes(MoveTypes.KING_MOVEMENT);
        setType(PieceType.KING);
    }

}
