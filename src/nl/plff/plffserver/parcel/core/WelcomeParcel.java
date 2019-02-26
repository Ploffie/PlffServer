package nl.plff.plffserver.parcel.core;

import nl.plff.plffserver.parcel.Parcel;

public class WelcomeParcel extends Parcel {

    private final String welcomeMessage;

    public WelcomeParcel(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

}
