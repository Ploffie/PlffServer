package nl.plff.plffserver.parcel;

import com.sun.istack.internal.Nullable;
import nl.plff.plffserver.server.conn.Connection;

import java.io.*;

public abstract class Parcel implements Serializable {

    private transient Connection sender;

    @Nullable
    public final Connection getSender() {
        return sender;
    }

    public final void setSender(Connection conn) {
        this.sender = conn;
    }

}
