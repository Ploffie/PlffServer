package nl.plff.plffserver.server;

import com.sun.istack.internal.Nullable;
import nl.plff.plffserver.parcel.Parcel;
import nl.plff.plffserver.parcel.UserActionParcel;
import nl.plff.plffserver.parcel.core.OkParcel;
import nl.plff.plffserver.parcel.core.UnknownObjectParcel;
import nl.plff.plffserver.parcel.core.UsernameParcel;
import nl.plff.plffserver.parcel.core.WelcomeParcel;
import nl.plff.plffserver.parcel.processor.ParcelUnit;
import nl.plff.plffserver.server.conn.Connection;
import nl.plff.plffserver.server.conn.ServerConnection;
import nl.plff.plffserver.server.exception.InvalidUsernameException;
import nl.plff.plffserver.server.extensions.PlffExtension;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class PlffServer {
    private static final int SERVER_PORT = 1337;
    private final Map<String, Connection> connections = new HashMap<>();
    private ParcelUnit parcelUnit = ParcelUnit.getInstance();

    void run() {
        System.out.println("Server starting...");

        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
        } catch (IOException e) {
            System.err.println("Failed to open server! Is the port already in use?");
            return;
        }

        registerParcels();

        //noinspection InfiniteLoopStatement we close thread if stop so is ok
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Attempting connection with client...");
                ServerConnection connection = new ServerConnection(this, clientSocket);
                connection.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void registerExtension(Class<? extends PlffExtension> extension) {
        PlffExtension e;
        try {
            e = extension.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            ex.printStackTrace();
            return;
        }
        e.setServer(this);
        e.registerParcels();
    }

    private synchronized void registerConnection(String username, Connection connection) throws InvalidUsernameException {
        if (isInvalid(username)) throw new InvalidUsernameException();
        connections.put(username, connection);
        System.out.println("Registering connection with username " + username);
        try {
            broadcastParcel(new UserActionParcel(UserActionParcel.UserAction.USER_JOINED, username));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void unregisterConnection(Connection conn) {
        String username = "";
        synchronized (connections) {
            for (Map.Entry<String, Connection> entry : connections.entrySet()) {
                if (entry.getValue().equals(conn)) {
                    username = entry.getKey();
                    System.out.println("Unregistering user " + entry.getKey());
                    break;
                }
            }
        }
        connections.remove(username);
        try {
            broadcastParcel(new UserActionParcel(UserActionParcel.UserAction.USER_LEFT, username));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public Connection getConnection(String username) {
        return connections.get(username);
    }

    @Nullable
    public String getUsername(Connection connection) {
        for (Map.Entry<String, Connection> entry : connections.entrySet()) {
            if (entry.getValue().equals(connection)) return entry.getKey();
        }
        return null;
    }

    public ParcelUnit getParcelUnit() {
        return parcelUnit;
    }

    private void broadcastParcel(Parcel parcel) throws IOException {
        synchronized (connections) { // Synchronized on connections so no concurrent exception when user leaves during broadcast
            for (Connection connection : connections.values()) {
                connection.sendParcel(parcel);
            }
        }
    }

    /**
     * Register all parcels to parcel unit. Only call this method once.
     */
    private void registerParcels() {
        parcelUnit.registerParcel(UsernameParcel.class, this::handleUsernameParcel);
        parcelUnit.registerParcel(UnknownObjectParcel.class, p -> System.err.println("Received unknown object with class " + p.getClass().getSimpleName()));
    }

    // MARK: Parcel handlers
    private void handleUsernameParcel(Parcel p) throws IOException {
        Connection sender = p.getSender();
        if (connections.containsValue(sender)) {
            sender.sendParcel(new UnknownObjectParcel());
            return;
        }

        UsernameParcel parcel = (UsernameParcel) p;
        if (isInvalid(parcel.getUsername())) {
            sender.sendParcel(new WelcomeParcel("Username is invalid. Please try again."));
        } else {
            try {
                registerConnection(parcel.getUsername(), sender);
                sender.sendParcel(new OkParcel());
            } catch (InvalidUsernameException e) {
                sender.sendParcel(new WelcomeParcel("Username invalid. Please try again."));
            }
        }
    }

    public String[] getOnlineUsers() {
        String[] result = new String[connections.keySet().size()];
        connections.keySet().toArray(result);
        return result;
    }

    private boolean isInvalid(String username) {
        if (!username.matches("\\w+")) {
            return true;
        }

        for (String s : connections.keySet()) {
            if (s.equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }
}
