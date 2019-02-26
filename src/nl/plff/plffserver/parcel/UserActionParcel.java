package nl.plff.plffserver.parcel;

public class UserActionParcel extends Parcel {

    private final UserAction action;
    private final String username;

    public UserActionParcel(UserAction action, String username) {
        this.action = action;
        this.username = username;
    }

    public UserAction getAction() {
        return action;
    }

    public String getMessage() {
        return username;
    }

    public enum UserAction {
        USER_JOINED, USER_LEFT
    }

}
