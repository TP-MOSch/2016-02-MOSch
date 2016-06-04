package db.datasets;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rest.UserProfile;

import javax.persistence.*;
import java.io.Serializable;

/**
 * MOSch-team test server for "Kill The Birds" game
 * Users {
 * id: int
 * username: string
 * password: string
 * score: int
 * points: int
 * auth_token: string
 * star_bf: bool
 * accuracy_bf: bool
 * speed_bf: bool
 * delay_bf: bool
 * }
 */
@SuppressWarnings("unused")
@Entity
@Table(name = "users")
public class UserDataSet implements Serializable { // Serializable Important to Hibernate!
    private static final long serialVersionUID = -8706689714326132798L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "score", nullable = false)
    private Long score = 0L;

    @Column(name = "points", nullable = false)
    private Long points = 0L;

    @Column(name = "auth_token", unique = true)
    private String authToken;

    @Column(name = "answer")
    private String answer = null;

    @Column(name = "star_bf", nullable = false)
    private Boolean starBf = false;

    @Column(name = "accuracy_bf", nullable = false)
    private Boolean accuracyBf = false;

    @Column(name = "speed_bf", nullable = false)
    private Boolean speedBf = false;

    @Column(name = "delay_bf", nullable = false)
    private Boolean delayBf = false;

    //Important to Hibernate!
    public UserDataSet() {
    }

    public UserDataSet(@NotNull String username) {
        this.id = -1L;
        this.username = username;
    }

    public UserDataSet(@NotNull String username, @NotNull String password) {
        this.id = -1L;
        this.username = username;
        this.password = password;
    }

    public UserDataSet(@NotNull UserProfile user) {
        this.id = -1L;
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.score = user.getScore();
        this.points = user.getPoints();
        this.answer = user.getAnswer();
        this.authToken = null;
        this.starBf = user.getStarBf();
        this.accuracyBf = user.getAccuracyBf();
        this.speedBf = user.getSpeedBf();
        this.delayBf = user.getDelayBf();
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
    public String getAuthToken() {
        return this.authToken;
    }

    public void setAuthToken(@Nullable String authToken) {
        this.authToken = authToken;
    }

    @NotNull
    public Boolean getStarBf() {
        return this.starBf;
    }

    public void setStarBf(@NotNull Boolean starBf) {
        this.starBf = starBf;
    }

    @NotNull
    public Boolean getAccuracyBf() {
        return this.accuracyBf;
    }

    public void setAccuracyBf(@NotNull Boolean accuracyBf) {
        this.accuracyBf = accuracyBf;
    }

    @NotNull
    public Boolean getSpeedBf() {
        return this.speedBf;
    }

    public void setSpeedBf(@NotNull Boolean speedBf) {
        this.speedBf = speedBf;
    }

    @NotNull
    public Boolean getDelayBf() {
        return this.delayBf;
    }

    public void setDelayBf(@NotNull Boolean delayBf) {
        this.delayBf = delayBf;
    }

    @Nullable
    public String getAnswer() {
        return this.answer;
    }

    public void setAnswerBf(@NotNull String answer) {
        this.answer = answer;
    }


    @NotNull
    public String toJsonString() {
        return "{id=" + this.id.toString() +
                ",username=\'" + this.username + '\'' +
                ",score=" + this.score.toString() +
                ",points=" + this.points.toString() +
                ",star_bf=" + this.starBf.toString() +
                ",accuracy_bf=" + this.accuracyBf.toString() +
                ",speed_bf=" + this.speedBf.toString() +
                ",delay_bf=" + this.delayBf.toString() +
                '}';
    }
}
