package rest;

import db.datasets.UserDataSet;
import game.GameUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
@SuppressWarnings({"unused"})
public class UserProfile {
    @NotNull
    private Long id = -1L;
    @NotNull
    private String username = "";
    @NotNull
    private String password = "";
    @NotNull
    private Long score = 0L;
    @NotNull
    private Long points = 0L;
    @Nullable
    private String answer = null;
    @NotNull
    private Boolean starBf = false;
    @NotNull
    private Boolean accuracyBf = false;
    @NotNull
    private Boolean speedBf = false;
    @NotNull
    private Boolean delayBf = false;

    public UserProfile() {
    }

    public UserProfile(@NotNull UserDataSet dataSet) {
        id = dataSet.getId();
        username = dataSet.getUsername();
        password = dataSet.getPassword();
        score = dataSet.getScore();
        points = dataSet.getPoints();
        answer = dataSet.getAnswer();
        starBf = dataSet.getStarBf();
        accuracyBf = dataSet.getAccuracyBf();
        speedBf = dataSet.getSpeedBf();
        delayBf = dataSet.getDelayBf();
    }

    public UserProfile(@NotNull String username, @NotNull String password) {
        this.username = username;
        this.password = password;
    }

    public UserProfile(@NotNull String username, @NotNull String password, @NotNull Long id) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public UserProfile(@NotNull String answer) {
        this.answer = answer;
    }

    public UserProfile(@NotNull GameUser gameUser) {
        this.score = (long) gameUser.getMyHeight();
    }

    @NotNull
    public Long getId() {
        return id;
    }

    public void setId(@NotNull Long id) {
        this.id = id;
    }

    @NotNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NotNull String username) {
        this.username = username;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }

    @NotNull
    public Long getScore() {
        return this.score;
    }

    public void setScore(@NotNull Long score) {
        this.score = score;
    }

    @NotNull
    public Long getPoints() {
        return this.points;
    }

    public void setPoints(@NotNull Long points) {
        this.points = points;
    }

    @Nullable
    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(@NotNull String answer) {
        this.answer = answer;
    }

    @NotNull
    public Boolean getStarBf() {
        return starBf;
    }

    public void setStarBf(@NotNull Boolean starBf) {
        this.starBf = starBf;
    }

    @NotNull
    public Boolean getAccuracyBf() {
        return accuracyBf;
    }

    public void setAccuracyBf(@NotNull Boolean accuracyBf) {
        this.accuracyBf = accuracyBf;
    }

    @NotNull
    public Boolean getSpeedBf() {
        return speedBf;
    }

    public void setSpeedBf(@NotNull Boolean speedBf) {
        this.speedBf = speedBf;
    }

    @NotNull
    public Boolean getDelayBf() {
        return delayBf;
    }

    public void setDelayBf(@NotNull Boolean delayBf) {
        this.delayBf = delayBf;
    }


    public boolean checkUsername() {
        final Pattern p = Pattern.compile("^[A-Za-z0-9]{1,32}$");
        final Matcher m = p.matcher(this.username);
        return m.matches();
    }

    public boolean checkPassword() {
        final Pattern p = Pattern.compile("^[A-Za-z0-9]{6,32}$");
        final Matcher m = p.matcher(this.password);
        return m.matches();
    }
}
