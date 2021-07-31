package stepintomeetups;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import javax.enterprise.context.ApplicationScoped;

@DiscordListener
@ApplicationScoped
public class MessageComponentChannelListener implements MessageCreateListener {

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (event.getMessage().getContent().equals("!stepintosummer")) {
            TextChannel channel = event.getChannel();
            ActionRow actionRow = ActionRow.of(
                    Button.primary("like", "Szeretem a Step Into Summert"),
                    Button.danger("no-like", "Még nem szeretem a Step Into Summert"));
            new MessageBuilder()
                    .setContent("Mi a véleményed a Step Into Summer-ről?")
                    .addComponents(actionRow)
                    .send(channel);
        }
    }

}
