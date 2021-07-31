package hu.stepintomeetups;


import java.net.URI;
import java.util.concurrent.TimeUnit;

public class ReactToMessageAction extends AbstractAction {

    public void joinToWebSocketAndStartListeningToEvents() throws Exception {
        GetWssEndpointAction getWssEndpointAction = new GetWssEndpointAction();
        GatewayResponse wssEndpoint = getWssEndpointAction.getWssEndpoint();
        DiscordClient discordClient = new DiscordClient(new URI(wssEndpoint.getUrl()));
        discordClient.connectBlocking();
        identify(discordClient);
    }

    private void identify(DiscordClient discordClient) throws InterruptedException {
        IdentifyCommand identifyCommand = new IdentifyCommand();
        identifyCommand.setData(IdentifyGatewayData.builder()
                .token(TOKEN)
                .intents(513)
                .properties(IdentifyGatewayData.IdentifyGatewayDataProperties.builder()
                        .os("Windows")
                        .device("Laptop")
                        .browser("Brave")
                        .build())
                .build());
        //TimeUnit.MILLISECONDS.sleep(3000);
        discordClient.identifyToServer(identifyCommand);
    }
}
