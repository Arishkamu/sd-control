package org.chat;

import static org.junit.jupiter.api.Assertions.*;
import org.chat.messages.MessageHandler;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class IntegrationTest {

    private static final String host = "127.0.0.1";
    private static final String channelName1 = "first-channel";
    private final String channelName2 = "second-channel";

    private static final List<String> messages1 = new ArrayList<>();
    private static final List<String> messages2 = new ArrayList<>();
    private static MessageHandler handler1;
    private static MessageHandler handler2;


    @BeforeAll
    public static void beforeAll() throws IOException, TimeoutException {
        handler1 = new MessageHandler(host, channelName1);
        handler1.connect(messages1::add);

        handler2 = new MessageHandler(host, channelName1);
        handler2.connect(messages2::add);
    }

    @Test
    @Order(1)
    public void sentTest() throws Exception {
        // check that send correctly
        String msg1 = "Hello World!";
        handler1.sendMessage(msg1);
        Thread.sleep(100);
        assertArrayEquals(new String[]{msg1}, messages1.toArray());
        assertArrayEquals(new String[]{msg1}, messages2.toArray());

        // check that send correctly
        String msg2 = "Message 2";
        handler1.sendMessage(msg2);
        Thread.sleep(100);
        assertArrayEquals(new String[]{msg1, msg2}, messages1.toArray());
        assertArrayEquals(new String[]{msg1, msg2}, messages2.toArray());

        // check that send correctly in other direction
        String msg3 = "Mew supper pupper message";
        handler2.sendMessage(msg3);
        Thread.sleep(100);
        assertArrayEquals(new String[]{msg1, msg2, msg3}, messages1.toArray());
        assertArrayEquals(new String[]{msg1, msg2, msg3}, messages2.toArray());
    }

    @Test
    @Order(2)
    public void switchTest() throws Exception {
        messages1.clear();
        messages2.clear();
        handler1.switchChannel(channelName2);

        // check that nothing after switch
        String msgNew1 = "Hello World!";
        handler1.sendMessage(msgNew1);
        Thread.sleep(100);
        assertArrayEquals(new String[]{msgNew1}, messages1.toArray());
        assertArrayEquals(new String[]{}, messages2.toArray());

        // check that nothing after switch other direction
        String msgNew2 = "Message 2";
        handler2.sendMessage(msgNew2);
        Thread.sleep(100);
        assertArrayEquals(new String[]{msgNew2}, messages2.toArray());
        assertArrayEquals(new String[]{msgNew1}, messages1.toArray());
    }

    @Order(3)
    @Test
    public void switchTestNotSee() throws Exception {
        messages1.clear();
        messages2.clear();

        // check that nothing before switch other direction
        String msgNew1 = "Hello World!";
        handler1.sendMessage(msgNew1);
        Thread.sleep(100);
        assertArrayEquals(new String[]{msgNew1}, messages1.toArray());
        assertArrayEquals(new String[]{}, messages2.toArray());

        handler2.switchChannel(channelName2);

        // check that okay again
        String msgNew2 = "Message 2";
        handler1.sendMessage(msgNew2);
        Thread.sleep(100);
        assertArrayEquals(new String[]{msgNew2}, messages2.toArray());
        assertArrayEquals(new String[]{msgNew1, msgNew2}, messages1.toArray());

        // check that okay again
        String msgNew3 = "Message 2";
        handler2.sendMessage(msgNew3);
        Thread.sleep(100);
        assertArrayEquals(new String[]{msgNew2, msgNew3}, messages2.toArray());
        assertArrayEquals(new String[]{msgNew1, msgNew2, msgNew3}, messages1.toArray());
    }

    @Order(4)
    @Test
    public void switchTestNewHandler() throws Exception {
        messages1.clear();
        messages2.clear();

        List<String> messages3 = new ArrayList<>();
        MessageHandler handler3 = new MessageHandler(host, channelName2);
        handler3.connect(messages3::add);

        // check that seen only last messages
        String msgNew1 = "Hello World!";
        handler1.sendMessage(msgNew1);
        Thread.sleep(200);
        assertArrayEquals(new String[]{msgNew1}, messages1.toArray());
        assertArrayEquals(new String[]{msgNew1}, messages3.toArray());
    }
}
