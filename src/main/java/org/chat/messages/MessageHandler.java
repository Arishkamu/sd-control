package org.chat.messages;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class MessageHandler {
    private final String host;
    private Connection connection;
    private Channel channel;
    private String queueName;
    private String currentChannel;
    private OnMessageReceived onReceived;

    public MessageHandler(String host, String initialChannel) {
        this.host = host;
        this.currentChannel = initialChannel;
    }

    public interface OnMessageReceived {
        void onMessage(String message);
    }

    public void connect(OnMessageReceived onReceive) throws IOException, TimeoutException {
        // create factory, channel, connection
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        connection = factory.newConnection();
        channel = connection.createChannel();

        // set queue parameters
        queueName = channel.queueDeclare().getQueue();
        onReceived = onReceive;
        switchChannel(currentChannel);
    }

    public void switchChannel(String newChannel) throws IOException {
        // check if channel is set. Throw otherwise
        if (channel == null) throw new IllegalStateException("Channel not open");

        // check if queue is already bind
        if (currentChannel != null) {
            channel.queueUnbind(queueName, currentChannel, "");
        }

        // set new channel and queue
        currentChannel = newChannel;
        channel.exchangeDeclare(currentChannel, BuiltinExchangeType.FANOUT, true);
        channel.queueBind(queueName, currentChannel, "");

        // set consumer
        channel.basicConsume(queueName, true, (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            onReceived.onMessage(message);
        }, consumerTag -> {});
    }

    // send message to currentChannel
    public void sendMessage(String message) throws Exception {
        channel.basicPublish(currentChannel, "", null, message.getBytes(StandardCharsets.UTF_8));
    }

    // close channel and connection
    public boolean close() {
        try {
            channel.close();
            connection.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
