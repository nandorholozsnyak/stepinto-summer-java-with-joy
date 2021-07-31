package stepintomeetups;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.listener.GloballyAttachableListener;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class DiscordApiProducer {

    @Inject
    @ConfigProperty(name = "discord.token")
    private String token;

    @Inject
    @DiscordListener
    private Instance<GloballyAttachableListener> globallyAttachableListeners;

    @Produces
    public DiscordApi produceDiscordApi() {
        DiscordApiBuilder discordApiBuilder = new DiscordApiBuilder()
                .setToken(token);
        globallyAttachableListeners.forEach(discordApiBuilder::addListener);
        return discordApiBuilder
                .login()
                .join();
    }

}
