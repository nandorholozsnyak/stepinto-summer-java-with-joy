package hu.stepintomeetups.pre;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class StepIntoMeetupRegistrationExample {

    private static final String TOKEN = System.getenv("TOKEN");

    public static void main(String[] args) throws Exception {
        JDA jda = JDABuilder.createDefault(TOKEN)
                .addEventListeners(new BotJoinListener())
                .build();
        String inviteUrl = jda.getInviteUrl(Permission.USE_SLASH_COMMANDS, Permission.MESSAGE_WRITE, Permission.MESSAGE_READ);
        log.info("Invite URL:{}", inviteUrl);

    }
}

@Slf4j
class BotJoinListener extends ListenerAdapter {

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        JDA jda = event.getJDA();
        CommandData command = new CommandData("meetup", "Register or list all the meetups")
                .addSubcommands(
                        new SubcommandData("registration", "Registration for a new meetup event with an idea")
                                .addOption(OptionType.STRING, "name", "Name of the meetup")
                                .addOption(OptionType.STRING, "topic", "Topic of the meetup")
                        ,
                        new SubcommandData("list", "List registered meetups"));
        jda.upsertCommand(command)

                .queue(cmd -> {
            log.info("Command has been registered:{}", cmd.toString());
        });
    }
}