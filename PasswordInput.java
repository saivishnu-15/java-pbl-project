package com.smartqueue.util;

import java.io.Console;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/** Reads passwords without echoing them when the application is launched from a real terminal. */
public final class PasswordInput {
    private PasswordInput() { }

    public static String readPassword(String prompt) {
        Console console = System.console();
        if (console != null) {
            char[] chars = console.readPassword(prompt);
            return chars == null ? "" : new String(chars);
        }

        // IntelliJ's Run console does not expose java.io.Console. Keep the application
        // console-based and tell the user how to get hidden input instead of printing a password.
        System.out.println("Password input masking is available when this application is run from a system terminal.");
        System.out.println("For final demonstration, run the generated .class files from Command Prompt/PowerShell.");
        return readVisibleFallback(prompt);
    }

    private static String readVisibleFallback(String prompt) {
        System.out.print(prompt);
        try {
            return new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (Exception e) {
            return "";
        }
    }
}
