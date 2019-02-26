package nl.plff.plffserver.parcel.tictactoe;

import nl.plff.plffserver.parcel.Parcel;

public class UnknownUserParcel extends Parcel {
    private final String username;

    public UnknownUserParcel(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
