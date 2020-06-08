package ru.hse.cs.java2020.task03.bot.service.updates.handlers.actions;

import java.rmi.UnexpectedException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.httpclient.ProtocolException;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.hse.cs.java2020.task03.bot.service.keyboards.builders.InlineKeyboardMarkupBuilder;
import ru.hse.cs.java2020.task03.bot.service.updates.ReplyMessageGenerator;
import ru.hse.cs.java2020.task03.bot.service.updates.handlers.states.SearchState;
import ru.hse.cs.java2020.task03.db.models.User;
import ru.hse.cs.java2020.task03.db.services.UserService;
import ru.hse.cs.java2020.task03.startrek.Issue;
import ru.hse.cs.java2020.task03.startrek.StartrekClient;
import ru.hse.cs.java2020.task03.util.FullState;

public class SearchMessageHandler implements MessageHandler {
    private Long chatId;
    private User user;
    private UserService userService;
    private String message;
    private ReplyMessageGenerator replyMessageGenerator;
    private StartrekClient startrek;

    private static final Integer COUNT_ISSUES = 5;

    public SearchMessageHandler(Long chatId, User user, UserService userService, String message,
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
            if (user.getStateValue().equals(SearchState.BEGIN.getStateValue())) {
                newState = getIssues();
            } else if (user.getStateValue().equals(SearchState.NEXT.getStateValue())) {
                JSONObject meta = new JSONObject(user.getMeta());
                int pageNum = meta.optInt("page_num", 1);
                pageNum++;
                pageNum = Math.min(pageNum, getPagesCount());
                meta.put("page_num", pageNum);
                user.setMeta(meta.toString());
                newState = getIssues();
            } else if (user.getStateValue().equals(SearchState.PREVIOUS.getStateValue())) {
                JSONObject meta = new JSONObject(user.getMeta());
                int pageNum = meta.optInt("page_num", 1);
                pageNum--;
                pageNum = Math.max(1, pageNum);
                meta.put("page_num", pageNum);
                user.setMeta(meta.toString());
                newState = getIssues();
            } else {
                throw new Exception("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            replyMessageGenerator.sendTextMessage(chatId, "Что-то пошло не так");
            newState = new FullState(SearchState.class.getSimpleName(), SearchState.BEGIN.getStateValue());
        }

        FullState.fillUserState(user, newState);
        userService.updateUser(user);
    }

    private SendMessage getKeyboardToChoose(List<Issue> issueList, Integer pageNum, Integer pagesCount) {
        InlineKeyboardMarkupBuilder builder = InlineKeyboardMarkupBuilder.create(chatId)
                .setText("Выберите задачу:");
        for (Issue issue : issueList) {
            builder.row();
            builder.button(issue.getKey(), "/search_issue " + issue.getKey());
            builder.endRow();
        }
        builder.row();
        builder.button("<<", "/search_previous");
        builder.button(String.format("%d/%d", pageNum, pagesCount), "/no_callback");
        builder.button(">>", "/search_next");
        builder.endRow();
        return builder.build();
    }

    private Integer getPagesCount() throws ProtocolException, UnexpectedException, JsonProcessingException {
        Integer count = startrek.countIssues(user.getToken(), user.getOrgId());
        if (count % COUNT_ISSUES != 0) {
            return count / COUNT_ISSUES + 1;
        } else {
            return count / COUNT_ISSUES;
        }
    }

    private List<Issue> getIssueList(Integer pageNum)
            throws ProtocolException, UnexpectedException, JsonProcessingException {
        return startrek.searchTask(user.getToken(), user.getOrgId(), COUNT_ISSUES, pageNum);
    }

    private FullState getIssues() throws ProtocolException, UnexpectedException, JsonProcessingException {
        JSONObject meta = new JSONObject(user.getMeta());
        Integer pageNum = meta.optInt("page_num", 1);
        Integer pagesCount = getPagesCount();
        List<Issue> issueList = getIssueList(pageNum);
        replyMessageGenerator.sendInlineKeyboardMessage(getKeyboardToChoose(issueList, pageNum, pagesCount));
        return new FullState(SearchState.class.getSimpleName(), SearchState.BEGIN.getStateValue());
    }
}
