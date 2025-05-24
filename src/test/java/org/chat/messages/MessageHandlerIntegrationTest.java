package org.chat.messages;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class MessageHandlerIntegrationTest {

    private MessageHandler handler;

    @BeforeEach
    void setUp() {
        handler = new MessageHandler("localhost", "main");
    }

    @AfterEach
    void tearDown() {
        if (handler != null) {
            handler.close();
        }
    }

    // checks basic send and receive
    @Test
    void testSendAndReceive() throws Exception {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
        handler.connect(queue::offer);
        handler.sendMessage("test");
        String received = queue.poll(2, TimeUnit.SECONDS);
        assertEquals("test", received);
    }

    // checks message delivery after switching exchanges
    @Test
    void testSwitchChannel() throws Exception {
        ArrayBlockingQueue<String> queue1 = new ArrayBlockingQueue<>(1);
        ArrayBlockingQueue<String> queue2 = new ArrayBlockingQueue<>(1);

        handler.connect(queue1::offer);
        handler.sendMessage("message one");
        assertEquals("message one", queue1.poll(2, TimeUnit.SECONDS));

        handler.switchChannel("secondChannel");
        handler.connect(queue2::offer);

        handler.sendMessage("message two");
        String received = queue2.poll(2, TimeUnit.SECONDS);
        assertEquals("message two", received);
    }

    // checks closing the handler
    @Test
    void testClose() throws Exception {
        handler.connect(msg -> {
        });
        assertTrue(handler.close());
    }

    // checks sending and receiving messages in the same order
    @Test
    void testSendAndReceiveMultipleMessages() throws Exception {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(2);

        handler.connect(queue::offer);
        handler.sendMessage("message one");
        handler.sendMessage("message two");

        String received1 = queue.poll(2, TimeUnit.SECONDS);
        String received2 = queue.poll(2, TimeUnit.SECONDS);
        assertEquals("message one", received1);
        assertEquals("message two", received2);
    }

    // checks receiving null from empty channel
    @Test
    void testReceiveTimeout() throws Exception {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(1);

        handler.connect(queue::offer);

        String received = queue.poll(1, TimeUnit.SECONDS);
        assertNull(received, "Expected no message to be received");
    }

    // checks reconnecting after closing
    @Test
    void testReconnectAfterClose() throws Exception {
        handler.connect(msg -> {
        });
        assertTrue(handler.close());

        // Recreate handler and connect again
        handler = new MessageHandler("localhost", "main");
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
        handler.connect(queue::offer);
        handler.sendMessage("test");

        String received = queue.poll(2, TimeUnit.SECONDS);
        assertEquals("test", received);
    }

    // checks sending an empty message
    @Test
    void testSendEmptyMessage() throws Exception {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
        handler.connect(queue::offer);
        handler.sendMessage("");
        String received = queue.poll(2, TimeUnit.SECONDS);
        assertEquals("", received);
    }
}
