package hu.stepintomeetups.pre;

import com.vdurmont.emoji.EmojiParser;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.ButtonStyle;
import org.javacord.api.entity.message.component.HighLevelComponent;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.InteractionCreateEvent;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.event.message.reaction.ReactionRemoveEvent;
import org.javacord.api.event.message.reaction.SingleReactionEvent;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.InteractionCreateListener;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.listener.message.reaction.ReactionAddListener;
import org.javacord.api.listener.message.reaction.ReactionRemoveListener;

import java.util.List;

public class StepIntoMeetupSummerEventsExample {

    private static final String TOKEN = System.getenv("TOKEN");

    public static void main(String[] args) {
        DiscordApi discordApi = new DiscordApiBuilder()
                .setToken(TOKEN)
                .addListener(StepIntoSummerEventListener::new)
                .login()
                .join();
        discordApi.updateActivity("Step Into Meetup", "https://www.twitch.tv/stepintomeetups");
        discordApi.getGlobalSlashCommands().thenAccept(slashCommands -> slashCommands.forEach(SlashCommand::deleteGlobal));
        SlashCommand
                .with("stepintosummer", "Használd a parancsot a nyári események áttekintésére",
                        List.of(SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, "live", "Elküldi a Twitch csatorna linkjét ahol az előadás sorozat zajlik"),
                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND_GROUP, "presenters", "Az előadókhoz tartozó parancsok",
                                        List.of(SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, "list", "Előadók listája"),
                                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "by-topic", "Témakör alapú keresés",
                                                        List.of(SlashCommandOption.create(SlashCommandOptionType.STRING, "topic", "Témakör neve", true))),
                                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "by-name", "Témakör alapú keresés",
                                                        List.of(SlashCommandOption.create(SlashCommandOptionType.STRING, "name", "Előadó neve", true))))
                                )
                        )
                )
                .createForServer(discordApi.getServerById("845013074880102500").orElseThrow())

        ;

    }

}

@Slf4j
class StepIntoSummerEventListener implements MessageCreateListener, ReactionAddListener, ReactionRemoveListener, MessageComponentCreateListener, InteractionCreateListener {

    private static final List<MeetupPresenter> PRESENTERS = List.of(
            MeetupPresenter.builder()
                    .meetupTopic("Felhő")
                    .name("Sági-Kazár Márk")
                    .picture("https://media.discordapp.net/attachments/848955307454627881/862694369253654528/05-sagi-kazar-mark.jpg?width=832&height=468")
                    .nicknames(List.of("Márk"))
                    .build(),
            MeetupPresenter.builder()
                    .meetupTopic("Adatbázisok")
                    .name("Miskolczi Zsolt")
                    .picture("https://media.discordapp.net/attachments/848955307454627881/862694374148407356/02-miskolczi-zsolt.jpg?width=832&height=468")
                    .nicknames(List.of("Zsolti"))
                    .build(),
            MeetupPresenter.builder()
                    .meetupTopic("Java")
                    .name("Holozsnyák Nándor")
                    .picture("https://media.discordapp.net/attachments/848955307454627881/862694377201336380/01-holozsnyak-nandor.jpg?width=832&height=468")
                    .nicknames(List.of("Nándi"))
                    .build(),
            MeetupPresenter.builder()
                    .meetupTopic("Java")
                    .name("Nagy István")
                    .picture("https://media.discordapp.net/attachments/848955307454627881/862694374928547910/03-nagy-istvan.jpg?width=832&height=468")
                    .nicknames(List.of("Esta"))
                    .build(),
            MeetupPresenter.builder()
                    .meetupTopic("Tesztelés")
                    .name("Novák Ádám")
                    .picture("https://media.discordapp.net/attachments/848955307454627881/862694372112597062/04-novak-adam.jpg?width=832&height=468")
                    .nicknames(List.of("Ádi"))
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

    private final DiscordApi discordApi;

    public StepIntoSummerEventListener(DiscordApi discordApi) {
        this.discordApi = discordApi;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (event.getMessageContent().equals("!stepintosummer")) {
            MeetupPresenter meetupPresenter = PRESENTERS.get(0);
            EmbedBuilder message = createMeetupPresenterEmbedMessage(meetupPresenter, "1");
            event.getChannel()
                    .sendMessage(message)
                    .thenAccept(msg -> {
                        msg.addReactions(BACKWARD_EMOJI, FORWARD_EMOJI)
                                .join();
                    });
        } else if (event.getMessageContent().equals("!stepintosummerv2")) {
            MeetupPresenter meetupPresenter = PRESENTERS.get(0);
            EmbedBuilder message = createMeetupPresenterEmbedMessage(meetupPresenter, "1");
            createInteractiveMessage(message)
                    .send(event.getChannel())
                    .join();
        }
    }

    private MessageBuilder createInteractiveMessage(EmbedBuilder message) {
        return new MessageBuilder()
                .addEmbed(message)
                .addComponents(ActionRow.of(Button.create("backward", ButtonStyle.SUCCESS, "Előző"),
                        Button.create("forward", ButtonStyle.SUCCESS, "Következő")
                ))
                ;
    }

    private HighLevelComponent createButtons() {
        return ActionRow.of(Button.create("backward", ButtonStyle.SUCCESS, "Előző"),
                Button.create("forward", ButtonStyle.SUCCESS, "Következő"));
    }

    private EmbedBuilder createMeetupPresenterEmbedMessage(MeetupPresenter meetupPresenter, String presenterIndex) {
        EmbedBuilder message = new EmbedBuilder()
                .setAuthor(MESSAGE_AUTHOR)
                .setDescription(MESSAGE_DESCRIPTION)
                .setFooter("https://stepintomeetups.hu | " + presenterIndex + "/" + (PRESENTERS.size()));
        message.setImage(meetupPresenter.getPicture())
                .addField("Név", meetupPresenter.getName())
                .addField("Téma", meetupPresenter.getMeetupTopic());
        return message;
    }

    @Override
    public void onReactionAdd(ReactionAddEvent event) {
        User user = getUserFromReaction(event);
        Message message = getMessage(event);
        try {
            if (user.isBot() && isSummerMeetupMessage(message)) {
                return;
            }
            message.addReaction(LOADING_EMOJI).join();
            paginateMessage(event, message);
        } finally {
            message.removeReactionByEmoji(LOADING_EMOJI).join();
        }
    }

    private boolean isSummerMeetupMessage(Message message) {
        if (!message.getEmbeds().isEmpty()) {
            Embed embed = message.getEmbeds().get(0);
            return embed.getAuthor().orElseThrow().getName().equals(MESSAGE_AUTHOR) && embed.getDescription().orElseThrow().equals(MESSAGE_DESCRIPTION);
        }
        return false;
    }

    @Override
    public void onReactionRemove(ReactionRemoveEvent event) {
        User user = getUserFromReaction(event);
        Message message = getMessage(event);
        try {
            if (user.isBot() && isSummerMeetupMessage(message)) {
                return;
            }
            message.addReaction(LOADING_EMOJI).join();
            paginateMessage(event, message);
        } finally {
            message.removeReactionByEmoji(LOADING_EMOJI).join();
        }
    }

    private Message getMessage(SingleReactionEvent singleReactionEvent) {
        return singleReactionEvent.getMessage().orElse(discordApi.getCachedMessageById(singleReactionEvent.getMessageId()).orElseThrow());
    }

    private void paginateMessage(SingleReactionEvent event, Message message) {
        if (event.getReaction().orElseThrow().getEmoji().equalsEmoji(BACKWARD_EMOJI)) {
            String actualPresenter = getActualPresenterIndex(message);
            int listIndex = Integer.parseInt(actualPresenter.trim()) - 1;
            int upcomingIndex = listIndex - 1;
            upcomingIndex = upcomingIndex <= 0 ? 0 : upcomingIndex;
            MeetupPresenter meetupPresenter = PRESENTERS.get(upcomingIndex);
            message.edit(createMeetupPresenterEmbedMessage(meetupPresenter, String.valueOf(upcomingIndex + 1)));
        } else if (event.getReaction().orElseThrow().getEmoji().equalsEmoji(FORWARD_EMOJI)) {
            String actualPresenter = getActualPresenterIndex(message);
            int listIndex = Integer.parseInt(actualPresenter.trim()) - 1;
            int upcomingIndex = listIndex + 1;
            upcomingIndex = upcomingIndex >= PRESENTERS.size() ? PRESENTERS.size() - 1 : upcomingIndex;
            MeetupPresenter meetupPresenter = PRESENTERS.get(upcomingIndex);
            message.edit(createMeetupPresenterEmbedMessage(meetupPresenter, String.valueOf(upcomingIndex + 1)));
        }
    }

    private String getActualPresenterIndex(Message message) {
        Embed embedMessage = message.getEmbeds().get(0);
        String paginationParams = embedMessage.getFooter().orElseThrow().getText().orElseThrow().split("\\|")[1];
        return paginationParams.split("/")[0];
    }

    private User getUserFromReaction(SingleReactionEvent singleReactionEvent) {
        return singleReactionEvent.getUser().orElse(discordApi.getUserById(singleReactionEvent.getUserId()).join());
    }

    @Override
    public void onComponentCreate(MessageComponentCreateEvent event) {
        MessageComponentInteraction messageComponentInteraction = event.getMessageComponentInteraction();
        messageComponentInteraction.createImmediateResponder().respond();
    }

    @Override
    public void onInteractionCreate(InteractionCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction().orElseThrow();
        SlashCommandInteractionOption slashCommandInteractionOption = slashCommandInteraction.getFirstOption().orElseThrow();
        InteractionImmediateResponseBuilder immediateResponder = slashCommandInteraction.createImmediateResponder();
        try {
            if (slashCommandInteractionOption.getName().equals("live")) {
                immediateResponder.addEmbed(new EmbedBuilder()
                        .setTitle("Twitch")
                        .setDescription("https://twitch.com/stepintomeetups"));
            } else if (slashCommandInteractionOption.getName().equals("presenters")) {
                SlashCommandInteractionOption presentersOption = slashCommandInteractionOption.getFirstOption().orElseThrow();
                if (presentersOption.getName().equals("list")) {
                    immediateResponder.addEmbed(new EmbedBuilder()
                            .setTitle("Lista lekérve")
                            .setDescription("https://twitch.com/stepintomeetups"));
                } else if (presentersOption.getName().equals("by-topic")) {
                    SlashCommandInteractionOption presentersParameter = presentersOption.getFirstOption().orElseThrow();
                    MeetupPresenter byTopic = findByTopic(presentersParameter.getStringValue().orElseThrow());
                    EmbedBuilder message = createMeetupPresenterEmbedMessage(byTopic, "1");
                    HighLevelComponent buttons = createButtons();
                    immediateResponder.addEmbed(message)
                            .addComponents(buttons);
                } else if (presentersOption.getName().equals("by-name")) {
                    SlashCommandInteractionOption presentersParameter = presentersOption.getFirstOption().orElseThrow();
                    MeetupPresenter byNickname = findByNickname(presentersParameter.getStringValue().orElseThrow());
                    EmbedBuilder message = createMeetupPresenterEmbedMessage(byNickname, "1");
                    HighLevelComponent buttons = createButtons();
                    immediateResponder.addEmbed(message)
                            .addComponents(buttons);
                }
            }
        } finally {
            immediateResponder.respond()
                    .exceptionally(throwable -> {
                        log.error("Error during sending followup message", throwable);
                        return null;
                    })
                    .join();
        }
    }

    @Data
    @Builder
    static class MeetupPresenter {
        private String name;
        private String picture;
        private String meetupTopic;
        private List<String> nicknames;
    }

    private MeetupPresenter findByNickname(String name) {
        return PRESENTERS.stream().filter(meetupPresenter -> meetupPresenter.getNicknames().contains(name)).findFirst().orElse(null);
    }

    private MeetupPresenter findByTopic(String topic) {
        return PRESENTERS.stream().filter(meetupPresenter -> meetupPresenter.getMeetupTopic().contains(topic)).findFirst().orElse(null);
    }
}

