package dbStuff.dataSets;

/**
 * Created by Olerdrive on 30.03.16.
 */
import com.sun.javafx.beans.IDProperty;
import org.jetbrains.annotations.NotNull;
import rest.UserProfile;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "auths")
public class AuthDataSet implements Serializable { // Serializable Important to Hibernate!
    private static final long serialVersionUID = -8706689714326132798L;

    @Id
    @JoinColumn (name = "id")
    private Long id;

    @Column (name = "auth_token", unique = true, nullable = false)
    private String authToken;

    @Column (name = "date", columnDefinition="DATETIME", nullable = false)
    private Date date;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "id")
    private UserDataSet user;

    public AuthDataSet() {
    }

    public AuthDataSet(UserDataSet userDS) {
        this.id = userDS.getId();
        this.authToken = "";
        this.date = new Date();
        this.user = userDS;

    }

    public void setToken(@NotNull String authToken){
        this.authToken = authToken;
    }

    @NotNull
    public String getAuthToken(){
        return this.authToken;
    }

    @NotNull
    public long getId(){
        return this.id;
    }

    @NotNull
    public UserDataSet getUser(){
        return this.user;
    }


}