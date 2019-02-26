package nl.plff.plffserver.parcel.tictactoe;

import nl.plff.plffserver.parcel.Parcel;
import nl.plff.plffserver.server.extensions.tictactoe.Status;

import java.util.UUID;

public class ChallengeStatusParcel extends Parcel {

    private final UUID challengeId;
    private final Status status;

    public ChallengeStatusParcel(UUID challengeId, Status status) {
        this.challengeId = challengeId;
        this.status = status;
    }

    public UUID getChallengeId() {
        return challengeId;
    }

    public Status getStatus() {
        return status;
    }

}
