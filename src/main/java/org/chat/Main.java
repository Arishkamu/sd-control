package org.chat;

import org.chat.cli.CliClient;
import org.chat.gui.ChatGUI;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        String mode = args[0];
        if (mode.equals("cli")) {
            CliClient.main(Arrays.copyOfRange(args, 1, args.length));
        } else {
            ChatGUI.main(Arrays.copyOfRange(args, 1, args.length));
        }
    }

}
