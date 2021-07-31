package hu.stepintomeetups;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.java.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log
public class DiscordClient extends WebSocketClient {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    protected static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);

    public DiscordClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public DiscordClient(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info("new connection opened");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        try {
            DiscordMessage discordMessage = objectMapper.readValue(message, DiscordMessage.class);
            handlingIncomingMessage(discordMessage);
        } catch (JsonProcessingException e) {
            log.warning("Error during parsing message:" + message);
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(ByteBuffer message) {
        log.info("received ByteBuffer");
    }

    @Override
    public void onError(Exception ex) {
        log.warning("an error occurred:" + ex);
    }

    /**
     * @param identifyCommand
     */
    public void identifyToServer(IdentifyCommand identifyCommand) {
        log.info("Identifying to server with command:[" + identifyCommand + "]");
        sendCommandAndHandleError(identifyCommand);
    }


    private void handlingIncomingMessage(DiscordMessage discordMessage) {
        log.info("Handling message from server:[" + discordMessage + "]");
        //Gateway hello, we must send a heartbeat
        if (discordMessage.getOperationCode() == 10) {
            try {
                HelloGatewayData helloGatewayData = objectMapper.readValue(objectMapper.writeValueAsString(discordMessage.getData()), HelloGatewayData.class);
                SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(this::sendHeartbeat, 0, helloGatewayData.getHeartbeatInterval(), TimeUnit.MILLISECONDS);
            } catch (JsonProcessingException e) {
                log.warning("Error during accepting and sending heartbeat at startup. Exception:" + e);
            }
        } else if (discordMessage.getOperationCode() == 1) {
            //Forced heartbeat ask
            sendHeartbeat();
        } else if (discordMessage.getOperationCode() == 11) {
            //ACK for the client heartbeat
            log.info("Heartbeat ACK arrived");
        } else if (discordMessage.getOperationCode() == 0) {
            //Dispatch event which then stores the event which should be handled.
            String eventType = discordMessage.getEventType();
            if (DiscordEventType.MESSAGE_CREATE.equals(eventType)) {
                handleMessageCreateEvent(discordMessage);
            }

        }
    }

    private void sendHeartbeat() {
        HeartbeatCommand heartbeatCommand = new HeartbeatCommand();
        sendCommandAndHandleError(heartbeatCommand);
    }

    private void sendCommandAndHandleError(DiscordMessage discordMessage) {
        try {
            log.info("Sending message to server:[" + discordMessage + "]");
            send(objectMapper.writeValueAsString(discordMessage));
        } catch (JsonProcessingException e) {
            log.warning("Error during sending message:" + discordMessage);
        }
    }

    private void handleMessageCreateEvent(DiscordMessage discordMessage) {
        try {
            String rawData = objectMapper.writeValueAsString(discordMessage.getData());
            MessageEventData discordMessageData = objectMapper.readValue(rawData, MessageEventData.class);
            log.info("Message create event discordMessageData:" + discordMessageData);
            if (!discordMessageData.getAuthor().isBot()) {
                SendSimpleMessageToChannelAction action = new SendSimpleMessageToChannelAction();
                action.sendMessageToChannel(discordMessageData.getChannelId(), "Hello neked is");
            }
        } catch (Exception e) {
            log.warning("Error during sending message: " + e);
        }
    }
}

class DiscordEventType {
    public static final String MESSAGE_CREATE = "MESSAGE_CREATE";
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class DiscordMessage {

    /**
     * Event name
     */
    @JsonProperty("t")
    protected String eventType;

    /**
     * Sequence
     */
    @JsonProperty("s")
    protected int sequence;

    /**
     * OP code
     */
    @JsonProperty("op")
    protected int operationCode;

    /**
     * Data
     */
    @JsonProperty("d")
    protected Object data;
}

interface DiscordGatewayData {
}

@ToString(callSuper = true)
class IdentifyCommand extends DiscordMessage {

    public IdentifyCommand() {
        this.operationCode = 2;
    }
}

@ToString(callSuper = true)
class HeartbeatCommand extends DiscordMessage {

    public HeartbeatCommand() {
        this.operationCode = 1;
    }
}

@ToString(callSuper = true)
class HelloCommand extends DiscordMessage {

    public HelloCommand() {
        this.operationCode = 10;
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class HelloGatewayData implements DiscordGatewayData {

    @JsonProperty("heartbeat_interval")
    private int heartbeatInterval;

}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class IdentifyGatewayData implements DiscordGatewayData {
    @ToString.Exclude
    private String token;
    private int intents;
    private IdentifyGatewayDataProperties properties;

    @Data
    @Builder
    @AllArgsConstructor
    static class IdentifyGatewayDataProperties {
        @JsonProperty("$os")
        private String os;
        @JsonProperty("$browser")
        private String browser;
        @JsonProperty("$device")
        private String device;
    }
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class MessageEventData implements DiscordGatewayData {
    private String id;
    @JsonProperty("channel_id")
    private String channelId;
    private String content;
    private Author author;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Author {
        private boolean bot;
    }

}

