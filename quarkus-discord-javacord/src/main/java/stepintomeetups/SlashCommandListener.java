package stepintomeetups;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.ButtonInteraction;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import javax.enterprise.context.ApplicationScoped;

@DiscordListener
@ApplicationScoped
public class SlashCommandListener implements SlashCommandCreateListener, MessageComponentCreateListener {

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        if (event.getInteraction().asSlashCommandInteraction().isPresent()) {
            SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
            String commandName = slashCommandInteraction.getCommandName();
            InteractionImmediateResponseBuilder immediateResponder = slashCommandInteraction.createImmediateResponder();
            try {
                if (commandName.equals("ping")) {
                    ServerTextChannel serverTextChannel = slashCommandInteraction.getFirstOption().orElseThrow().getChannelValue().orElseThrow().asServerTextChannel().orElseThrow();
                    serverTextChannel.sendMessage("Pong ide");
                }
            } finally {
                immediateResponder.respond();
            }
        }

    }

    @Override
    public void onComponentCreate(MessageComponentCreateEvent event) {
        if (event.getInteraction().asMessageComponentInteraction().isPresent()) {
            MessageComponentInteraction messageComponentInteraction = event.getInteraction().asMessageComponentInteraction().orElseThrow();
            InteractionImmediateResponseBuilder immediateResponder = messageComponentInteraction.createImmediateResponder();
            if (messageComponentInteraction.asButtonInteraction().isPresent()) {
                ButtonInteraction buttonInteraction = messageComponentInteraction.asButtonInteraction().get();
                String customId = buttonInteraction.getCustomId();
                if (customId.equals("like")) {
                    immediateResponder.setContent("Köszönjük szépen a visszajelzést");
                } else if (customId.equals("no-like")) {
                    immediateResponder.setContent("Reméljük később mégiscsak belénk szeretsz!");
                }
            }
            immediateResponder.respond();
        }
    }
}
