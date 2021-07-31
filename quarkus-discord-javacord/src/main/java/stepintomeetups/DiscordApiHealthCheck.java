package stepintomeetups;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;
import org.javacord.api.DiscordApi;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Collectors;

@Readiness
@ApplicationScoped
public class DiscordApiHealthCheck implements HealthCheck {

    @Inject
    DiscordApi discordApi;

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("Discord API")
                .up()
                .withData("intents", discordApi.getIntents().stream().map(Enum::toString).collect(Collectors.joining(",")))
                .withData("current-shard", discordApi.getCurrentShard())
                .withData("total-shards", discordApi.getTotalShards())
                .build();
    }
}
