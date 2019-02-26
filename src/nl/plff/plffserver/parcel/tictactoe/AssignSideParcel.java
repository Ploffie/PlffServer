package nl.plff.plffserver.parcel.tictactoe;

import nl.plff.plffserver.parcel.Parcel;
import nl.plff.plffserver.server.extensions.tictactoe.Side;

import java.util.UUID;

public class AssignSideParcel extends Parcel {
    private final Side side;
    private final UUID gameId;

    public AssignSideParcel(UUID gameId, Side side) {
        this.side = side;
        this.gameId = gameId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public Side getSide() {
        return side;
    }
}
