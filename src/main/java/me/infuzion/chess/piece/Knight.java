package me.infuzion.chess.piece;

import me.infuzion.chess.board.ChessPosition;
import me.infuzion.chess.piece.movement.MoveTypes;

public class Knight extends ChessPiece {

    public Knight(Color color, ChessPosition position) {
        super(color, position);
        setMovementTypes(MoveTypes.KNIGHT_MOVEMENT);
        setType(PieceType.KNIGHT);
    }
}
