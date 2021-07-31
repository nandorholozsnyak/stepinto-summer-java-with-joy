package stepintomeetups;

import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@DiscordListener
@ApplicationScoped
public class CounterChannelMessageListener implements MessageCreateListener {

    @Override
    @Timed(name = "CounterChannelMessageListener#onMessageCreate", description = "Méri a metódus futás idejét", unit = MetricUnits.MILLISECONDS)
    @Counted(name = "CounterChannelMessageListener#onMessageCreate:counted", description = "Méri a metódus meghívásának a számát")
    public void onMessageCreate(MessageCreateEvent event) {
        ServerTextChannel serverTextChannel = event.getServerTextChannel().orElseThrow();
        String name = serverTextChannel.getName();
        MessageAuthor messageAuthor = event.getMessageAuthor();
        if ("counting".equals(name) && !messageAuthor.isBotUser()) {
            ServerCounter serverCounter = getServerCounter(event);
            try {
                Message message = event.getMessage();
                String content = message.getContent();
                if (StringUtils.isNumeric(content)) {
                    int actualValue = Integer.parseInt(content);
                    int expectedNext = serverCounter.nextValue;
                    String lastUserId = serverCounter.lastUserId;
                    if (actualValue == expectedNext && !lastUserId.equals(messageAuthor.getIdAsString())) {
                        message.addReaction(EmojiParser.parseToUnicode(":white_check_mark:"))
                                .join();
                        serverCounter.nextValue = expectedNext + 1;
                        serverCounter.lastUserId = messageAuthor.getIdAsString();
                    } else {
                        message.addReaction(EmojiParser.parseToUnicode(":x:"))
                                .join();
                        serverTextChannel.sendMessage("Rossz érték, a várt érték:" + expectedNext + ", indulás 1-ről");
                        serverCounter.nextValue = 1;
                        serverCounter.lastUserId = "";
                    }
                }
            } finally {
                serverCounter.persistOrUpdate();
            }
        }
    }

    private ServerCounter getServerCounter(MessageCreateEvent event) {
        String serverId = event.getServer().orElseThrow().getIdAsString();
        return Optional.ofNullable(ServerCounter.findByServerId(serverId))
                .orElse(ServerCounter.builder()
                        .serverId(serverId)
                        .lastUserId("")
                        .nextValue(1)
                        .build());
    }
}
