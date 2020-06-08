package ru.hse.cs.java2020.task03.bot.service.updates.handlers.actions;

import java.rmi.UnexpectedException;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.httpclient.ProtocolException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.hse.cs.java2020.task03.bot.service.keyboards.builders.InlineKeyboardMarkupBuilder;
import ru.hse.cs.java2020.task03.bot.service.updates.ReplyMessageGenerator;
import ru.hse.cs.java2020.task03.bot.service.updates.handlers.states.ChooseState;
import ru.hse.cs.java2020.task03.bot.service.updates.handlers.states.OAuthState;
import ru.hse.cs.java2020.task03.db.models.User;
import ru.hse.cs.java2020.task03.db.services.UserService;
import ru.hse.cs.java2020.task03.startrek.Person;
import ru.hse.cs.java2020.task03.startrek.StartrekClient;
import ru.hse.cs.java2020.task03.util.FullState;

public class OAuthMessageHandler implements MessageHandler {
    private Long chatId;
    private User user;
    private UserService userService;
    private String message;
    private ReplyMessageGenerator replyMessageGenerator;
    private StartrekClient startrek;

    public OAuthMessageHandler(Long chatId, User user, UserService userService, String message,
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
            if (user.getStateValue().equals(OAuthState.GET_TOKEN.getStateValue())) {
                newState = getTokenAction();
            } else if (user.getStateValue().equals(OAuthState.GET_ORG_ID.getStateValue())) {
                newState = getOrgIdAction();
            } else {
                throw new Exception("");
            }
        } catch (Exception e) {
            replyMessageGenerator.sendTextMessage(chatId, "Что-то пошло не так");
            newState = new FullState(OAuthState.class.getSimpleName(), OAuthState.GET_TOKEN.getStateValue());
        }

        FullState.fillUserState(user, newState);
        userService.updateUser(user);
    }

    private FullState getTokenAction() {
        user.setToken(message);
        replyMessageGenerator.sendTextMessage(chatId, "Введите ID организации");
        return new FullState(OAuthState.class.getSimpleName(), OAuthState.GET_ORG_ID.getStateValue());
    }

    private SendMessage getKeyboardToChoose() {
        return InlineKeyboardMarkupBuilder.create(chatId)
                .setText("Выберите нужное действие:")
                .row()
                .button("Создать задачу", "/create")
                .endRow()
                .row()
                .button("Посмотреть список задач", "/search")
                .endRow()
                .row()
                .button("Посмотреть задачу", "/watch")
                .endRow()
                .build();
    }

    private FullState getOrgIdAction()
            throws ProtocolException, UnexpectedException, JsonProcessingException {
        user.setOrgId(message);
        Person personInfo = startrek.personInfo(user.getToken(), user.getOrgId());
        user.setUid(personInfo.getUid());
        replyMessageGenerator.sendInlineKeyboardMessage(getKeyboardToChoose());
        return new FullState(ChooseState.class.getSimpleName(), ChooseState.BEGIN.getStateValue());
    }
}
