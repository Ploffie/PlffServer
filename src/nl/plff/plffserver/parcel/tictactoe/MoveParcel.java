package nl.plff.plffserver.parcel.tictactoe;

import nl.plff.plffserver.parcel.Parcel;

import java.util.UUID;

public class MoveParcel extends Parcel {
    private final int row, column;
    private final UUID gameId;

    public MoveParcel(UUID gameId, int row, int column) {
        this.gameId = gameId;
        this.row = row;
        this.column = column;
    }

    public UUID getGameId() {
        return gameId;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
