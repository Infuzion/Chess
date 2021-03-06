/*
 * Copyright 2020 Srikavin Ramkumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.infuzion.chess.game.board;

import me.infuzion.chess.game.piece.PieceType;
import org.jetbrains.annotations.Nullable;

public class ChessMove {
    private final ChessPosition source;
    private final ChessPosition end;
    private final PieceType promotion;

    public ChessMove(String source, String end) {
        this(new ChessPosition(source), new ChessPosition(end));
    }

    public ChessMove(ChessPosition source, ChessPosition end) {
        this.source = source;
        this.end = end;
        this.promotion = null;
    }

    public ChessMove(ChessPosition source, ChessPosition end, @Nullable PieceType promotion) {
        this.source = source;
        this.end = end;
        this.promotion = promotion;
    }

    public ChessPosition getSource() {
        return source;
    }

    public ChessPosition getEnd() {
        return end;
    }

    @Nullable
    public PieceType getPromotion() {
        return promotion;
    }

    @Override
    public String toString() {
        return "ChessMove{" +
                "source=" + source +
                ", end=" + end +
                ", promotion=" + promotion +
                '}';
    }
}
