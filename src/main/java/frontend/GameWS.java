package frontend;

import base.AccountService;
import com.google.gson.*;

import game.GameMechanicsImpl;
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

import java.io.IOException;

/**
 * Created by Olerdrive on 29.05.16.
 */
public class GameWS {

    private static final Logger LOGGER = LogManager.getLogger(GameWS.class);

    @Nullable
    private Session session;
    @NotNull
    private final GameMechanicsImpl gameMechanics;
    private final WSServiceImpl webSocketService;
    private final AccountService accountService;

    @NotNull
    private final String myName;

    public GameWS(@NotNull String myName, Context context) {
        this.myName = myName;
        this.gameMechanics = context.get(GameMechanicsImpl.class);
        this.webSocketService = context.get(WSServiceImpl.class);
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
        json.addProperty("enemyRecord", accountService.getUserByLogin(user.getEnemyName()).getScore());

        sendJson(json);
    }

    public void gameOver(boolean equality, boolean win) {

        final JsonObject jsonEndGame = new JsonObject();
        jsonEndGame.addProperty("action", "finishGame");
        if (equality) {
            jsonEndGame.addProperty("equality", true);
        } else {
            jsonEndGame.addProperty("win", win);
        }
        final int currentScore = gameMechanics.getMyScore(myName);
        jsonEndGame.addProperty("myName", myName);
        jsonEndGame.addProperty("myScore", currentScore);
        jsonEndGame.addProperty("enemyName", gameMechanics.getEnemyName(myName));
        jsonEndGame.addProperty("enemyScore", gameMechanics.getEnemyScore(myName));


        sendJson(jsonEndGame);

        gameMechanics.removeGameSession(myName);

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
                throw new JsonSyntaxException("Can't find out \"action\" in JSON");
            }
            switch (action) {
                default:
                    throw new JsonSyntaxException("Unknown \"action\"");
            }
        } catch (JsonSyntaxException e) {

            LOGGER.error("Can't find out \"action\" in JSON");
            final JsonObject jsonError = new JsonObject();
            jsonError.addProperty("error", "action is null");
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
        gameMechanics.removeGameSession(myName);
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