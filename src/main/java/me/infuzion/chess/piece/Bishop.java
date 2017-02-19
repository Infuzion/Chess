package me.infuzion.chess.piece;

import me.infuzion.chess.ChessPiece;
import me.infuzion.chess.ChessPosition;
import me.infuzion.chess.piece.movement.MoveTypes;

public class Bishop extends ChessPiece {

    public Bishop(Color color, ChessPosition position) {
        super(color, position);
        setMovementTypes(MoveTypes.DIAGONAL_MOVEMENT);
    }

    public PieceType getType() {
        return PieceType.BISHOP;
    }
}
