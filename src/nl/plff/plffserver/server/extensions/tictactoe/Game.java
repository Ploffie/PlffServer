package nl.plff.plffserver.server.extensions.tictactoe;

import nl.plff.plffserver.parcel.tictactoe.*;
import nl.plff.plffserver.server.PlffServer;
import nl.plff.plffserver.server.conn.Connection;

import java.io.IOException;
import java.util.*;

class Game extends Thread {

    private final PlffServer server;
    private final HashMap<String, Connection> players;
    private final UUID gameId;
    private Status status;
    private Queue<MoveParcel> moves;

    Game(PlffServer server, String challenger, String challenged, UUID gameId) {
        this.server = server;
        this.gameId = gameId;
        this.players = new HashMap<>(2);
        this.moves = new LinkedList<>();

        Connection challengerConn = server.getConnection(challenger);
        Connection challengedConn = server.getConnection(challenged);
        if (challengedConn == null) {
            // Challenged user does not exist
            try {
                server.getConnection(challenger).sendParcel(new UnknownUserParcel(challenged));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                status = Status.CANCELLED;
            }
            return;
        }
        players.put(challenged, challengedConn);
        players.put(challenger, challengerConn);

        try {
            players.get(challenger).sendParcel(new ChallengeConfirmParcel(gameId, challenged));
            players.get(challenged).sendParcel(new NewChallengeParcel(gameId, challenger));
        } catch (IOException e) {
            e.printStackTrace();
        }
        status = Status.AWAITING_RESPONSE;
    }

    @Override
    public void run() {
        Side currentTurn;
        HashMap<String, Side> sides = new HashMap<>(2);

        try {
            for (String s : players.keySet()) {
                if (sides.isEmpty()) {
                    sides.put(s, Side.X);
                } else {
                    sides.put(s, Side.O);
                }
                players.get(s).sendParcel(new AssignSideParcel(gameId, sides.get(s)));
            }

            setStatus(Status.IN_PROGRESS);
            Side[][] board = new Side[3][3];
            currentTurn = Side.X;

            while (status == Status.IN_PROGRESS) {
                synchronized (this) {
                    this.wait();
                    while (!moves.isEmpty()) {
                        MoveParcel move = moves.poll();
                        String mover = server.getUsername(move.getSender());
                        if (!sides.get(mover).equals(currentTurn)) {
                            move.getSender().sendParcel(new IllegalMoveParcel());
                            break;
                        }

                        if (board[move.getRow()][move.getColumn()] != null) {
                            // If position is already occupied
                            move.getSender().sendParcel(new IllegalMoveParcel());
                            break;
                        }

                        board[move.getRow()][move.getColumn()] = currentTurn;
                        currentTurn = currentTurn == Side.X ? Side.O : Side.X;
                        getOpponent(mover).sendParcel(move);
                        // Check if somebody won
                        for (int i = 0; i < board.length; i++) {
                            if (board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][2] != null) {
                                // Victory on row ${i}
                                Side winner = board[i][0];
                                for (Connection c : players.values()) {
                                    c.sendParcel(new GameOverParcel(gameId, winner));
                                }
                                status = Status.FINISHED;
                                return;
                            }
                            if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[2][i] != null) {
                                Side winner = board[0][i];
                                for (Connection c : players.values()) {
                                    c.sendParcel(new GameOverParcel(gameId, winner));
                                }
                                status = Status.FINISHED;
                                return;
                            }
                        }

                        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[2][2] != null) {
                            Side winner = board[0][0];
                            for (Connection c : players.values()) {
                                c.sendParcel(new GameOverParcel(gameId, winner));
                            }
                            status = Status.FINISHED;
                            return;
                        }

                        if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[2][0] != null) {
                            Side winner = board[2][0];
                            for (Connection c : players.values()) {
                                c.sendParcel(new GameOverParcel(gameId, winner));
                            }
                            status = Status.FINISHED;
                            return;
                        }
                        boolean allTilesFilled = true;
                        for (Side[] s : board) {
                            for (Side side : s) {
                                if (side == null) {
                                    allTilesFilled = false;
                                    break;
                                }
                            }
                        }
                        if(allTilesFilled) {
                            for (Connection c : players.values()) {
                                c.sendParcel(new GameOverParcel(gameId, null));
                            }
                            status = Status.FINISHED;
                            return;
                        }
                    }
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    void onMove(MoveParcel parcel) {
        moves.add(parcel);
        synchronized (this) {
            this.notify();
        }
    }

    // Only works for 2 player games
    Connection getOpponent(String username) {
        for (Map.Entry e : players.entrySet()) {
            if (!e.getKey().equals(username)) return (Connection) e.getValue();
        }
        return null;
    }

    void setStatus(Status s) {
        status = s;
    }
}

/*
S: AssignSideParcel(UUID gameId, Side.X | Side.O) // X always starts
// X: Player X
// O: Player O
WHILE PLAYING
<wait>
X: MoveParcel(UUID gameId, int row, int column)
S: <forward>
<wait>
O: MoveParcel(UUID gameId, int row, int column)
S: <forward>
END WHILE

S: GameOverParcel(UUID gameId, Side winner)
*/