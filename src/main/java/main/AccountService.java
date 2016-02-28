package main;

import rest.UserProfile;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
public class AccountService {
    private Map<String, UserProfile> users = new HashMap<>();

    public AccountService() {
        users.put("Tolya", new UserProfile("Tolya", "12345"));
        users.put("Kolya", new UserProfile("Kolya", "12345"));
        users.put("Lesha", new UserProfile("Lesha", "12345"));
    }

    public Collection<UserProfile> getAllUsers() {
        return users.values();
    }

    public boolean addUser(String userName, UserProfile userProfile) {
        if (users.containsKey(userName))
            return false;
        users.put(userName, userProfile);
        return true;
    }

    public UserProfile getUser(String userName) {
        return users.get(userName);
    }
}