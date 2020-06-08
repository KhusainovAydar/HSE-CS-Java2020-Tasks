package ru.hse.cs.java2020.task03.bot.service.updates.handlers.actions;

import java.io.IOException;
import java.rmi.UnexpectedException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.httpclient.ProtocolException;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.hse.cs.java2020.task03.bot.service.keyboards.builders.InlineKeyboardMarkupBuilder;
import ru.hse.cs.java2020.task03.bot.service.updates.ReplyMessageGenerator;
import ru.hse.cs.java2020.task03.bot.service.updates.handlers.states.CreateState;
import ru.hse.cs.java2020.task03.bot.service.updates.handlers.states.WatchState;
import ru.hse.cs.java2020.task03.db.models.User;
import ru.hse.cs.java2020.task03.db.services.UserService;
import ru.hse.cs.java2020.task03.startrek.Comment;
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
            } else if (user.getStateValue().equals(WatchState.INTRO_COMMENT.getStateValue())) {
                newState = introComment();
            } else if (user.getStateValue().equals(WatchState.GET_COMMENT.getStateValue())) {
                newState = getComment();
            } else if (user.getStateValue().equals(WatchState.SHOW_COMMENTS.getStateValue())) {
                newState = showComments();
            } else {
                throw new Exception("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            replyMessageGenerator.sendTextMessage(chatId, "Что-то пошло не так");
            newState = new FullState(WatchState.class.getSimpleName(), WatchState.BEGIN.getStateValue());
        }

        FullState.fillUserState(user, newState);
        userService.updateUser(user);
    }

    private String showCommentMessage(Comment comment) {
        return "*Автор*: " + comment.getAuthor() + '\n'
                + "*Комментарий*:\n" + comment.getText() + "\n\n";
    }

    private String showCommentsMessage(List<Comment> comments) {
        if (comments.isEmpty()) {
            return "Нет комментариев";
        }
        StringBuilder result = new StringBuilder();
        for (Comment comment : comments) {
            result.append(showCommentMessage(comment));
        }
        return result.toString();
    }

    private FullState showComments() throws ProtocolException, UnexpectedException, JsonProcessingException {
        List<Comment> comments = startrek.getComments(
                user.getToken(), user.getOrgId(), new JSONObject(user.getMeta()).getString("issue_key"));
        replyMessageGenerator.sendMarkdownTextMessage(chatId, showCommentsMessage(comments));
        return new FullState(WatchState.class.getSimpleName(), WatchState.WAIT_BACK.getStateValue());
    }

    private FullState introComment() {
        replyMessageGenerator.sendTextMessage(chatId, "Введите ваш комментарий");
        return new FullState(WatchState.class.getSimpleName(), WatchState.GET_COMMENT.getStateValue());
    }

    private FullState getComment() throws ProtocolException, UnexpectedException, JsonProcessingException {
        JSONObject meta = new JSONObject(user.getMeta());
        startrek.postComment(user.getToken(), user.getOrgId(), meta.getString("issue_key"), message);
        replyMessageGenerator.sendTextMessage(chatId, "Комментарий успешно добавлен");
        return new FullState(WatchState.class.getSimpleName(), WatchState.WAIT_BACK.getStateValue());
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

    private SendMessage getKeyboardToChoose(Issue issue) {
        return InlineKeyboardMarkupBuilder.create(chatId)
                .setText(getIssueMessage(issue))
                .row()
                .button("Получить комментарии", "/watch_get_comments " + issue.getKey())
                .button("Добавить комментарий", "/watch_add_comment " + issue.getKey())
                .endRow()
                .build();
    }

    private FullState getIssueAction() throws IOException {
        if (message == null) {
            message = new JSONObject(user.getMeta()).getString("issue_key");
        }
        Issue issue = startrek.watchTask(user.getToken(), user.getOrgId(), message);
        replyMessageGenerator.sendInlineKeyboardMessage(getKeyboardToChoose(issue));
//        replyMessageGenerator.sendMarkdownTextMessage(chatId, getIssueMessage(issue));
        return new FullState(WatchState.class.getSimpleName(), WatchState.WAIT_BACK.getStateValue());
    }
}
