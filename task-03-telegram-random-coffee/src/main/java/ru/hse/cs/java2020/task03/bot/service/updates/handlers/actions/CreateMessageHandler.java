package ru.hse.cs.java2020.task03.bot.service.updates.handlers.actions;

import java.rmi.UnexpectedException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.httpclient.ProtocolException;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.hse.cs.java2020.task03.bot.service.keyboards.builders.InlineKeyboardMarkupBuilder;
import ru.hse.cs.java2020.task03.bot.service.updates.ReplyMessageGenerator;
import ru.hse.cs.java2020.task03.bot.service.updates.handlers.states.ChooseState;
import ru.hse.cs.java2020.task03.bot.service.updates.handlers.states.CreateState;
import ru.hse.cs.java2020.task03.db.models.User;
import ru.hse.cs.java2020.task03.db.services.UserService;
import ru.hse.cs.java2020.task03.startrek.Issue;
import ru.hse.cs.java2020.task03.startrek.Queue;
import ru.hse.cs.java2020.task03.startrek.StartrekClient;
import ru.hse.cs.java2020.task03.util.FullState;

public class CreateMessageHandler implements MessageHandler {
    private Long chatId;
    private User user;
    private UserService userService;
    private String message;
    private ReplyMessageGenerator replyMessageGenerator;
    private StartrekClient startrek;

    public CreateMessageHandler(Long chatId, User user, UserService userService, String message,
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
            if (user.getStateValue().equals(CreateState.BEGIN.getStateValue())) {
                newState = sendButtons();
            } else if (user.getStateValue().equals(CreateState.ME_ASSIGNER.getStateValue())) {
                JSONObject meta = new JSONObject(user.getMeta());
                meta.put("me_assignee", true);
                user.setMeta(meta.toString());
                userService.updateUser(user);
                newState = sendButtons();
            } else if (user.getStateValue().equals(CreateState.NO_ASSIGNER.getStateValue())) {
                JSONObject meta = new JSONObject(user.getMeta());
                meta.put("me_assignee", false);
                user.setMeta(meta.toString());
                userService.updateUser(user);
                newState = sendButtons();
            } else if (user.getStateValue().equals(CreateState.INTRO_SUMMARY.getStateValue())) {
                newState = introSummary();
            } else if (user.getStateValue().equals(CreateState.GET_SUMMARY.getStateValue())) {
                newState = getSummary();
            } else if (user.getStateValue().equals(CreateState.INTRO_DESCRIPTION.getStateValue())) {
                newState = introDescription();
            } else if (user.getStateValue().equals(CreateState.GET_DESCRIPTION.getStateValue())) {
                newState = getDescription();
            } else if (user.getStateValue().equals(CreateState.UPDATE_CHANGES.getStateValue())) {
                newState = updateChanges();
            } else if (user.getStateValue().equals(CreateState.INTRO_QUEUE.getStateValue())) {
                newState = introQueue();
            } else if (user.getStateValue().equals(CreateState.GET_QUEUE.getStateValue())) {
                newState = getQueue();
            } else {
                throw new Exception("");
            }
        } catch (Exception e) {
            replyMessageGenerator.sendTextMessage(chatId, "Что-то пошло не так");
            newState = new FullState(CreateState.class.getSimpleName(), CreateState.BEGIN.getStateValue());
        }

        FullState.fillUserState(user, newState);
        userService.updateUser(user);
    }

    private SendMessage getKeyboardToChoose(Boolean meAssignee) {
        InlineKeyboardMarkupBuilder builder = InlineKeyboardMarkupBuilder.create(chatId)
                .setText("Выберите нужное действие:")
                .row()
                .button("Выберите очередь", "/create_queue")
                .endRow()
                .row()
                .button("Изменить название задачи", "/create_summary")
                .endRow()
                .row()
                .button("Изменить описание задачи", "/create_description")
                .endRow();
        builder.row();
        if (!meAssignee) {
            builder.button("Назначить меня исполнителем", "/create_me_assignee");
        } else {
            builder.button("Я исполнитель", "/create_no_assignee");
        }
        builder.endRow();
        builder.row();
        builder.button("Создать", "/create_done");
        builder.endRow();
        return builder.build();
    }


    private SendMessage getQueueToChoose(List<Queue> queueList) {
        InlineKeyboardMarkupBuilder builder = InlineKeyboardMarkupBuilder.create(chatId)
                .setText("Выберите очередь:");
        for (Queue queue : queueList) {
            builder.row();
            builder.button(queue.getKey(), "/create_choose_queue " + queue.getId());
            builder.endRow();
        }
        return builder.build();
    }

    private List<Queue> getQueueList() throws ProtocolException, UnexpectedException, JsonProcessingException {
        return startrek.searchQueue(user.getToken(), user.getOrgId());
    }

    private FullState introQueue() throws ProtocolException, UnexpectedException, JsonProcessingException {
        List<Queue> queueList = getQueueList();
        replyMessageGenerator.sendInlineKeyboardMessage(getQueueToChoose(queueList));
        return new FullState(CreateState.class.getSimpleName(), CreateState.GET_QUEUE.getStateValue());
    }

    private FullState getQueue() {
        return sendButtons();
    }

    private FullState introSummary() {
        replyMessageGenerator.sendTextMessage(chatId, "Введите название задачи");
        return new FullState(CreateState.class.getSimpleName(), CreateState.GET_SUMMARY.getStateValue());
    }

    private FullState getSummary() {
        JSONObject meta = new JSONObject(user.getMeta());
        meta.put("summary", message);
        user.setMeta(meta.toString());
        userService.updateUser(user);
        System.out.println("Отправка");
        return sendButtons();
    }

    private FullState introDescription() {
        replyMessageGenerator.sendTextMessage(chatId, "Введите описание задачи");
        return new FullState(CreateState.class.getSimpleName(), CreateState.GET_DESCRIPTION.getStateValue());
    }

    private FullState getDescription() {
        JSONObject meta = new JSONObject(user.getMeta());
        meta.put("description", message);
        user.setMeta(meta.toString());
        userService.updateUser(user);
        return sendButtons();
    }

    private FullState updateChanges()
            throws ProtocolException, UnexpectedException, JsonProcessingException {
        JSONObject meta = new JSONObject(user.getMeta());
        try {
            meta.getString("queue_id");
        } catch (JSONException e) {
            replyMessageGenerator.sendTextMessage(chatId, "Перед тем как создать выберите очередь!");
            return sendButtons();
        }
        try {
            meta.getString("summary");
        } catch (JSONException e) {
            replyMessageGenerator.sendTextMessage(chatId, "У задачи должно быть название!");
            return sendButtons();
        }

        Issue issue = startrek.createTask(
                user.getToken(),
                user.getOrgId(),
                user.getUid(),
                meta.getString("queue_id"),
                meta.getString("summary"),
                meta.optString("description", ""),
                meta.optBoolean("me_assignee", false)
        );
        replyMessageGenerator.sendTextMessage(chatId,
                "Созданная задача https://tracker.yandex.ru/" + issue.getKey());
        return new FullState(ChooseState.class.getSimpleName(), ChooseState.BEGIN.getStateValue());
    }

    private FullState sendButtons() {
        JSONObject meta = new JSONObject(user.getMeta());
        SendMessage message = getKeyboardToChoose(meta.optBoolean("me_assignee", false));
        replyMessageGenerator.sendInlineKeyboardMessage(message);
        return new FullState(CreateState.class.getSimpleName(), CreateState.BEGIN.getStateValue());
    }
}
