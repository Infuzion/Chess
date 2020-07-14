package me.infuzion.chess.piece;

import me.infuzion.chess.board.BoardData;
import me.infuzion.chess.board.ChessBoard;
import me.infuzion.chess.board.ChessPosition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KingTest {

    @Test
    void getType() {
        King king = new King(Color.WHITE, new ChessPosition(4, 4));
        assertEquals(king.getType(), PieceType.KING);
    }

    @Test
    void allowed() {
        King king = new King(Color.WHITE, new ChessPosition(4, 4));
        BoardData def = ChessBoard.getDefaultBoard().getData();

        def.setPiece(king.currentPosition(), king);

        assertTrue(king.allowed(def, new ChessPosition(5, 5)));
        assertTrue(king.allowed(def, new ChessPosition(5, 3)));
        assertTrue(king.allowed(def, new ChessPosition(3, 5)));
        assertTrue(king.allowed(def, new ChessPosition(5, 4)));
        assertTrue(king.allowed(def, new ChessPosition(4, 5)));

        assertFalse(king.allowed(def, new ChessPosition(6, 6)));
        assertFalse(king.allowed(def, new ChessPosition(6, 7)));
        assertFalse(king.allowed(def, new ChessPosition(2, 1)));
        assertFalse(king.allowed(def, new ChessPosition(2, 3)));
        assertFalse(king.allowed(def, new ChessPosition(4, 4)));
    }

    @Test
    void whiteQueenSideCastling() {
        King king = new King(Color.WHITE, new ChessPosition("e1"));
        BoardData def = ChessBoard.getDefaultBoard().getData();
        def.setPiece(king.currentPosition(), king);

        assertFalse(king.allowed(def, new ChessPosition("c1")));

        def.setPiece(new ChessPosition("b1"), null);
        def.setPiece(new ChessPosition("c1"), null);
        def.setPiece(new ChessPosition("d1"), null);


        // queen side castle
        assertTrue(king.allowed(def, new ChessPosition("c1")));
        assertFalse(king.allowed(def, new ChessPosition("g1")));
        assertFalse(king.allowed(def, new ChessPosition("c8")));
        assertFalse(king.allowed(def, new ChessPosition("g8")));

        def.getCastlingAvailability().remove(CastlingAvailability.WHITE_QUEEN_SIDE);
        assertFalse(king.allowed(def, new ChessPosition("c1")));
        assertFalse(king.allowed(def, new ChessPosition("g1")));
        assertFalse(king.allowed(def, new ChessPosition("c8")));
        assertFalse(king.allowed(def, new ChessPosition("g8")));
    }

    @Test
    void whiteKingSideCastling() {
        King king = new King(Color.WHITE, new ChessPosition("e1"));
        BoardData def = ChessBoard.getDefaultBoard().getData();
        def.setPiece(king.currentPosition(), king);

        assertFalse(king.allowed(def, new ChessPosition("g1")));

        def.setPiece(new ChessPosition("f1"), null);
        def.setPiece(new ChessPosition("g1"), null);


        // queen side castle
        assertFalse(king.allowed(def, new ChessPosition("c1")));
        assertTrue(king.allowed(def, new ChessPosition("g1")));
        assertFalse(king.allowed(def, new ChessPosition("c8")));
        assertFalse(king.allowed(def, new ChessPosition("g8")));

        def.getCastlingAvailability().remove(CastlingAvailability.WHITE_KING_SIDE);
        assertFalse(king.allowed(def, new ChessPosition("c1")));
        assertFalse(king.allowed(def, new ChessPosition("g1")));
        assertFalse(king.allowed(def, new ChessPosition("c8")));
        assertFalse(king.allowed(def, new ChessPosition("g8")));
    }

    @Test
    void blackQueenSideCastling() {
        King king = new King(Color.BLACK, new ChessPosition("e8"));
        BoardData def = ChessBoard.getDefaultBoard().getData();
        def.setPiece(king.currentPosition(), king);

        assertFalse(king.allowed(def, new ChessPosition("c8")));

        def.setPiece(new ChessPosition("b8"), null);
        def.setPiece(new ChessPosition("c8"), null);
        def.setPiece(new ChessPosition("d8"), null);


        // queen side castle
        assertFalse(king.allowed(def, new ChessPosition("c1")));
        assertFalse(king.allowed(def, new ChessPosition("g1")));
        assertTrue(king.allowed(def, new ChessPosition("c8")));
        assertFalse(king.allowed(def, new ChessPosition("g8")));

        def.getCastlingAvailability().remove(CastlingAvailability.BLACK_QUEEN_SIDE);
        assertFalse(king.allowed(def, new ChessPosition("c1")));
        assertFalse(king.allowed(def, new ChessPosition("g1")));
        assertFalse(king.allowed(def, new ChessPosition("c8")));
        assertFalse(king.allowed(def, new ChessPosition("g8")));
    }

    @Test
    void blackKingSideCastling() {
        King king = new King(Color.BLACK, new ChessPosition("e8"));
        BoardData def = ChessBoard.getDefaultBoard().getData();
        def.setPiece(king.currentPosition(), king);

        assertFalse(king.allowed(def, new ChessPosition("g8")));

        def.setPiece(new ChessPosition("f8"), null);
        def.setPiece(new ChessPosition("g8"), null);


        // queen side castle
        assertFalse(king.allowed(def, new ChessPosition("c1")));
        assertFalse(king.allowed(def, new ChessPosition("g1")));
        assertFalse(king.allowed(def, new ChessPosition("c8")));
        assertTrue(king.allowed(def, new ChessPosition("g8")));

        def.getCastlingAvailability().remove(CastlingAvailability.BLACK_KING_SIDE);
        assertFalse(king.allowed(def, new ChessPosition("c1")));
        assertFalse(king.allowed(def, new ChessPosition("g1")));
        assertFalse(king.allowed(def, new ChessPosition("c8")));
        assertFalse(king.allowed(def, new ChessPosition("g8")));
    }
}