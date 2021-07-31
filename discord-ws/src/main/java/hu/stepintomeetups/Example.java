package hu.stepintomeetups;


public class Example {

    public static final String CHANNEL_ID = "864094351495397416";

    public static void main(String[] args) throws Exception {
        GetChannelAction getChannelAction = new GetChannelAction();
        getChannelAction.getChannel(CHANNEL_ID);
        SendMessageToChannelAction sendMessageToChannelAction = new SendMessageToChannelAction();
        sendMessageToChannelAction.sendMessageToChannel(CHANNEL_ID);
        ReactToMessageAction reactToMessageAction = new ReactToMessageAction();
        reactToMessageAction.joinToWebSocketAndStartListeningToEvents();
    }

}
