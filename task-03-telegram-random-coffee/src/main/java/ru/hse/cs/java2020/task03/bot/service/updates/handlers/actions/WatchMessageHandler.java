package ru.hse.cs.java2020.task03.bot.service.updates.handlers.actions;

import java.io.IOException;

import org.json.JSONObject;
import ru.hse.cs.java2020.task03.bot.service.updates.ReplyMessageGenerator;
import ru.hse.cs.java2020.task03.bot.service.updates.handlers.states.WatchState;
import ru.hse.cs.java2020.task03.db.models.User;
import ru.hse.cs.java2020.task03.db.services.UserService;
import ru.hse.cs.java2020.task03.startrek.Issue;
import ru.hse.cs.java2020.task03.startrek.StartrekClient;
import ru.hse.cs.java2020.task03.util.FullState;

public class WatchMessageHandler implements MessageHandler {
    private Long chatId;
    private User user;
    private UserService userService;
    private String message;
    private ReplyMessageGenerator replyMessageGenerator;
    private StartrekClient startrek;

    public WatchMessageHandler(Long chatId, User user, UserService userService, String message,
                               ReplyMessageGenerator replyMessageGenerator, StartrekClient startrek) {
        this.chatId = chatId;
        this.user = user;
        this.userService = userService;
        this.message = message;
        this.replyMessageGenerator = replyMessageGenerator;
        this.startrek = startrek;
    }

    @Override
    public void actionHandler() {
        FullState newState;
        try {
            if (user.getStateValue().equals(WatchState.BEGIN.getStateValue())) {
                newState = getGetKeyAction();
            } else if (user.getStateValue().equals(WatchState.GET_KEY.getStateValue())
            || user.getStateValue().equals(WatchState.SHOW_FROM_CB.getStateValue())) {
                newState = getIssueAction();
            } else {
                throw new Exception("");
            }
        } catch (Exception e) {
            replyMessageGenerator.sendTextMessage(chatId, "Что-то пошло не так");
            newState = new FullState(WatchState.class.getSimpleName(), WatchState.BEGIN.getStateValue());
        }

        FullState.fillUserState(user, newState);
        userService.updateUser(user);
    }

    private FullState getGetKeyAction() {
        replyMessageGenerator.sendTextMessage(chatId, "Введите ключ задачи");
        return new FullState(WatchState.class.getSimpleName(), WatchState.GET_KEY.getStateValue());
    }

    private String getIssueMessage(Issue issue) {
        return "*Название:*\n" + issue.getSummary() + "\n\n"
                + "*Описание:*\n" + issue.getDescription() + "\n\n"
                + "*Автор:*\n" + issue.getAuthor() + "\n\n"
                + "*Исполнитель:*\n" + issue.getAssigner() + "\n\n"
                + "*Наблюдатели:*\n" + String.join(", ", issue.getFollowers());
    }

    private FullState getIssueAction() throws IOException {
        if (message == null) {
            message = new JSONObject(user.getMeta()).getString("issue_key");
        }
        Issue issue = startrek.watchTask(user.getToken(), user.getOrgId(), message);
        replyMessageGenerator.sendMarkdownTextMessage(chatId, getIssueMessage(issue));
        return new FullState(WatchState.class.getSimpleName(), WatchState.WAIT_BACK.getStateValue());
    }
}
