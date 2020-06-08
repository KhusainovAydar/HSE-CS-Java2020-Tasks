package ru.hse.cs.java2020.task03.bot;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.hse.cs.java2020.task03.bot.service.Bot;

class BotApplication {

    public static void main(String[] args) {
        final Logger logger = Logger.getLogger(BotApplication.class.getName());
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
            logger.info("[Бот успешно запущен]");
        } catch (TelegramApiRequestException e) {
            logger.log(Level.WARNING, "[Не удалось запустить бота]: " + e.toString());
        }
    }
}
