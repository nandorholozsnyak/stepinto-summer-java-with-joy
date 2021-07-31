package hu.stepintomeetups.pre;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

@Slf4j
public class StepIntoWazaaBotExample {

    private static final String TOKEN = System.getenv("TOKEN");

    public static void main(String[] args) throws Exception {
        JDA jda = JDABuilder.createDefault(TOKEN)
                .addEventListeners(new WazaaListener())
                .build();
        String inviteUrl = jda.getInviteUrl(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ);
        log.info("Invite URL:{}", inviteUrl);
    }

}

@Slf4j
class WazaaListener extends ListenerAdapter {

    private static final List<String> WAZA_URLS = List.of(
            "https://media.giphy.com/media/8bXtRaK3rHvxe/giphy.gif",
            "https://media.giphy.com/media/3hxk2aOwWmfOU/giphy.gif",
            "https://media.giphy.com/media/N1W2bgj7h5XdC/giphy.gif");

    @SneakyThrows
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (!event.getAuthor().isBot() && event.getMessage().getContentDisplay().toLowerCase().contains("waza")) {
            String image = WAZA_URLS.get(new Random().nextInt(WAZA_URLS.size()));
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setAuthor("Waza", "https://stepintomeetups.hu", image)
                    .setTitle("WAZAAAA")
                    .setDescription("Héj vedd fel te is")
                    .setImage(image)
                    .setFooter("Wazaaa");
            event.getChannel().sendMessage(embedBuilder.build()).queue(message -> log.info("Message sent successfully"), throwable -> log.error("Error during sending message", throwable));
        }
    }
}
