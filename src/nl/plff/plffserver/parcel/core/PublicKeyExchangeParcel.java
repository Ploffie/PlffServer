package nl.plff.plffserver.parcel.core;

import nl.plff.plffserver.parcel.Parcel;

import java.security.PublicKey;

public class PublicKeyExchangeParcel extends Parcel {

    private final PublicKey key;

    public PublicKeyExchangeParcel(PublicKey pubKey) {
        this.key = pubKey;
    }

    public PublicKey getKey() {
        return key;
    }
}
