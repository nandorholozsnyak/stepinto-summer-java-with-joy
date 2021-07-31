package hu.stepintomeetups;

import lombok.extern.java.Log;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Log
public class SendMessageToChannelAction extends AbstractAction {

    public void sendMessageToChannel(String channelId) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder()
                .build();
        HttpRequest sendMessage = commonRequestBuilder()
                .uri(URI.create(DISCORD_API_URL + "/channels/" + channelId + "/messages"))
                .POST(HttpRequest.BodyPublishers.ofString("{\n" +
                        "  \"content\": \"Hello Step into meetup Stream!\",\n" +
                        "  \"tts\": false,\n" +
                        "  \"embeds\": [{\n" +
                        "    \"title\": \"Ez itt egy embedded üzenet\",\n" +
                        "    \"description\": \"Ez pedig itt a belső része.\"\n" +
                        "  }]\n" +
                        "}"))
                .build();
        HttpResponse<String> sendMessageResponse = httpClient.send(sendMessage, HttpResponse.BodyHandlers.ofString());
        if (sendMessageResponse.statusCode() != 200) {
            throw new RuntimeException("Nem sikerült az üzenet elküldése, státusz kód:[" + sendMessageResponse.statusCode() + "]");
        }
        log.info("Response:[" + sendMessageResponse.body() + "]");
    }

}
