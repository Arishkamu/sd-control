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
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        connection = factory.newConnection();
        channel = connection.createChannel();
        queueName = channel.queueDeclare().getQueue();
        onReceived = onReceive;
        switchChannel(currentChannel);
    }

    public void switchChannel(String newChannel) throws IOException {
        if (channel == null) throw new IllegalStateException("Channel not open");

        if (currentChannel != null) {
            channel.queueUnbind(queueName, currentChannel, "");
        }

        currentChannel = newChannel;
        channel.exchangeDeclare(currentChannel, BuiltinExchangeType.FANOUT, true);
        channel.queueBind(queueName, currentChannel, "");

        channel.basicConsume(queueName, true, (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            onReceived.onMessage(message);
        }, consumerTag -> {});
    }

    public void sendMessage(String message) throws Exception {
        channel.basicPublish(currentChannel, "", null, message.getBytes(StandardCharsets.UTF_8));
    }

    public boolean close() {
        try {
            channel.close();
            connection.close();
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }
}
