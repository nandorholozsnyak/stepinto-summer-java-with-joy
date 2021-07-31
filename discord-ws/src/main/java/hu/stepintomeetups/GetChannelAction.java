package hu.stepintomeetups;

import lombok.extern.java.Log;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Log
public class GetChannelAction extends AbstractAction {

    public void getChannel(String channelId) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder()
                .build();
        HttpRequest getChannelRequest = commonRequestBuilder()
                .GET()
                .uri(URI.create(DISCORD_API_URL + "/channels/" + channelId))
                .build();
        HttpResponse<String> response = httpClient.send(getChannelRequest, HttpResponse.BodyHandlers.ofString());
        log.info("Response:[" + response.body() + "]");
    }
}
