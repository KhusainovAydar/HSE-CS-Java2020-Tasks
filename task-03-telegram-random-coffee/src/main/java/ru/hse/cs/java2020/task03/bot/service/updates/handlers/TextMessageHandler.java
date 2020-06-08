package ru.hse.cs.java2020.task03.bot.service.updates.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hse.cs.java2020.task03.bot.service.updates.ReplyMessageGenerator;

import ru.hse.cs.java2020.task03.bot.service.updates.handlers.actions.ChooseMessageHandler;
import ru.hse.cs.java2020.task03.bot.service.updates.handlers.actions.CreateMessageHandler;
import ru.hse.cs.java2020.task03.bot.service.updates.handlers.actions.OAuthMessageHandler;
import ru.hse.cs.java2020.task03.bot.service.updates.handlers.actions.WatchMessageHandler;

import ru.hse.cs.java2020.task03.bot.service.updates.handlers.states.ChooseState;
import ru.hse.cs.java2020.task03.bot.service.updates.handlers.states.CreateState;
import ru.hse.cs.java2020.task03.bot.service.updates.handlers.states.OAuthState;
import ru.hse.cs.java2020.task03.bot.service.updates.handlers.states.WatchState;

import ru.hse.cs.java2020.task03.db.models.User;
import ru.hse.cs.java2020.task03.db.services.UserService;

import ru.hse.cs.java2020.task03.startrek.StartrekClient;
import ru.hse.cs.java2020.task03.util.FullState;

public class TextMessageHandler {

    private Update update;
    private ReplyMessageGenerator replyGenerator;

    public TextMessageHandler(Update update, ReplyMessageGenerator replyGenerator) {
        this.update = update;
        this.replyGenerator = replyGenerator;
    }

    public void handleTextMessage() {
        Long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText();

        UserService userService = new UserService();
        User user = userService.findUserByChatId(chatId.toString());

        StartrekClient startrek = new StartrekClient();

        // Создаем нового пользователя в БД
        if (user == null) {
            User newUser = new User(null, chatId.toString(), null, null, OAuthState.class.getSimpleName(),
                    OAuthState.GET_TOKEN.getStateValue(), null);
            userService.saveUser(newUser);
            replyGenerator.sendMarkdownTextMessage(chatId, "Введите OAuth Token");
            replyGenerator.sendReplyKeyboardMessage(chatId);
            return;
        }

        if (message.equals("Назад")) {
            FullState newState = new FullState(ChooseState.class.getSimpleName(), ChooseState.BEGIN.getStateValue());
            FullState.fillUserState(user, newState);
            userService.updateUser(user);
            new ChooseMessageHandler(chatId, user, userService, message, replyGenerator, startrek).actionHandler();
            return;
        }

        if (message.equals("Выйти")) {
            userService.deleteUser(user);
            replyGenerator.sendTextMessage(chatId, "Введите команду /start");
            return;
        }

        if (user.getStateClass().equals(OAuthState.class.getSimpleName())) {
            new OAuthMessageHandler(chatId, user, userService, message, replyGenerator, startrek).actionHandler();
        } else if (user.getStateClass().equals(ChooseState.class.getSimpleName())) {
            replyGenerator.sendTextMessage(chatId, "Ayayaya");
            // todo обработка непонятных сообщений
        } else if (user.getStateClass().equals(WatchState.class.getSimpleName())) {
            new WatchMessageHandler(chatId, user, userService, message, replyGenerator, startrek).actionHandler();
        } else if (user.getStateClass().equals(CreateState.class.getSimpleName())) {
            new CreateMessageHandler(chatId, user, userService, message, replyGenerator, startrek).actionHandler();
        }
    }
}
