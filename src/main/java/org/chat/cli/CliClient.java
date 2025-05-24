package org.chat.cli;

import org.chat.messages.MessageHandler;

import java.io.IOException;
import java.util.Scanner;

public class CliClient {
    private static final String DEFAULT_HOST = "127.0.0.1";
    private final MessageHandler messageHandler;


    private CliClient(String channelName, String host) {
        this.messageHandler = new MessageHandler(host, channelName);
    }

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            System.err.println(" Error: wrong number of arguments");
            System.exit(1);
        }

        String channelName = args[0];
        String host = args.length == 2 ? args[1] : DEFAULT_HOST;

        CliClient cliClient = new CliClient(channelName, host);

        try {
            cliClient.start(channelName);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void start(String channelName) throws Exception {
        System.out.println("Enter your username");

        // Подключаемся к нужному каналу по имени
        messageHandler.connect(this::writeCli);
        System.out.println("Connected to channel: " + channelName);
        System.out.println("Type '!switch <channel>' to change channels");

        readCli();
    }


    private void readCli() throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (!scanner.hasNextLine()) {
                continue;
            }
            String input = scanner.nextLine();
            if (input.contains("!switch")) {
                String newChannelName = input.substring(8);
                try {
                    messageHandler.switchChannel(newChannelName);
                    System.out.println("Switched to channel: " + newChannelName);
                } catch (IOException e) {
                    System.err.println("Failed switched to channel: " + newChannelName);
                }
            } else if (input.contains("!exit")) {
                if (!messageHandler.close()) {
                    System.out.println("Closing connection throw exception");
                }
                return;
            } else {
                messageHandler.sendMessage(input);
            }
        }
    }

    private void writeCli(String message) {
        System.out.println(message);
    }
}
