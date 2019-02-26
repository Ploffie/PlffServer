package nl.plff.plffserver.server.extensions.tictactoe;

import nl.plff.plffserver.parcel.Parcel;
import nl.plff.plffserver.parcel.processor.ParcelUnit;
import nl.plff.plffserver.parcel.tictactoe.*;
import nl.plff.plffserver.server.extensions.PlffExtension;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class TicTacToe extends PlffExtension {

    private HashMap<UUID, Game> games = new HashMap<>();

    @Override
    public void registerParcels() {
        ParcelUnit unit = getServer().getParcelUnit();
        unit.registerParcel(RequestUserListParcel.class, this::handleRequestUserListParcel);
        unit.registerParcel(NewChallengeParcel.class, this::handleNewChallengeParcel);
        unit.registerParcel(ChallengeStatusParcel.class, this::handleChallengeStatusParcel);
        unit.registerParcel(MoveParcel.class, this::handleMoveParcel);
    }

    private void handleRequestUserListParcel(Parcel parcel) throws IOException {
        parcel.getSender().sendParcel(new UserListParcel(
                getServer().getOnlineUsers()
        ));
    }

    private void handleNewChallengeParcel(Parcel parcel) {
        NewChallengeParcel p = (NewChallengeParcel) parcel;
        String challenger = getServer().getUsername(p.getSender());
        String opponent = p.getUsername();
        UUID gameId = UUID.randomUUID();
        Game g = new Game(getServer(), challenger, opponent, gameId);
        games.put(gameId, g);
    }

    private void handleChallengeStatusParcel(Parcel parcel) {
        ChallengeStatusParcel p = (ChallengeStatusParcel) parcel;
        Status challengeStatus = p.getStatus();
        Game g = games.get(p.getChallengeId());
        g.setStatus(challengeStatus);
        try {
            g.getOpponent(getServer().getUsername(p.getSender())).sendParcel(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (challengeStatus.equals(Status.ACCEPTED)) {
            g.start();
        }
    }

    private void handleMoveParcel(Parcel parcel) {
        MoveParcel p = (MoveParcel) parcel;
        games.get(p.getGameId()).onMove(p);
    }
}

/* Protocol spec
S: Server
C: Client
---
C: <initiate connection>
S: WelcomeParcel(String welcomeMessage)
C: UsernameParcel(String username)
    S-ERR: WelcomeParcel(String welcomeMessage)
    S: OkParcel
// Here we wait for client to be ready to receive user list
C: RequestUserListParcel()
S: UserListParcel(String[] users)
--- Connection complete
S: UserActionParcel(UserAction.JOIN | UserAction.LEAVE, String username)

<on interrupt>
C: NewChallengeParcel(String username)
    S-ERR: UnknownUserParcel(String username)
    S: NewChallengeParcel(long challengeId, String username) // To both parties, username of other user
    <wait>
    C: ChallengeStatusParcel(UUID challengeId, Status.ACCEPTED | Status.DENIED)
    S: <forward>
--- Challenge done, init game
S: AssignSideParcel(long gameId, Side.X | Side.O) // X always starts
// X: Player X
// O: Player O
WHILE PLAYING
X: MoveParcel(long gameId, int row, int column)
S: <forward>
O: MoveParcel(long gameId, int row, int column)
S: <forward>
END WHILE

S: GameOverParcel(long gameId, Side winner)
*/