package stepintomeetups;

import io.quarkus.runtime.Startup;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@Slf4j
@Startup
@ApplicationScoped
public class DiscordApiStartup {

    @Inject
    DiscordApi discordApi;

    @PostConstruct
    public void init() {
        log.info("Starting up Discord BOT");
        log.info("Registering slash commands");
        registerCommand();
    }

    private void registerCommand() {
        String serverId = "863454904685035551";
        Server server = discordApi.getServerById(serverId).orElseThrow();
        getPing()
                .createForServer(server)
                .exceptionally(throwable -> {
                    log.error("Error during slash command registation", throwable);
                    return null;
                })
                .join();
    }

    private SlashCommandBuilder getPing() {
        return SlashCommand.with("ping", "Responds PONG!",
                List.of(SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "Csatorna", "Az a csatorna ahova menjen a PONG üzenet", true))
        )
                ;
    }

}
