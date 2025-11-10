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

public class OptionManual {
    private static final int SCREEN_WIDTH = 100;
    private static final int MENU_START_ROW = 3;

    // Limpia un área específica de la pantalla
    private static void clearArea(Screen screen, TextGraphics tg, int startRow, int height) {
        String blank = " ".repeat(Math.max(1, SCREEN_WIDTH));
        for (int r = 0; r < height; r++) {
            tg.putString(1, startRow + r, blank);
        }
        try { screen.refresh(); } catch (IOException ignored) {}
    }

    // Escribe múltiples líneas en la pantalla comenzando desde una fila específica
    private static void writeLines(Screen screen, TextGraphics tg, int startRow, List<String> lines) {
        for (int r = 0; r < Math.max(12, lines.size() + 2); r++) {
            tg.putString(1, startRow + r, " ".repeat(Math.max(1, SCREEN_WIDTH)));
        }
        for (int i = 0; i < lines.size(); i++) {
            tg.putString(1, startRow + i, lines.get(i));
        }
        try { screen.refresh(); } catch (IOException ignored) {}
    }

    // Lee una línea de entrada con un tiempo de espera
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

    // Lee un solo carácter de entrada con un tiempo de espera
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

    // Muestra un cuadro de diálogo modal para entrada y la refleja en la pantalla
    private static String fallbackModalAndEcho(Screen screen, TextGraphics tg, int echoRow, String message, String title) {
        String resp = JOptionPane.showInputDialog(null, message, title, JOptionPane.QUESTION_MESSAGE);
        if (resp != null) {
            writeLines(screen, tg, echoRow, List.of("Entrada (modal): " + resp));
        } else {
            writeLines(screen, tg, echoRow, List.of("Entrada cancelada (modal)."));
        }
        return resp == null ? "" : resp.trim();
    }

    private static TiempoBinario segundosATiempoBinario(ConversorBase conversor, int totalSegundos) {
        totalSegundos = ((totalSegundos % 86400) + 86400) % 86400;
        int h = totalSegundos / 3600;
        int m = (totalSegundos % 3600) / 60;
        int s = totalSegundos % 60;
        String hBin = conversor.decimalABase(h, 2);
        String mBin = conversor.decimalABase(m, 2);
        String sBin = conversor.decimalABase(s, 2);
        return new TiempoBinario(hBin, mBin, sBin);
    }

    // Método principal para ejecutar la opción de ingreso manual
    public static void execute(Screen screen, TextGraphics tg, MostrarReloj reloj, ConversorBase conversor, OperacionAritmetica calculadora) throws IOException {
        int headerRow = MENU_START_ROW + 6;
        writeLines(screen, tg, headerRow, List.of("--- OPCIÓN 2: INGRESAR TIEMPOS MANUALMENTE (en DECIMAL) ---"));

        int cantidadRow = headerRow + 2;
        tg.putString(1, cantidadRow, "¿Cuántos tiempos deseas sumar/restar? (1 a 6): ");
        screen.refresh();

        String cantidadStr = readLineWithTimeout(screen, tg, cantidadRow, 46, 30);
        if (cantidadStr == null || cantidadStr.trim().isEmpty()) {
            String single = readSingleCharWithTimeout(screen, tg, cantidadRow, 46, 5);
            if (single != null) cantidadStr = single;
        }
        if (cantidadStr == null || cantidadStr.trim().isEmpty()) {
            cantidadStr = fallbackModalAndEcho(screen, tg, cantidadRow + 2, "¿Cuántos tiempos deseas sumar/restar? (1 a 6):", "Entrada cantidad");
            if (cantidadStr.isEmpty()) { writeLines(screen, tg, cantidadRow + 4, List.of("Operación cancelada.")); return; }
        }

        int cantidad;
        try { cantidad = Integer.parseInt(cantidadStr); }
        catch (Exception e) { writeLines(screen, tg, cantidadRow + 4, List.of("Cantidad inválida.")); return; }
        if (cantidad < 1 || cantidad > 6) { writeLines(screen, tg, cantidadRow + 4, List.of("Cantidad inválida. Debe estar entre 1 y 6.")); return; }

        // Hora actual base
        int[] horaActual = reloj.getHoraActual();
        int baseHora = horaActual[0], baseMin = horaActual[1], baseSeg = horaActual[2];
        int totalBaseSeg = baseHora * 3600 + baseMin * 60 + baseSeg;

        // mostrar hora base en pantalla
        String baseBinHora = conversor.decimalABase(baseHora, 2);
        String baseBinMin  = conversor.decimalABase(baseMin, 2);
        String baseBinSeg  = conversor.decimalABase(baseSeg, 2);
        String baseOctHora = conversor.decimalABase(baseHora, 8);
        String baseOctMin  = conversor.decimalABase(baseMin, 8);
        String baseOctSeg  = conversor.decimalABase(baseSeg, 8);
        String baseHexHora = conversor.decimalABase(baseHora, 16);
        String baseHexMin  = conversor.decimalABase(baseMin, 16);
        String baseHexSeg  = conversor.decimalABase(baseSeg, 16);

        writeLines(screen, tg, cantidadRow + 2, List.of(
                String.format("Hora actual (Base 10): %02d:%02d:%02d", baseHora, baseMin, baseSeg),
                String.format("Binario: %s:%s:%s", baseBinHora, baseBinMin, baseBinSeg),
                String.format("Octal:   %s:%s:%s", baseOctHora, baseOctMin, baseOctSeg),
                String.format("Hex:     %s:%s:%s", baseHexHora, baseHexMin, baseHexSeg)
        ));

        TiempoBinario[] tiempos = new TiempoBinario[cantidad];
        int[] deltasSeg = new int[cantidad];
        int rowCursor = cantidadRow + 8;

        for (int i = 0; i < cantidad; i++) {
            writeLines(screen, tg, rowCursor, List.of(String.format("Ingrese las HORAS a sumar/restar para tiempo %d (decimal, >=0): ", i+1)));
            String hStr = readLineWithTimeout(screen, tg, rowCursor, 60, 30);
            if (hStr == null || hStr.trim().isEmpty()) {
                hStr = fallbackModalAndEcho(screen, tg, rowCursor, "Ingrese las HORAS (decimal, >=0) para tiempo " + (i+1) + " :", "Hora decimal");
                if (hStr.isEmpty()) { writeLines(screen, tg, rowCursor + 2, List.of("Operación cancelada.")); return; }
            }
            int hDec;
            try { hDec = Integer.parseInt(hStr.trim()); }
            catch (Exception e) { writeLines(screen, tg, rowCursor + 2, List.of("Hora inválida. Debe ser entero >=0.")); return; }
            if (hDec < 0) { writeLines(screen, tg, rowCursor + 2, List.of("Hora debe ser >= 0.")); return; }

            writeLines(screen, tg, rowCursor + 1, List.of(String.format("Ingrese los MINUTOS a sumar/restar para tiempo %d (0-59): ", i+1)));
            String mStr = readLineWithTimeout(screen, tg, rowCursor + 1, 64, 30);
            if (mStr == null || mStr.trim().isEmpty()) {
                mStr = fallbackModalAndEcho(screen, tg, rowCursor + 1, "Ingrese los MINUTOS (0-59) para tiempo " + (i+1) + " :", "Minuto decimal");
                if (mStr.isEmpty()) { writeLines(screen, tg, rowCursor + 3, List.of("Operación cancelada.")); return; }
            }
            int mDec;
            try { mDec = Integer.parseInt(mStr.trim()); }
            catch (Exception e) { writeLines(screen, tg, rowCursor + 3, List.of("Minuto inválido. Debe ser entero 0-59.")); return; }
            if (mDec < 0 || mDec > 59) { writeLines(screen, tg, rowCursor + 3, List.of("Minuto fuera de rango (0-59).")); return; }

            writeLines(screen, tg, rowCursor + 2, List.of(String.format("Ingrese los SEGUNDOS a sumar/restar para tiempo %d (0-59): ", i+1)));
            String sStr = readLineWithTimeout(screen, tg, rowCursor + 2, 66, 30);
            if (sStr == null || sStr.trim().isEmpty()) {
                sStr = fallbackModalAndEcho(screen, tg, rowCursor + 2, "Ingrese los SEGUNDOS (0-59) para tiempo " + (i+1) + " :", "Segundo decimal");
                if (sStr.isEmpty()) { writeLines(screen, tg, rowCursor + 4, List.of("Operación cancelada.")); return; }
            }
            int sDec;
            try { sDec = Integer.parseInt(sStr.trim()); }
            catch (Exception e) { writeLines(screen, tg, rowCursor + 4, List.of("Segundo inválido. Debe ser entero 0-59.")); return; }
            if (sDec < 0 || sDec > 59) { writeLines(screen, tg, rowCursor + 4, List.of("Segundo fuera de rango (0-59).")); return; }

            String hBin = conversor.decimalABase(hDec, 2);
            String mBin = conversor.decimalABase(mDec, 2);
            String sBin = conversor.decimalABase(sDec, 2);

            tiempos[i] = new TiempoBinario(hBin, mBin, sBin);
            deltasSeg[i] = hDec * 3600 + mDec * 60 + sDec;

            // Mostrar lo ingresado para cada tiempo (así se imprimen todos)
            writeLines(screen, tg, rowCursor, List.of(
                    String.format("Ingresado tiempo %d -> %02d:%02d:%02d (binario: %s:%s:%s)", i+1, hDec, mDec, sDec, hBin, mBin, sBin)
            ));

            rowCursor += 5;
        }

        // pedir suma/resta
        int promptRow = rowCursor;
        tg.putString(1, promptRow, "¿Desea realizar una Suma (S) o una Resta (R)? ");
        screen.refresh();

        String opcion = readLineWithTimeout(screen, tg, promptRow, 44, 25);
        if (opcion == null || opcion.trim().isEmpty()) {
            String single = readSingleCharWithTimeout(screen, tg, promptRow, 44, 5);
            if (single != null) opcion = single;
        }
        if (opcion == null || opcion.trim().isEmpty()) {
            opcion = fallbackModalAndEcho(screen, tg, promptRow + 2, "¿Desea realizar una Suma (S) o una Resta (R)? (S/R)", "Entrada S/R");
            if (opcion.isEmpty()) { writeLines(screen, tg, promptRow + 4, List.of("Operación cancelada.")); return; }
        }

        opcion = opcion.trim().toUpperCase();
        writeLines(screen, tg, promptRow + 2, List.of("DEBUG: opcion recibida = " + opcion));

        long resultadoSegundos = totalBaseSeg;
        for (int i = 0; i < cantidad; i++) {
            if ("S".equals(opcion)) resultadoSegundos += deltasSeg[i];
            else if ("R".equals(opcion)) resultadoSegundos -= deltasSeg[i];
            else { writeLines(screen, tg, promptRow + 4, List.of("Opción no válida.")); return; }
        }

        TiempoBinario resultado = segundosATiempoBinario(conversor, (int) resultadoSegundos);
        int hDec = Integer.parseInt(resultado.hora, 2);
        int mDec = Integer.parseInt(resultado.minuto, 2);
        int sDec = Integer.parseInt(resultado.segundo, 2);

        String binRes = String.format("%s:%s:%s", resultado.hora, resultado.minuto, resultado.segundo);
        String octHora = conversor.decimalABase(hDec, 8);
        String octMin = conversor.decimalABase(mDec, 8);
        String octSeg = conversor.decimalABase(sDec, 8);
        String hexHora = conversor.decimalABase(hDec, 16);
        String hexMin = conversor.decimalABase(mDec, 16);
        String hexSeg = conversor.decimalABase(sDec, 16);

        List<String> out = new ArrayList<>();
        out.add("====================================");
        out.add("Resultado final:");
        out.add(String.format("Binario: %s", binRes));
        out.add(String.format("Octal : %s:%s:%s", octHora, octMin, octSeg));
        out.add(String.format("Decimal: %02d:%02d:%02d", hDec, mDec, sDec));
        out.add(String.format("Hex   : %s:%s:%s", hexHora, hexMin, hexSeg));
        out.add("====================================");
        writeLines(screen, tg, promptRow + 4, out);

        StringBuilder popup = new StringBuilder();
        popup.append("Resultado final:\n");
        popup.append(String.format("Binario: %s\n", binRes));
        popup.append(String.format("Decimal: %02d:%02d:%02d\n", hDec, mDec, sDec));
        popup.append(String.format("Octal : %s:%s:%s\n", octHora, octMin, octSeg));
        popup.append(String.format("Hex   : %s:%s:%s\n", hexHora, hexMin, hexSeg));
        JOptionPane.showMessageDialog(null, popup.toString(), "Resultado", JOptionPane.INFORMATION_MESSAGE);

        // limpiar área de menú antes de regresar
        clearArea(screen, tg, MENU_START_ROW, 30);
    }
}
