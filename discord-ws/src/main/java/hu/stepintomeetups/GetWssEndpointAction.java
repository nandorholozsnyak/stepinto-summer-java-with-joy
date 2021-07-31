package hu.stepintomeetups;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Log
public class GetWssEndpointAction extends AbstractAction {

    public GatewayResponse getWssEndpoint() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder()
                .build();
        HttpRequest getChannelRequest = commonRequestBuilder()
                .GET()
                .uri(URI.create(DISCORD_API_URL + "/gateway/bot"))
                .build();
        HttpResponse<String> response = httpClient.send(getChannelRequest, HttpResponse.BodyHandlers.ofString());
        ObjectMapper objectMapper = new ObjectMapper();
        String body = response.body();
        log.info("Response:[" + body + "]");
        return objectMapper.readValue(body, GatewayResponse.class);
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class GatewayResponse {
    private String url;
}