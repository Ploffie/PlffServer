package nl.plff.plffserver.server.extensions;

import nl.plff.plffserver.server.PlffServer;

public abstract class PlffExtension {

    private PlffServer server;

    public final void setServer(PlffServer server) {
        this.server = server;
    }

    public abstract void registerParcels();

    protected final PlffServer getServer() {
        return server;
    }

}
