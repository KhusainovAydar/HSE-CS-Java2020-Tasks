package ru.hse.cs.java2020.task03.bot.service;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hse.cs.java2020.task03.bot.service.updates.UpdatesReceiver;


public class Bot extends TelegramLongPollingBot {

    private static final String BOT_USERNAME = "Google Play Parser Bot";

    private static final String BOT_TOKEN = "1134490046:AAG6RgG4xrRDptQg5A11RSktCTRL-bBISPM";

    @Override
    public void onUpdateReceived(Update update) {
        UpdatesReceiver.handleUpdates(update);
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}
