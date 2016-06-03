package frontend;

import com.google.gson.*;

import base.*;
import game.GameSession;
import game.GameUser;
import main.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rest.UserProfile;

import java.io.IOException;

/**
 * Created by Olerdrive on 29.05.16.
 */

@WebSocket
public class GameWS {

    private static final Logger LOGGER = LogManager.getLogger(GameWS.class);

    @Nullable
    private Session session;
    @NotNull
    private final GameMechanics gameMechanics;
    private final WSService webSocketService;
    private final AccountService accountService;

    @NotNull
    private final String myName;

    public GameWS(@NotNull String myName, Context context) {
        this.myName = myName;
        this.gameMechanics = context.get(GameMechanics.class);
        this.webSocketService = context.get(WSService.class);
        this.accountService = context.get(AccountService.class);
        LOGGER.info("Socket created for " + myName);


    }

    @NotNull
    public String getMyName() {
        return myName;
    }

    public void startGame(@NotNull GameUser user) {

        final JsonObject json = new JsonObject();
        json.addProperty("action", "startGame");
        json.addProperty("user", user.getMyName());
        //noinspection ConstantConditions
        json.addProperty("userHighScore", accountService.getUserByLogin(user.getMyName()).getScore());
        json.addProperty("enemy", user.getEnemyName());
        //noinspection ConstantConditions
        json.addProperty("enemyHighScore", accountService.getUserByLogin(user.getEnemyName()).getScore());

        sendJson(json);
    }

    public void gameOver(boolean equality, boolean firstWinBF) {

        final JsonObject jsonEndGame = new JsonObject();
        final String enemyName = gameMechanics.getEnemyName(myName);
        jsonEndGame.addProperty("action", "finishGame");
        if (equality) {
            jsonEndGame.addProperty("equality", true);
        } else {
            jsonEndGame.addProperty("isWinner", firstWinBF);
        }
        final int currentScore = gameMechanics.getMyScore(myName);
        jsonEndGame.addProperty("myName", myName);
        jsonEndGame.addProperty("myScore", currentScore);
        jsonEndGame.addProperty("myLeadCount", gameMechanics.getMyLeadCount(myName));
        jsonEndGame.addProperty("enemyName", enemyName);
        jsonEndGame.addProperty("enemyScore", gameMechanics.getEnemyScore(myName));
        jsonEndGame.addProperty("enemyLeadCount", gameMechanics.getEnemyLeadCount(myName));


        sendJson(jsonEndGame);

        final GameSession currentSession = gameMechanics.getGameSession(myName);
        final int multiplier = gameMechanics.countMultiplier(myName);
        saveResults(currentSession.getSelf(myName), multiplier);
        //gameMechanics.removeGameSession(myName);

    }

    public void saveResults(GameUser user1, int multiplier){
        LOGGER.info("Saving results for " + user1.getMyName());
        LOGGER.info("Scores: {} x {}", user1.getMyHeight(), multiplier);
        final UserProfile user1Profile = accountService.getUserByLogin(user1.getMyName());
        if (user1Profile != null) {
            final long user1Id = user1Profile.getId();
            accountService.updateUser(user1Id, new UserProfile(user1), multiplier);
        }

    }

    public void finishGameEnemyleft() {
        final JsonObject json = new JsonObject();
        json.add("action", new JsonPrimitive("enemyLeft"));
        sendJson(json);

        webSocketService.removeUser(this);
        gameMechanics.removeGameSession(myName);
    }

    @SuppressWarnings("unused")
    @OnWebSocketMessage
    public void onMessage(String data) {
        try {
            final JsonElement jsonElement = new JsonParser().parse(data);
            final String action = jsonElement.getAsJsonObject().getAsJsonPrimitive("action").getAsString();
            if (action == null) {
                throw new JsonSyntaxException("Can't understand \"action\" in JSON");
            }
            switch (action) {
                case "build":
                {
                    final String builderName = jsonElement.getAsJsonObject().getAsJsonPrimitive("username").getAsString();

                    if (!gameMechanics.getGameSession(builderName).isGameOver()) {

                        //LOGGER.info("User {} builds up", builderName);
                        gameMechanics.incrementScore(builderName);
                        final JsonObject json = new JsonObject();
                        json.add("action", new JsonPrimitive("buildOK"));
                        json.add("username", new JsonPrimitive(builderName));
                        json.add("height", new JsonPrimitive(gameMechanics.getMyScore(builderName)));
                        final GameSession currentSession = gameMechanics.getGameSession(myName);
                        webSocketService.notifyUserBuilds(currentSession.getFirst(), json);
                        webSocketService.notifyUserBuilds(currentSession.getSecond(), json);
                    }
                    break;
                }
                case "miss":
                {
                    final String looserName = jsonElement.getAsJsonObject().getAsJsonPrimitive("username").getAsString();
                    LOGGER.info("User {} misses", looserName);
                    gameMechanics.setLooser(looserName);
                    break;

                }
                default:
                    throw new JsonSyntaxException("Unknown \"action\"");
            }
        } catch (JsonSyntaxException e) {

            LOGGER.error(e);
            final JsonObject jsonError = new JsonObject();
            jsonError.addProperty("error", "action is invalid");
            sendJson(jsonError);
        }
    }

    @SuppressWarnings({"ParameterHidesMemberVariable", "unused"})
    @OnWebSocketConnect
    public void onOpen(@NotNull Session session) {
        this.session = session;
        webSocketService.addUser(this);
        gameMechanics.addUser(myName);

    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        webSocketService.removeUser(this);
        gameMechanics.removeUser(myName);
        if (gameMechanics.getGameSession(myName) != null) {
            gameMechanics.removeGameSession(myName);
        }
        LOGGER.info("Closing socket for: {}  status: {} reason: {}", myName, statusCode, reason);
    }

    public void sendJson(JsonObject json) {
        try {
            if (session != null && session.isOpen())
                session.getRemote().sendString(json.toString());
        } catch (IOException | WebSocketException e) {
            LOGGER.error("Can't send web socket", e);
        }
    }
}