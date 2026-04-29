package com.example.testsaytproyekt.telegrambot.config;

import com.example.testsaytproyekt.telegrambot.bot.TelegramBots;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@RequiredArgsConstructor
public class TelegramBotConfig {

    private final TelegramBots telegramBots;

    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramBots);

            System.out.println("BOT STARTED SUCCESSFULLY");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}