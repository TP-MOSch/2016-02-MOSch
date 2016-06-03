package db.datasets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import supportclasses.LoginScoreSet;

import java.util.ArrayList;
import java.util.List;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
public class UserDataSetDAO {
    private final Session session;
    private static final Logger LOGGER = LogManager.getLogger(UserDataSetDAO.class);



    public UserDataSetDAO(Session session) {
        this.session = session;
    }

    public static void logoutAll(Session session) {
        final List dsList = session.createCriteria(UserDataSet.class).list();
        for (Object uDS : dsList) {
            ((UserDataSet) uDS).setAuthToken(null);
        }
    }

    public static void deleteAll(Session session) {
        final List dsList = session.createCriteria(UserDataSet.class).list();
        //noinspection unchecked
        dsList.forEach(session::delete);
    }

    public boolean createUser(@NotNull UserDataSet dataSet) {
        return (session.save(dataSet) != null);
    }

    public void deleteUser(@NotNull Long userID) {
        final UserDataSet user = readUserByID(userID);
        if (user != null) {
            session.delete(user);
        }
    }

    @Nullable
    public UserDataSet readUserByID(@NotNull Long userID) {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        return (UserDataSet) criteria.add(Restrictions.eq("id", userID)).uniqueResult();
    }

    @Nullable
    public UserDataSet readUserBySessionID(@NotNull String sessionID) {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        return (UserDataSet) criteria.add(Restrictions.eq("authToken", sessionID)).uniqueResult();
    }

    @Nullable
    public UserDataSet readUserByLogin(@NotNull String username) {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        return (UserDataSet) criteria.add(Restrictions.eq("username", username)).uniqueResult();
    }

    @SuppressWarnings("MagicNumber")
    public void updateUser(@NotNull Long userID, @NotNull UserDataSet dataSet, @SuppressWarnings("SameParameterValue") @Nullable Integer multiplier) {
        final UserDataSet oldDataSet = readUserByID(userID);
        LOGGER.info("Update method used with answer {}", dataSet.getAnswer());
        if (oldDataSet == null) {
            return;
        }
        final Long newScore = dataSet.getScore();
        if (oldDataSet.getScore() < newScore) {
            oldDataSet.setScore(newScore);
        }
        if (multiplier != null) {
            oldDataSet.setPoints(oldDataSet.getPoints() + multiplier * newScore);
        }

        if (oldDataSet.getAnswer() != "no" && oldDataSet.getAnswer() != "yes") {
            oldDataSet.setAnswerBf(dataSet.getAnswer());
            if (oldDataSet.getAnswer() == "yes") {
                oldDataSet.setPoints(oldDataSet.getPoints() + 1000000L);
                LOGGER.info("User {} hit correct answer. Congrats!", oldDataSet.getUsername());
            }
        }

        if (!oldDataSet.getAccuracyBf() && dataSet.getAccuracyBf() && oldDataSet.getPoints() > 350000L) {
            oldDataSet.setPoints(oldDataSet.getPoints() - 350000L);
            oldDataSet.setAccuracyBf(true);
            LOGGER.info("User {} bought the Hawkeye (Accuracy)", oldDataSet.getUsername());
        }
        if (!oldDataSet.getDelayBf() && dataSet.getDelayBf() && oldDataSet.getPoints() > 250000L) {
            oldDataSet.setPoints(oldDataSet.getPoints() - 250000L);
            oldDataSet.setDelayBf(true);
            LOGGER.info("User {} bought the Cheetah Paws (Delay)", oldDataSet.getUsername());
        }
        if (!oldDataSet.getSpeedBf() && dataSet.getSpeedBf() && oldDataSet.getPoints() > 300000L) {
            oldDataSet.setPoints(oldDataSet.getPoints() - 300000L);
            oldDataSet.setSpeedBf(true);
            LOGGER.info("User {} bought the Turtle Shell (Speed)", oldDataSet.getUsername());
        }
        if (!oldDataSet.getStarBf() && dataSet.getStarBf() && oldDataSet.getPoints() > 1000000L) {
            oldDataSet.setPoints(oldDataSet.getPoints() - 1000000L);
            oldDataSet.setStarBf(true);
            LOGGER.info("User {} bought the Leo Sign (Star). Bingo!", oldDataSet.getUsername());
        }
    }

    public long countUsers() {
        return (long) session.createCriteria(UserDataSet.class).setProjection(Projections.rowCount()).uniqueResult();
    }

    @NotNull
    public List<LoginScoreSet> readAll() {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        criteria.addOrder(Order.desc("score"));
        criteria.addOrder(Order.asc("username"));
        final List dsList = criteria.list();
        final ArrayList<LoginScoreSet> result = new ArrayList<>(dsList.size());
        for (Object uDS : dsList) {
            result.add(new LoginScoreSet((UserDataSet) uDS));
        }
        return result;
    }

    @Nullable
    public String loginUser(@NotNull Long userID, @NotNull String sessionID) {
        final UserDataSet userDataSet = readUserByID(userID);
        if (userDataSet != null) {
            userDataSet.setAuthToken(sessionID);
            return sessionID;
        } else {
            return null;
        }
    }

    public void logoutUser(@NotNull Long userID) {
        final UserDataSet userDataSet = readUserByID(userID);
        if (userDataSet != null) {
            userDataSet.setAuthToken(null);
        }
    }
}
