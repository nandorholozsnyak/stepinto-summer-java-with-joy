package hu.stepintomeetups.pre;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.Nameable;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class StepIntoCounterExample {

    private static final String TOKEN = System.getenv("TOKEN");

    public static void main(String[] args) {
        DiscordApi discordApi = new DiscordApiBuilder()
                .setToken(TOKEN)
                .addListener(CounterChannelListener::new)
                .login()
                .join();
        discordApi.updateActivity("Step Into Meetup", "https://www.twitch.tv/stepintomeetups");
    }
}

@Slf4j
class CounterChannelListener implements MessageCreateListener {

    public static final String CHANNEL_NAME = "counting";
    public static final String CHECKMARK_EMOJI = ":white_check_mark:";
    public static final String X_EMOJI = ":x:";
    private final Object lock = new Object();
    private final Map<Server, ServerCounter> serverValueHolderMap = new HashMap<>();

    public CounterChannelListener(DiscordApi discordApi) {
        initializeServerCounterMap(discordApi);
    }

    private void initializeServerCounterMap(DiscordApi discordApi) {
        discordApi.getServerTextChannelsByName(CHANNEL_NAME)
                .forEach(serverTextChannel -> {
                    Server server = serverTextChannel.getServer();
                    serverTextChannel
                            .getMessagesUntil(this::hasProperReactionFromBot)
                            .thenCompose(messages -> {
                                ServerCounter pickedCounter = messages.isEmpty()
                                        ? ServerCounter.createStarting()
                                        : messages.getOldestMessage()
                                        .filter(message -> hasBotBasedEmojiOnMessage(message, CHECKMARK_EMOJI))
                                        .map(message -> new ServerCounter(Integer.parseInt(message.getContent()) + 1, message.getAuthor().getIdAsString()))
                                        .orElse(ServerCounter.createStarting());
                                return CompletableFuture.completedFuture(pickedCounter);
                            })
                            .thenAccept(serverCounter -> {
                                serverValueHolderMap.put(server, serverCounter);
                                log.info("Server with name: {} counter next value: {}", server.getName(), serverCounter.getNextValue());
                                serverTextChannel.sendMessage("Bot reconnected, next number should be: " + serverCounter.getNextValue());
                            })
                            .join();
                });
    }

    private boolean hasProperReactionFromBot(Message message) {
        return (hasBotBasedEmojiOnMessage(message, CHECKMARK_EMOJI) || hasBotBasedEmojiOnMessage(message, X_EMOJI)) && StringUtils.isNumeric(message.getContent());
    }

    private boolean hasBotBasedEmojiOnMessage(Message message, String emoji) {
        return message.getReactionByEmoji(EmojiParser.parseToUnicode(emoji))
                .filter(Reaction::containsYou)
                .isPresent();
    }

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        if (messageCreateEvent.getMessage().isServerMessage() && !messageCreateEvent.getMessageAuthor().isBotUser()) {
            Server server = messageCreateEvent.getServer().orElseThrow();
            ServerCounter serverCounter;
            if (serverValueHolderMap.containsKey(server)) {
                serverCounter = serverValueHolderMap.get(server);
            } else {
                serverCounter = serverValueHolderMap.putIfAbsent(server, new ServerCounter(1, ""));
            }
            String channelName = messageCreateEvent.getChannel()
                    .asServerTextChannel()
                    .map(Nameable::getName)
                    .orElseThrow();
            int nextValue = serverCounter.getNextValue();
            String lastUserId = serverCounter.getLastUserId();
            if (CHANNEL_NAME.equals(channelName)) {
                //synchronized (lock) {
                Message message = messageCreateEvent.getMessage();
                String messageContent = message.getContent();
                if (Integer.parseInt(messageContent) == nextValue && !lastUserId.equals(messageCreateEvent.getMessageAuthor().getIdAsString())) {
                    serverCounter.updateServerCounter(nextValue + 1, messageCreateEvent.getMessageAuthor().getIdAsString());
                    message.addReaction(EmojiParser.parseToUnicode(CHECKMARK_EMOJI)).join();
                } else {
                    message.getChannel().sendMessage("Somebody ruined the sequence, upcoming number was:" + nextValue + ", reseting counter, starting from 1 again.");
                    message.addReaction(EmojiParser.parseToUnicode(X_EMOJI));
                    serverCounter.updateServerCounter(1, "");
                }
                //}
            }
        }
    }

    @Data
    @AllArgsConstructor
    static class ServerCounter {
        private int nextValue = 1;
        private String lastUserId;

        public void updateServerCounter(int nextValue, String lastUserId) {
            this.nextValue = nextValue;
            this.lastUserId = lastUserId;
        }

        public static ServerCounter createStarting() {
            return new ServerCounter(1, "");
        }
    }
}
