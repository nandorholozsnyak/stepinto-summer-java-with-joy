package hu.stepintomeetups.live;

import com.vdurmont.emoji.EmojiParser;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.event.message.reaction.ReactionRemoveEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.listener.message.reaction.ReactionAddListener;
import org.javacord.api.listener.message.reaction.ReactionRemoveListener;

import java.awt.*;
import java.util.List;
import java.util.Optional;

@Slf4j
public class StepIntoSummerAdv {

    public static final String TOKEN = System.getenv("TOKEN");

    public static void main(String[] args) {
        System.out.printf("Hello Step Into!");
        DiscordApi discordApi = new DiscordApiBuilder().setToken(TOKEN)
                .addListener(StepIntoSummerListener::new)
                .login()
                .join();
        log.info("Invite URL:[{}]", discordApi.createBotInvite());
    }

}

class StepIntoSummerListener implements MessageCreateListener, ReactionAddListener, ReactionRemoveListener {

    private final DiscordApi discordApi;

    private static final List<MeetupPresenter> PRESENTERS = List.of(
            MeetupPresenter.builder()
                    .meetupTopic("Felhő")
                    .name("Sági-Kazár Márk")
                    .picture("https://media.discordapp.net/attachments/848955307454627881/862694369253654528/05-sagi-kazar-mark.jpg?width=832&height=468")
                    .build(),
            MeetupPresenter.builder()
                    .meetupTopic("Adatbázisok")
                    .name("Miskolczi Zsolt")
                    .picture("https://media.discordapp.net/attachments/848955307454627881/862694374148407356/02-miskolczi-zsolt.jpg?width=832&height=468")
                    .build(),
            MeetupPresenter.builder()
                    .meetupTopic("Java")
                    .name("Holozsnyák Nándor")
                    .picture("https://media.discordapp.net/attachments/848955307454627881/862694377201336380/01-holozsnyak-nandor.jpg?width=832&height=468")
                    .build(),
            MeetupPresenter.builder()
                    .meetupTopic("Java")
                    .name("Nagy István")
                    .picture("https://media.discordapp.net/attachments/848955307454627881/862694374928547910/03-nagy-istvan.jpg?width=832&height=468")
                    .build(),
            MeetupPresenter.builder()
                    .meetupTopic("Tesztelés")
                    .name("Novák Ádám")
                    .picture("https://media.discordapp.net/attachments/848955307454627881/862694372112597062/04-novak-adam.jpg?width=832&height=468")
                    .build()
    );
    private static final String FORWARD = ":arrow_forward:";
    private static final String FORWARD_EMOJI = EmojiParser.parseToUnicode(FORWARD);
    private static final String BACKWARD = ":arrow_backward:";
    private static final String BACKWARD_EMOJI = EmojiParser.parseToUnicode(BACKWARD);
    private static final String PERSON_INDEX_EMOJI = ":computer:";
    public static final String MESSAGE_AUTHOR = "Step Into Meetup Summer Bot";
    public static final String MESSAGE_DESCRIPTION = "Step Into Meetup Summer Hírek";
    public static final String LOADING_EMOJI = EmojiParser.parseToUnicode(":arrows_counterclockwise:");

    public StepIntoSummerListener(DiscordApi discordApi) {
        this.discordApi = discordApi;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        MeetupPresenter meetupPresenter = PRESENTERS.get(0);
        if (event.getMessageContent().equals("!stepintosummerv3")) {
            EmbedBuilder embedBuilder = new EmbedBuilder().setAuthor("Step Into Meetup", "https://stepintomeetups.hu", "")
                    .setTitle("Ez itt egy title")
                    .setDescription("Ez itt egy description")
                    .addField("Név", meetupPresenter.getName())
                    .addField("Topic", meetupPresenter.getMeetupTopic())
                    .setImage(meetupPresenter.getPicture())
                    .setColor(Color.magenta);
            Message message = event.getChannel().sendMessage(embedBuilder).join();
            message.addReactions(BACKWARD_EMOJI, FORWARD_EMOJI).join();
        }
    }

    @Override
    public void onReactionAdd(ReactionAddEvent event) {
        User user = event.getUser().orElse(discordApi.getUserById(event.getUserIdAsString()).join());
        if (!user.isBot()) {

        }
    }

    @Override
    public void onReactionRemove(ReactionRemoveEvent event) {
        if (!event.getUser().orElseThrow().isBot()) {

        }
    }

    @Data
    @Builder
    static class MeetupPresenter {
        private String name;
        private String picture;
        private String meetupTopic;
    }
}
