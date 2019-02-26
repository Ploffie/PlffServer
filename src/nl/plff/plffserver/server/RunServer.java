package nl.plff.plffserver.server;

import nl.plff.plffserver.server.extensions.tictactoe.TicTacToe;

public class RunServer {

    public static void main(String[] args) {
        PlffServer server = new PlffServer();
        server.registerExtension(TicTacToe.class);
        server.run();
    }

}
