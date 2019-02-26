package nl.plff.plffserver.parcel.tictactoe;

import nl.plff.plffserver.parcel.Parcel;

import java.util.UUID;

public class ChallengeConfirmParcel extends Parcel {
    private final UUID gameId;
    private final String opponent;

    public ChallengeConfirmParcel(UUID gameId, String opponent) {
        this.gameId = gameId;
        this.opponent = opponent;
    }

    public UUID getGameId() {
        return gameId;
    }

    public String getOpponent() {
        return opponent;
    }
}
