package ru.hse.cs.java2020.task03.db.dao;

import java.util.List;

import ru.hse.cs.java2020.task03.db.models.User;

public interface UserDAO {
    User findById(int id);

    User findByChatId(String chatId);

    void save(User user);

    void update(User user);

    void delete(User user);

    List<User> findAll();
}
