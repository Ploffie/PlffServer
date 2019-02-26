package nl.plff.plffserver.parcel.tictactoe;

import nl.plff.plffserver.parcel.Parcel;

public class UserListParcel extends Parcel {
    private final String[] users;

    public UserListParcel(String[] users) {
        this.users = users;
    }

    public String[] getUsers() {
        return users;
    }
}
