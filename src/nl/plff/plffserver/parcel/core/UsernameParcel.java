package nl.plff.plffserver.parcel.core;

import nl.plff.plffserver.parcel.Parcel;

public class UsernameParcel extends Parcel {

    private final String username;

    public UsernameParcel(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

}
