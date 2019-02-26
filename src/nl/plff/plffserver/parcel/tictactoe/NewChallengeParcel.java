package nl.plff.plffserver.parcel.tictactoe;

import nl.plff.plffserver.parcel.Parcel;

import java.util.UUID;

public class NewChallengeParcel extends Parcel {

    private final String username;
    private final UUID challengeId;

    public NewChallengeParcel(String username) {
        this.username = username;
        this.challengeId = null;
    }
    public NewChallengeParcel(UUID challengeId, String username) {
        this.username = username;
        this.challengeId = challengeId;
    }

    public String getUsername() {
        return username;
    }

    public UUID getChallengeId() {
        return challengeId;
    }
}
