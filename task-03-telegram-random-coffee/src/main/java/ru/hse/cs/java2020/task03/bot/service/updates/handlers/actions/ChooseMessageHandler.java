package ru.hse.cs.java2020.task03.bot.service.updates.handlers.actions;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.hse.cs.java2020.task03.bot.service.keyboards.builders.InlineKeyboardMarkupBuilder;
import ru.hse.cs.java2020.task03.bot.service.updates.ReplyMessageGenerator;
import ru.hse.cs.java2020.task03.bot.service.updates.handlers.states.ChooseState;
import ru.hse.cs.java2020.task03.db.models.User;
import ru.hse.cs.java2020.task03.db.services.UserService;
import ru.hse.cs.java2020.task03.startrek.StartrekClient;
import ru.hse.cs.java2020.task03.util.FullState;

public class ChooseMessageHandler implements MessageHandler {
    private Long chatId;
    private User user;
    private UserService userService;
    private String message;
    private ReplyMessageGenerator replyMessageGenerator;
    private StartrekClient startrek;

    public ChooseMessageHandler(Long chatId, User user, UserService userService, String message,
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
            if (user.getStateValue().equals(ChooseState.BEGIN.getStateValue())) {
                newState = getChooseAction();
            } else {
                throw new Exception("");
            }
        } catch (Exception e) {
            replyMessageGenerator.sendTextMessage(chatId, "Что-то пошло не так");
            newState = new FullState(ChooseState.class.getSimpleName(), ChooseState.BEGIN.getStateValue());
        }

        FullState.fillUserState(user, newState);
        userService.updateUser(user);
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

    private FullState getChooseAction() {
        user.setMeta("{}");
        replyMessageGenerator.sendInlineKeyboardMessage(getKeyboardToChoose());
        return new FullState(ChooseState.class.getSimpleName(), ChooseState.BEGIN.getStateValue());
    }
}
