package org.example.controller;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import org.example.operaacionesAritmeticas.OperacionAritmetica;
import org.example.reloj.ConversorBase;
import org.example.reloj.MostrarReloj;
import org.example.modelos.TiempoBinario;

import javax.swing.JOptionPane;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OptionAutomatic {
    private static final int SCREEN_WIDTH = 100;
    private static final int MENU_START_ROW = 3;

    /**writeLines esto ayuda a escribir multiples lineas en la pantalla lo
     * que significa que se limpia el area y se escribe de nuevo **/
    private static void writeLines(Screen screen, TextGraphics tg, int startRow, List<String> lines) {
      // limpia area en un perimetro de 12 filas
        for (int r = 0; r < Math.max(12, lines.size() + 2); r++) {
            tg.putString(1, startRow + r, " ".repeat(Math.max(1, SCREEN_WIDTH)));
        }
        //en este otro ciclo ayuda a escribir las lineas que se van a mostrar
        for (int i = 0; i < lines.size(); i++) {
            tg.putString(1, startRow + i, lines.get(i));
        }
        try { screen.refresh(); } catch (IOException ignored) {}
    }


    /**readLineWithTimeout lee una linea con timeout o sea que si no se ingresa nada en el tiempo
     * dado retorna null, o cadena vacia si se presiona escape es el tiempo de espera en segundos**/
    private static String readLineWithTimeout(Screen screen, TextGraphics tg, int promptRow, int promptCol, int timeoutSeconds) throws IOException {
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
        return null;
    }

    /** ayuda a leer un solo caracter con timeout o sea que si no se ingresa nada en el tiempo dado retorna null
    además si se presiona enter se ignora el input es el tiempo de espera en segundos
     @param screen  ayuda en la pantalla a realizar la operacion
     @param tg ayuda a escribir en la pantalla
     @param promptRow ayuda a saber la fila donde se va a escribir
     @param promptCol  ayuda a saber la columna donde se escribe
     @param timeoutSeconds que ayuda saber el tiempo de espera en segundo**/
    private static String readSingleCharWithTimeout(Screen screen, TextGraphics tg, int promptRow, int promptCol, int timeoutSeconds) throws IOException {
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

    /**fallbackModal muestra un JOptionPane para entrada si lanterna falla o sea que si no se recibe input en el tiempo dado
     además muestra el resultado en la pantalla lanterna**/
    private static String fallbackModal(Screen screen, TextGraphics tg, int echoRow, String message, String title) {
        String resp = JOptionPane.showInputDialog(null, message, title, JOptionPane.QUESTION_MESSAGE);
        if (resp != null) {
            writeLines(screen, tg, echoRow, List.of("Entrada (modal): " + resp));
        } else {
            writeLines(screen, tg, echoRow, List.of("Entrada cancelada (modal)."));
        }
        return resp == null ? "" : resp.trim();
    }

    // convertir TiempoBinario -> total segundos
    private static int binarioATotalSegundos(TiempoBinario t) {
        int h = Integer.parseInt(t.hora, 2);
        int m = Integer.parseInt(t.minuto, 2);
        int s = Integer.parseInt(t.segundo, 2);
        return h * 3600 + m * 60 + s;
    }

    // método principal para ejecutar la opción automática
    public static void execute(Screen screen, TextGraphics tg, MostrarReloj reloj, ConversorBase conversor, OperacionAritmetica calculadora) throws IOException {
        int headerRow = MENU_START_ROW + 6;
        writeLines(screen, tg, headerRow, List.of("-------- HORAS AUTOMÁTICAS -----------"));

        int cantidadRow = headerRow + 2;
        tg.putString(1, cantidadRow, "¿Cuántos tiempos deseas sumar? (2 a 6): ");
        screen.refresh();

        String cantidadStr = readLineWithTimeout(screen, tg, cantidadRow, 38, 30);
        if (cantidadStr == null || cantidadStr.trim().isEmpty()) {
            String single = readSingleCharWithTimeout(screen, tg, cantidadRow, 38, 5);
            if (single != null && !single.trim().isEmpty()) cantidadStr = single;
        }
        if (cantidadStr == null || cantidadStr.trim().isEmpty()) {
            cantidadStr = fallbackModal(screen, tg, cantidadRow + 2, "¿Cuántos tiempos deseas sumar? (2 a 6):", "Entrada cantidad");
            if (cantidadStr.isEmpty()) {
                writeLines(screen, tg, cantidadRow + 4, List.of("Operación cancelada."));
                return;
            }
        }

        int cantidad;
        try { cantidad = Integer.parseInt(cantidadStr); }
        catch (Exception e) { writeLines(screen, tg, cantidadRow + 4, List.of("Cantidad inválida.")); return; }
        if (cantidad < 2 || cantidad > 6) { writeLines(screen, tg, cantidadRow + 4, List.of("Cantidad inválida. Debe estar entre 2 y 6.")); return; }

        // Capturar automáticamente los tiempos actuales (cada ~1s)
        TiempoBinario[] tiempos = new TiempoBinario[cantidad];
        int listStart = cantidadRow + 6;
        for (int i = 0; i < cantidad; i++) {
            int[] hms = reloj.getHoraActual();
            String hBin = conversor.decimalABase(hms[0], 2);
            String mBin = conversor.decimalABase(hms[1], 2);
            String sBin = conversor.decimalABase(hms[2], 2);
            tiempos[i] = new TiempoBinario(hBin, mBin, sBin);

            writeLines(screen, tg, listStart + i, List.of(
                    String.format("Tiempo %d -> %02d:%02d:%02d (binario: %s:%s:%s)", i+1, hms[0], hms[1], hms[2], hBin, mBin, sBin)
            ));
            try { Thread.sleep(900); } catch (InterruptedException ignored) {}
        }

        int promptRow = listStart + cantidad + 1;
        tg.putString(1, promptRow, "¿Desea realizar una Suma (S) o una Resta (R)? ");
        screen.refresh();

        String opcion = readLineWithTimeout(screen, tg, promptRow, 44, 25);
        if (opcion == null || opcion.trim().isEmpty()) {
            String single = readSingleCharWithTimeout(screen, tg, promptRow, 44, 5);
            if (single != null && !single.trim().isEmpty()) opcion = single;
        }
        if (opcion == null || opcion.trim().isEmpty()) {
            opcion = fallbackModal(screen, tg, promptRow + 2, "¿Desea realizar una Suma (S) o una Resta (R)? (S/R)", "Entrada S/R");
            if (opcion.isEmpty()) { writeLines(screen, tg, promptRow + 4, List.of("Operación cancelada.")); return; }
        }

        opcion = opcion.trim().toUpperCase();
        writeLines(screen, tg, promptRow + 2, List.of("DEBUG: opcion recibida = " + opcion));

        int resultadoSegundos = binarioATotalSegundos(tiempos[0]);
        for (int i = 1; i < cantidad; i++) {
            int currentSegundos = binarioATotalSegundos(tiempos[i]);
            if ("S".equals(opcion)) resultadoSegundos += currentSegundos;
            else if ("R".equals(opcion)) resultadoSegundos -= currentSegundos;
            else { writeLines(screen, tg, promptRow + 4, List.of("Opción no válida. Ingresa S o R.")); return; }
        }

        // convertir a HMS y mostrar en todas las bases
        resultadoSegundos = ((resultadoSegundos % 86400) + 86400) % 86400;
        int h = resultadoSegundos / 3600;
        int m = (resultadoSegundos % 3600) / 60;
        int s = resultadoSegundos % 60;
        String hBin = conversor.decimalABase(h, 2);
        String mBin = conversor.decimalABase(m, 2);
        String sBin = conversor.decimalABase(s, 2);
        String hOct = conversor.decimalABase(h, 8);
        String mOct = conversor.decimalABase(m, 8);
        String sOct = conversor.decimalABase(s, 8);
        String hHex = conversor.decimalABase(h, 16);
        String mHex = conversor.decimalABase(m, 16);
        String sHex = conversor.decimalABase(s, 16);

        List<String> out = new ArrayList<>();
        out.add("====================================");
        out.add("Resultado final:");
        out.add(String.format("Binario: %s:%s:%s", hBin, mBin, sBin));
        out.add(String.format("Decimal: %02d:%02d:%02d", h, m, s));
        out.add(String.format("Octal  : %s:%s:%s", hOct, mOct, sOct));
        out.add(String.format("Hex    : %s:%s:%s", hHex, mHex, sHex));
        out.add("====================================");
        writeLines(screen, tg, promptRow + 4, out);

        StringBuilder popup = new StringBuilder();
        popup.append("Resultado final:\n");
        popup.append(String.format("Binario: %s:%s:%s\n", hBin, mBin, sBin));
        popup.append(String.format("Decimal: %02d:%02d:%02d\n", h, m, s));
        popup.append(String.format("Octal  : %s:%s:%s\n", hOct, mOct, sOct));
        popup.append(String.format("Hex    : %s:%s:%s\n", hHex, mHex, sHex));
        JOptionPane.showMessageDialog(null, popup.toString(), "Resultado", JOptionPane.INFORMATION_MESSAGE);

        // volver al menú (controlador principal redibujará)
    }
}
