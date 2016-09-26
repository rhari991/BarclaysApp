package edu.vesit.barclaysapp;

import java.util.ArrayList;
import java.util.List;

public class DummyData {

    public static List<User> USERS = new ArrayList<>();
    public static List<String> EMAILS = new ArrayList<>();

    static {
        USERS.add(new User("1", "amy@example.com", "password", "Amy", "Tate", "FA01.png", null));
        USERS.add(new User("2", "bob@example.com", "password", "Bob", "Turner", "A01.png", null));
        USERS.add(new User("3", "ted@example.com", "password", "Ted", "Packman", "A02.png", null));
        USERS.add(new User("4", "cavin@example.com", "password", "Cavin", "Page", "A03.png", null));
        USERS.add(new User("5", "emily@example.com", "password", "Emily", "Pedsy", "FB01.png", null));
        USERS.add(new User("6", "cindy@example.com", "password", "Cindy", "Pettis", "FB02.png", null));
        for (User user : USERS)
            EMAILS.add(user.getEmail());
    }

    public static User findByEmail(String email) {
        for (User user : USERS) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    public static List<User> getUserListForSelect(User user) {
        List<User> userList = new ArrayList<>();
        for (User u : USERS) {
            if (u.getEmail().equals(user.getEmail())) {
                continue;
            }
            userList.add(u);
        }
        return userList;
    }

}
