package hu.stepintomeetups.live;

import com.vdurmont.emoji.EmojiParser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CounterExample {

    public static final String TOKEN = System.getenv("TOKEN");

    public static void main(String[] args) {
        System.out.printf("Hello Step Into!");
        DiscordApi discordApi = new DiscordApiBuilder().setToken(TOKEN)
                .addListener(new CounterChannelMessageListener())
                .login()
                .join();
        log.info("Invite URL:[{}]", discordApi.createBotInvite());
    }
}

class CounterChannelMessageListener implements MessageCreateListener {

    private Map<Server, ServerCounter> serverCounterMap = new HashMap();

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        ServerTextChannel serverTextChannel = event.getServerTextChannel().orElseThrow();
        String name = serverTextChannel.getName();
        MessageAuthor messageAuthor = event.getMessageAuthor();
        if ("counting".equals(name) && !messageAuthor.isBotUser()) {
            ServerCounter serverCounter = getServerCounter(event);
            Message message = event.getMessage();
            String content = message.getContent();
            if (StringUtils.isNumeric(content)) {
                int actualValue = Integer.parseInt(content);
                int expectedNext = serverCounter.getExpectedNext();
                String lastUserId = serverCounter.getLastUserId();
                if (actualValue == expectedNext && !lastUserId.equals(messageAuthor.getIdAsString())) {
                    message.addReaction(EmojiParser.parseToUnicode(":white_check_mark:"))
                            .join();
                    serverCounter.setExpectedNext(expectedNext + 1);
                    serverCounter.setLastUserId(messageAuthor.getIdAsString());
                } else {
                    message.addReaction(EmojiParser.parseToUnicode(":x:"))
                            .join();
                    serverTextChannel.sendMessage("Rossz érték, a várt érték:" + expectedNext + ", indulás 1-ről");
                    serverCounter.setExpectedNext(1);
                    serverCounter.setLastUserId("");
                }
            }
        }
    }

    private ServerCounter getServerCounter(MessageCreateEvent event) {
        Server server = event.getServer().orElseThrow();
        ServerCounter actualServerCounter;
        if (serverCounterMap.containsKey(server)) {
            actualServerCounter = serverCounterMap.get(server);
        } else {
            actualServerCounter = new ServerCounter();
            serverCounterMap.put(server, actualServerCounter);
        }
        return actualServerCounter;
    }

    @Data
    static class ServerCounter {
        private int expectedNext = 1;
        private String lastUserId = "";
    }
}
