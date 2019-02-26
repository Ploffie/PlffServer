package nl.plff.plffserver.server.conn;

import nl.plff.plffserver.parcel.Parcel;
import nl.plff.plffserver.parcel.core.HeartbeatParcel;
import nl.plff.plffserver.parcel.core.UnknownObjectParcel;
import nl.plff.plffserver.parcel.core.WelcomeParcel;
import nl.plff.plffserver.server.PlffServer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.Socket;

public class ServerConnection extends Connection {

    // Remember server to send data to
    private final PlffServer server;
    private final Heartbeat heartbeat;

    public ServerConnection(PlffServer server, Socket socket) throws IOException {
        super(socket);
        this.server = server;
        this.heartbeat = new Heartbeat(this);
        this.heartbeat.start();
    }

    @Override
    public void run() {
        try {
            // Client initiates connection. but server initiates welcome sequence
            sendParcel(new WelcomeParcel("Welcome to this awesome chat server!"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (isOpen) {
            try {
                Parcel p = (Parcel) receiveObject();
                if (p instanceof HeartbeatParcel) {
                    heartbeat.lastHeartbeat = System.currentTimeMillis();
                    continue;
                    // Heartbeat parcels should never make it to the processor and should be handled by the connection itself
                    // This does bake heartbeat into the core of the code but maybe that's a good thing
                }
                p.setSender(this);
                server.getParcelUnit().processParcel(p);
            } catch (IOException | ClassNotFoundException ignored) {
                try {
                    sendParcel(new UnknownObjectParcel());
                } catch (IOException ignored1) {
                    server.unregisterConnection(this);
                    isOpen = false;
                }
            }
        }
        try {
            close();
        } catch (InterruptedException | IOException ignored) {
        }
    }

    @Override
    protected void close() throws InterruptedException, IOException {
        heartbeat.timer.stop();
        heartbeat.join();
        super.close();
    }

    private class Heartbeat extends Thread {
        private final ServerConnection conn;
        private int timeToRespond = 3 * 1000;
        private long lastHeartbeat;
        private Timer timer = new Timer(1000 * 60, this::sendHeartbeatActionListener);

        private Heartbeat(ServerConnection connection) {
            this.conn = connection;
        }

        @Override
        public void run() {
            timer.setRepeats(true);
            timer.start();
        }

        @SuppressWarnings("unused")
        private void sendHeartbeatActionListener(ActionEvent ignored) {
            try {
                sendParcel(new HeartbeatParcel());
                Timer responseTimer = new Timer(timeToRespond - 50, this::receiveHeartbeatActionListener);
                responseTimer.setRepeats(false);
                responseTimer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @SuppressWarnings("unused")
        private void receiveHeartbeatActionListener(ActionEvent ignored) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - timeToRespond > lastHeartbeat) {
                try {
                    close();
                    server.unregisterConnection(conn);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
