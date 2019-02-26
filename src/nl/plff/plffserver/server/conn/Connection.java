package nl.plff.plffserver.server.conn;

import nl.plff.plffserver.parcel.Parcel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class Connection extends Thread {

    private final Socket socket;
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;
    boolean isOpen;

    protected Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
        outputStream.flush();
        isOpen = true;
    }

    public synchronized void sendParcel(Parcel p) throws IOException {
        writeObject(p);
    }

    protected void close() throws InterruptedException, IOException {
        join();
        isOpen = false;
        socket.close();
        outputStream.close();
        inputStream.close();
    }

    protected Object receiveObject() throws IOException, ClassNotFoundException {
        Object o = inputStream.readObject();
        System.out.println("Connection receive object with class " + o.getClass().getSimpleName());
        return o;
    }

    private void writeObject(Object o) throws IOException {
        System.out.println("Connection write object with class " + o.getClass().getSimpleName());
        outputStream.writeObject(o);
        outputStream.flush();
    }
}
