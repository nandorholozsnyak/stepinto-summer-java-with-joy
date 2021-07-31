package hu.stepintomeetups;

import java.net.http.HttpRequest;

public abstract class AbstractAction {

    protected static final String DISCORD_API_URL = "https://discord.com/api/v8";
    protected static final String TOKEN = System.getenv("TOKEN");

    protected HttpRequest.Builder commonRequestBuilder() {
        return HttpRequest.newBuilder()
                .header("Authorization", "Bot " + TOKEN)
                .header("Content-Type", "application/json");
    }
}
