package ru.hse.cs.java2020.task03.db;

import java.sql.SQLException;

import org.json.JSONObject;
import ru.hse.cs.java2020.task03.db.models.User;
import ru.hse.cs.java2020.task03.db.services.UserService;

public class Main {
    public static void main(String[] args) throws SQLException {
        UserService userService = new UserService();
        JSONObject kek = new JSONObject();
//        kek.put("azaza", "trololo");
        User user = new User("hui", "2", "hui", "hui", "hui", "hui", kek.toString());
        userService.saveUser(user);
//        userService.saveUser(user);
//        User find_user = userService.findUserByChatId("2");
//        find_user = userService.findUserByChatId("2");
//        System.out.println(find_user.toString());
    }
}
