package ru.hse.cs.java2020.task03.bot.service.updates;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.hse.cs.java2020.task03.bot.service.Bot;
import ru.hse.cs.java2020.task03.bot.service.keyboards.builders.ReplyKeyboardMarkupBuilder;

public class ReplyMessageGenerator extends Bot {

    private Logger logger = Logger.getLogger(ReplyMessageGenerator.class.getName());

    private synchronized void sendTextMessage(Long chatId, String message, Boolean enableMarkdown) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(enableMarkdown)
                .setChatId(chatId)
                .setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendTextMessage(Long chatId, String message) {
        sendTextMessage(chatId, message, false);
    }

    public synchronized void sendMarkdownTextMessage(Long chatId, String message) {
        sendTextMessage(chatId, message, true);
    }

    public synchronized void deleteMessage(Long chatId, Integer messageId) {
        DeleteMessage message = new DeleteMessage()
                .setMessageId(messageId)
                .setChatId(chatId);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public synchronized void sendInlineKeyboardMessage(SendMessage keyboard) {
        try {
            execute(keyboard);
        } catch (TelegramApiException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public synchronized void sendReplyKeyboardMessage(long chatId) {
        SendMessage keyboard = ReplyKeyboardMarkupBuilder.create(chatId)
                .row()
                .button("Назад")
                .button("Выйти")
                .endRow()
                .build();
        try {
            execute(keyboard);
        } catch (TelegramApiException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }
}

