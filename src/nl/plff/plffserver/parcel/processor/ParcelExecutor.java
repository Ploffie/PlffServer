package nl.plff.plffserver.parcel.processor;

import nl.plff.plffserver.parcel.Parcel;

import java.io.IOException;

public interface ParcelExecutor {
    void onParcel(Parcel parcel) throws IOException;
}
