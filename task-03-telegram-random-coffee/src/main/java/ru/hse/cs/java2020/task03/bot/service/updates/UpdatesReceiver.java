package ru.hse.cs.java2020.task03.bot.service.updates;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hse.cs.java2020.task03.bot.service.updates.handlers.CallbackQueryHandler;
import ru.hse.cs.java2020.task03.bot.service.updates.handlers.TextMessageHandler;

public class UpdatesReceiver {

    public static void handleUpdates(Update update) {
        ReplyMessageGenerator replyGenerator = new ReplyMessageGenerator();
        if (update.hasMessage() && update.getMessage().hasText()) {
            new TextMessageHandler(update, replyGenerator).handleTextMessage();
        } else if (update.hasCallbackQuery()) {
            new CallbackQueryHandler(update, replyGenerator).handleCallBackQuery();
        } else {
            replyGenerator.sendTextMessage(update.getMessage().getChatId(), "Я могу принимать только текстовые сообщения!");
            throw new IllegalArgumentException();
        }
    }
}

