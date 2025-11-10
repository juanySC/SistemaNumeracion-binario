package org.example.ui;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;

import javax.swing.JOptionPane;
import java.io.IOException;

public class InputHelper {

    // ayuda en la lectura de una línea con tiempo de espera
    public static String readLineWithTimeout(Screen screen, TextGraphics tg, int promptRow, int promptCol, int timeoutSeconds) throws IOException {
        StringBuilder sb = new StringBuilder();
        long deadline = System.currentTimeMillis() + timeoutSeconds * 1000L;
        try { screen.refresh(); } catch (IOException ignored) {}

        while (System.currentTimeMillis() < deadline) {
            KeyStroke key = screen.pollInput();
            if (key == null) {
                try { Thread.sleep(40); } catch (InterruptedException ignored) {}
                continue;
            }
            switch (key.getKeyType()) {
                case Character:
                    sb.append(key.getCharacter());
                    tg.putString(promptCol, promptRow, sb.toString() + " ");
                    screen.refresh();
                    break;
                case Backspace:
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length()-1);
                        tg.putString(promptCol, promptRow, sb.toString() + " ");
                        screen.refresh();
                    }
                    break;
                case Enter:
                    return sb.toString().trim();
                case Escape:
                    return "";
                default:
            }
        }
        return null; // timeout
    }

    // ayuda en la lectura de un solo carácter con tiempo de espera
    public static String readSingleCharWithTimeout(Screen screen, TextGraphics tg, int promptRow, int promptCol, int timeoutSeconds) throws IOException {
        long deadline = System.currentTimeMillis() + timeoutSeconds * 1000L;
        try { screen.refresh(); } catch (IOException ignored) {}
        while (System.currentTimeMillis() < deadline) {
            KeyStroke key = screen.pollInput();
            if (key == null) {
                try { Thread.sleep(40); } catch (InterruptedException ignored) {}
                continue;
            }
            switch (key.getKeyType()) {
                case Character:
                    char c = key.getCharacter();
                    tg.putString(promptCol, promptRow, Character.toString(c) + " ");
                    try { screen.refresh(); } catch (IOException ignored) {}
                    return Character.toString(c);
                case Enter:
                    break;
                default:
            }
        }
        return null;
    }


    /**fallbackModalAndEcho  método de respaldo para entrada modal usando JOptionPane
     o sea que emerge otra ventana para pedir la entrada**/
    public static String fallbackModalAndEcho(Screen screen, TextGraphics tg, int echoRow, String message, String title) {
        String resp = JOptionPane.showInputDialog(null, message, title, JOptionPane.QUESTION_MESSAGE);
        if (resp != null) {
            try { tg.putString(1, echoRow, "Entrada (modal): " + resp); screen.refresh(); } catch (IOException | RuntimeException ignored) {}
        } else {
            try { tg.putString(1, echoRow, "Entrada cancelada (modal)."); screen.refresh(); } catch (IOException | RuntimeException ignored) {}
        }
        return resp == null ? "" : resp.trim();
    }
}
