package ru.hse.cs.java2020.task03.db.services;

import java.util.List;

import ru.hse.cs.java2020.task03.db.dao.UserDAO;
import ru.hse.cs.java2020.task03.db.dao.UserDAOImpl;
import ru.hse.cs.java2020.task03.db.models.User;

public class UserService {

    private UserDAO usersDao = new UserDAOImpl();

    public UserService() {
    }

    public User findUser(int id) {
        return usersDao.findById(id);
    }

    public User findUserByChatId(String chatId) {
        try {
            return usersDao.findByChatId(chatId);
        } catch (Exception e) {
            return null;
        }
    }

    public void saveUser(User user) {
        usersDao.save(user);
    }

    public void deleteUser(User user) {
        usersDao.delete(user);
    }

    public void updateUser(User user) {
        usersDao.update(user);
    }

    public List<User> findAllUsers() {
        return usersDao.findAll();
    }

}
