package ru.hse.cs.java2020.task03.bot.service.updates.handlers;

import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hse.cs.java2020.task03.bot.service.updates.ReplyMessageGenerator;
import ru.hse.cs.java2020.task03.bot.service.updates.handlers.actions.CreateMessageHandler;
import ru.hse.cs.java2020.task03.bot.service.updates.handlers.actions.SearchMessageHandler;
import ru.hse.cs.java2020.task03.bot.service.updates.handlers.actions.WatchMessageHandler;
import ru.hse.cs.java2020.task03.bot.service.updates.handlers.states.CreateState;
import ru.hse.cs.java2020.task03.bot.service.updates.handlers.states.SearchState;
import ru.hse.cs.java2020.task03.bot.service.updates.handlers.states.WatchState;
import ru.hse.cs.java2020.task03.db.models.User;
import ru.hse.cs.java2020.task03.db.services.UserService;
import ru.hse.cs.java2020.task03.startrek.StartrekClient;
import ru.hse.cs.java2020.task03.util.FullState;

public class CallbackQueryHandler {

    private Update update;
    private ReplyMessageGenerator replyGenerator;

    public CallbackQueryHandler(Update update, ReplyMessageGenerator replyGenerator) {
        this.update = update;
        this.replyGenerator = replyGenerator;
    }

    public void handleCallBackQuery() {
        String callData = update.getCallbackQuery().getData();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        UserService userService = new UserService();
        User user = userService.findUserByChatId(chatId.toString());
        String[] command = callData.split("\\s+");
        FullState newState;
        JSONObject meta;
        switch (command[0]) {
            case "/watch":
                newState = new FullState(WatchState.class.getSimpleName(), WatchState.BEGIN.getStateValue());
                FullState.fillUserState(user, newState);
                userService.updateUser(user);
                new WatchMessageHandler(chatId, user, userService, null, replyGenerator, new StartrekClient())
                        .actionHandler();
                break;
            case "/watch_get_comments":
                newState = new FullState(WatchState.class.getSimpleName(), WatchState.SHOW_COMMENTS.getStateValue());
                FullState.fillUserState(user, newState);
                meta = new JSONObject(user.getMeta());
                meta.put("issue_key", command[1]);
                user.setMeta(meta.toString());
                userService.updateUser(user);
                new WatchMessageHandler(chatId, user, userService, null, replyGenerator, new StartrekClient())
                        .actionHandler();
                break;
            case "/watch_add_comment":
                newState = new FullState(WatchState.class.getSimpleName(), WatchState.INTRO_COMMENT.getStateValue());
                FullState.fillUserState(user, newState);
                meta = new JSONObject(user.getMeta());
                meta.put("issue_key", command[1]);
                user.setMeta(meta.toString());
                userService.updateUser(user);
                new WatchMessageHandler(chatId, user, userService, null, replyGenerator, new StartrekClient())
                        .actionHandler();
                break;
            case "/search":
                replyGenerator.deleteMessage(chatId, messageId);
                newState = new FullState(SearchState.class.getSimpleName(), SearchState.BEGIN.getStateValue());
                FullState.fillUserState(user, newState);
                userService.updateUser(user);
                new SearchMessageHandler(chatId, user, userService, null, replyGenerator, new StartrekClient())
                        .actionHandler();
                break;
            case "/search_next":
                replyGenerator.deleteMessage(chatId, messageId);
                newState = new FullState(SearchState.class.getSimpleName(), SearchState.NEXT.getStateValue());
                FullState.fillUserState(user, newState);
                userService.updateUser(user);
                new SearchMessageHandler(chatId, user, userService, null, replyGenerator, new StartrekClient())
                        .actionHandler();
                break;
            case "/search_previous":
                replyGenerator.deleteMessage(chatId, messageId);
                newState = new FullState(SearchState.class.getSimpleName(), SearchState.PREVIOUS.getStateValue());
                FullState.fillUserState(user, newState);
                userService.updateUser(user);
                new SearchMessageHandler(chatId, user, userService, null, replyGenerator, new StartrekClient())
                        .actionHandler();
                break;
            case "/search_issue":
                newState = new FullState(WatchState.class.getSimpleName(), WatchState.SHOW_FROM_CB.getStateValue());
                FullState.fillUserState(user, newState);
                meta = new JSONObject(user.getMeta());
                meta.put("issue_key", command[1]);
                user.setMeta(meta.toString());
                userService.updateUser(user);
                new WatchMessageHandler(chatId, user, userService, null, replyGenerator, new StartrekClient())
                        .actionHandler();
                break;
            case "/create":
                replyGenerator.deleteMessage(chatId, messageId);
                newState = new FullState(CreateState.class.getSimpleName(), CreateState.BEGIN.getStateValue());
                FullState.fillUserState(user, newState);
                userService.updateUser(user);
                new CreateMessageHandler(chatId, user, userService, null, replyGenerator, new StartrekClient())
                        .actionHandler();
                break;
            case "/create_summary":
                replyGenerator.deleteMessage(chatId, messageId);
                newState = new FullState(CreateState.class.getSimpleName(), CreateState.INTRO_SUMMARY.getStateValue());
                FullState.fillUserState(user, newState);
                userService.updateUser(user);
                new CreateMessageHandler(chatId, user, userService, null, replyGenerator, new StartrekClient())
                        .actionHandler();
                break;
            case "/create_description":
                replyGenerator.deleteMessage(chatId, messageId);
                newState = new FullState(CreateState.class.getSimpleName(), CreateState.INTRO_DESCRIPTION.getStateValue());
                FullState.fillUserState(user, newState);
                userService.updateUser(user);
                new CreateMessageHandler(chatId, user, userService, null, replyGenerator, new StartrekClient())
                        .actionHandler();
                break;
            case "/create_me_assignee":
                replyGenerator.deleteMessage(chatId, messageId);
                newState = new FullState(CreateState.class.getSimpleName(), CreateState.ME_ASSIGNER.getStateValue());
                FullState.fillUserState(user, newState);
                userService.updateUser(user);
                new CreateMessageHandler(chatId, user, userService, null, replyGenerator, new StartrekClient())
                        .actionHandler();
                break;
            case "/create_no_assignee":
                replyGenerator.deleteMessage(chatId, messageId);
                newState = new FullState(CreateState.class.getSimpleName(), CreateState.NO_ASSIGNER.getStateValue());
                FullState.fillUserState(user, newState);
                userService.updateUser(user);
                new CreateMessageHandler(chatId, user, userService, null, replyGenerator, new StartrekClient())
                        .actionHandler();
                break;
            case "/create_done":
                replyGenerator.deleteMessage(chatId, messageId);
                newState = new FullState(CreateState.class.getSimpleName(), CreateState.UPDATE_CHANGES.getStateValue());
                FullState.fillUserState(user, newState);
                userService.updateUser(user);
                new CreateMessageHandler(chatId, user, userService, null, replyGenerator, new StartrekClient())
                        .actionHandler();
                break;
            case "/create_queue":
                replyGenerator.deleteMessage(chatId, messageId);
                newState = new FullState(CreateState.class.getSimpleName(), CreateState.INTRO_QUEUE.getStateValue());
                FullState.fillUserState(user, newState);
                userService.updateUser(user);
                new CreateMessageHandler(chatId, user, userService, null, replyGenerator, new StartrekClient())
                        .actionHandler();
                break;
            case "/create_choose_queue":
                replyGenerator.deleteMessage(chatId, messageId);
                meta = new JSONObject(user.getMeta());
                meta.put("queue_id", command[1]);
                user.setMeta(meta.toString());
                newState = new FullState(CreateState.class.getSimpleName(), CreateState.GET_QUEUE.getStateValue());
                FullState.fillUserState(user, newState);
                userService.updateUser(user);
                new CreateMessageHandler(chatId, user, userService, null, replyGenerator, new StartrekClient())
                        .actionHandler();
                break;
            default:
                break;
        }
    }
}
