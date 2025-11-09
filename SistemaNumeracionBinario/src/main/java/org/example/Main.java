package org.example;

import javax.swing.JOptionPane;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import org.example.operaacionesAritmeticas.OperacionAritmetica;
import org.example.reloj.ConversorBase;
import org.example.reloj.MostrarReloj;
import org.example.reloj.TiempoBinario;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    private static final MostrarReloj reloj = new MostrarReloj();
    private static final ConversorBase conversor = new ConversorBase();
    private static final OperacionAritmetica calculadora = new OperacionAritmetica();

    // configuración de pantalla y son final porque no se piensan mover de lugar
    private static final int RELOJ_ROW = 1;
    private static final int MENU_START_ROW = 3;
    private static final int SCREEN_WIDTH = 100;

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // ------------------ helpers de cálculo, o sea ayuda con los calculos de los tiempos
    private static int binarioATotalSegundos(TiempoBinario t) {
        int h = Integer.parseInt(t.hora, 2);
        int m = Integer.parseInt(t.minuto, 2);
        int s = Integer.parseInt(t.segundo, 2);
        return h * 3600 + m * 60 + s;
    }

    private static TiempoBinario segundosATiempoBinario(int totalSegundos) {
        totalSegundos = ((totalSegundos % 86400) + 86400) % 86400;
        int h = totalSegundos / 3600;
        int m = (totalSegundos % 3600) / 60;
        int s = totalSegundos % 60;
        String hBin = conversor.decimalABase(h, 2);
        String mBin = conversor.decimalABase(m, 2);
        String sBin = conversor.decimalABase(s, 2);
        return new TiempoBinario(hBin, mBin, sBin);
    }

    // ------------------ manipulacion pantalla ------------------
    private static void clearArea(Screen screen, TextGraphics tg, int startRow, int height) {
        String blank = " ".repeat(Math.max(1, SCREEN_WIDTH));
        for (int r = 0; r < height; r++) {
            tg.putString(1, startRow + r, blank);
        }
        try { screen.refresh(); } catch (IOException ignored) {}
    }

    private static void drawClock(TextGraphics tg) {
        int[] h = reloj.getHoraActual();
        String base10 = String.format("%02d:%02d:%02d", h[0], h[1], h[2]);
        String base2  = String.format("%s:%s:%s",
                conversor.decimalABase(h[0], 2),
                conversor.decimalABase(h[1], 2),
                conversor.decimalABase(h[2], 2));
        String base8  = String.format("%s:%s:%s",
                conversor.decimalABase(h[0], 8),
                conversor.decimalABase(h[1], 8),
                conversor.decimalABase(h[2], 8));
        String base16 = String.format("%s:%s:%s",
                conversor.decimalABase(h[0], 16),
                conversor.decimalABase(h[1], 16),
                conversor.decimalABase(h[2], 16));

        tg.putString(1, RELOJ_ROW, " ".repeat(Math.max(1, SCREEN_WIDTH)));
        String compact = String.format("Hora: %s | Bin: %s | Oct: %s | Hex: %s",
                base10, base2, base8, base16);
        if (compact.length() > SCREEN_WIDTH) compact = compact.substring(0, SCREEN_WIDTH);
        tg.putString(1, RELOJ_ROW, compact);
    }

    private static void drawMenu(Screen screen, TextGraphics tg) {
        // limpiar área del menu + zona de resultados (30 líneas)
        clearArea(screen, tg, MENU_START_ROW, 30);

        tg.setForegroundColor(TextColor.ANSI.YELLOW);
        tg.putString(1, MENU_START_ROW, "¨========Eliga una de las opciones: ========");
        tg.setForegroundColor(TextColor.ANSI.WHITE);
        tg.putString(1, MENU_START_ROW + 1, "1. Tomar horas del reloj automaticamente");
        tg.putString(1, MENU_START_ROW + 2, "2. Ingresar las horas manual ");
        tg.putString(1, MENU_START_ROW + 3, "3. Salir");
        tg.putString(1, MENU_START_ROW + 4, "Eliga una opcion: ");
        tg.setForegroundColor(TextColor.ANSI.CYAN);
        tg.putString(1, MENU_START_ROW + 6, "Presione 1 / 2 / 3. Para respuestas escritas, escriba y pulse Enter.");
        tg.setForegroundColor(TextColor.ANSI.WHITE);

        try { screen.refresh(); } catch (IOException ignored) {}
    }

    // escribe varias líneas empezando en startRow (limpia antes)
    private static void writeLines(Screen screen, TextGraphics tg, int startRow, List<String> lines) {
        for (int r = 0; r < Math.max(12, lines.size() + 2); r++) {
            tg.putString(1, startRow + r, " ".repeat(Math.max(1, SCREEN_WIDTH)));
        }
        for (int i = 0; i < lines.size(); i++) {
            tg.putString(1, startRow + i, lines.get(i));
        }
        try { screen.refresh(); } catch (IOException ignored) {}
    }

    // ------------------ lecturas robustas ------------------
    // readLine no-bloqueante con poll + timeout helper
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
                    // ignorar
            }
        }
        return null; // timeout
    }

    // leer un solo caracter con timeout
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

    // fallback modal (solo si lanterna no responde). Si se usa, ECHOA la respuesta en la pantalla para visualización.
    private static String fallbackModalAndEcho(Screen screen, TextGraphics tg, int echoRow, String message, String title) {
        String resp = JOptionPane.showInputDialog(null, message, title, JOptionPane.QUESTION_MESSAGE);
        if (resp != null) {
            writeLines(screen, tg, echoRow, List.of("Entrada (modal): " + resp));
        } else {
            writeLines(screen, tg, echoRow, List.of("Entrada cancelada (modal)."));
        }
        return resp == null ? "" : resp.trim();
    }

    // ------------------ Lógica Opción 1 (automático) ------------------
    private static void opcion1_horasAutomatico(Screen screen, TextGraphics tg) throws IOException {
        int headerRow = MENU_START_ROW + 6;
        writeLines(screen, tg, headerRow, List.of("-------- HORAS AUTOMÁTICAS -----------"));

        int cantidadRow = headerRow + 2;
        tg.putString(1, cantidadRow, "¿Cuántos tiempos deseas sumar? (2 a 6): ");
        screen.refresh();

        // Preferimos Lanterna: timeout amplio (30s)
        String cantidadStr = readLineWithTimeout(screen, tg, cantidadRow, 38, 30);

        // si no llega, intento single char corto
        if (cantidadStr == null || cantidadStr.trim().isEmpty()) {
            String single = readSingleCharWithTimeout(screen, tg, cantidadRow, 38, 5);
            if (single != null && !single.trim().isEmpty()) cantidadStr = single;
        }

        // fallback modal solo si siguió vacío; ECHOA la respuesta al regresar
        if (cantidadStr == null || cantidadStr.trim().isEmpty()) {
            cantidadStr = fallbackModalAndEcho(screen, tg, cantidadRow + 2, "¿Cuántos tiempos deseas sumar? (2 a 6):", "Entrada cantidad");
            if (cantidadStr.isEmpty()) {
                writeLines(screen, tg, cantidadRow + 4, List.of("Operación cancelada."));
                return;
            }
        }

        writeLines(screen, tg, cantidadRow + 2, List.of("DEBUG: cantidad recibida = " + cantidadStr));

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr);
        } catch (Exception e) {
            writeLines(screen, tg, cantidadRow + 4, List.of("Cantidad inválida. Debe ser un número."));
            return;
        }
        if (cantidad < 2 || cantidad > 6) {
            writeLines(screen, tg, cantidadRow + 4, List.of("Cantidad inválida. Debe estar entre 2 y 6."));
            return;
        }

        // Capturar automáticamente los tiempos
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
            opcion = fallbackModalAndEcho(screen, tg, promptRow + 2, "¿Desea realizar una Suma (S) o una Resta (R)? (S/R)", "Entrada S/R");
            if (opcion.isEmpty()) { writeLines(screen, tg, promptRow + 4, List.of("Operación cancelada.")); return; }
        }

        opcion = opcion.trim().toUpperCase();
        writeLines(screen, tg, promptRow + 2, List.of("DEBUG: opcion recibida = " + opcion));

        int resultadoSegundos = binarioATotalSegundos(tiempos[0]);
        for (int i = 1; i < cantidad; i++) {
            int currentSegundos = binarioATotalSegundos(tiempos[i]);
            if ("S".equals(opcion)) resultadoSegundos += currentSegundos;
            else if ("R".equals(opcion)) resultadoSegundos -= currentSegundos;
            else {
                writeLines(screen, tg, promptRow + 4, List.of("Opción no válida. Ingresa S o R."));
                return;
            }
        }

        TiempoBinario resultado = segundosATiempoBinario(resultadoSegundos);
        int hDec = Integer.parseInt(resultado.hora, 2);
        int mDec = Integer.parseInt(resultado.minuto, 2);
        int sDec = Integer.parseInt(resultado.segundo, 2);

        String octHora = conversor.decimalABase(hDec, 8);
        String octMin = conversor.decimalABase(mDec, 8);
        String octSeg = conversor.decimalABase(sDec, 8);
        String hexHora = conversor.decimalABase(hDec, 16);
        String hexMin = conversor.decimalABase(mDec, 16);
        String hexSeg = conversor.decimalABase(sDec, 16);

        List<String> out = new ArrayList<>();
        out.add("====================================");
        out.add("Resultado final:");
        out.add(String.format("Binario: %s:%s:%s", resultado.hora, resultado.minuto, resultado.segundo));
        out.add(String.format("Decimal: %02d:%02d:%02d", hDec, mDec, sDec));
        out.add(String.format("Octal : %s:%s:%s", octHora, octMin, octSeg));
        out.add(String.format("Hex   : %s:%s:%s", hexHora, hexMin, hexSeg));
        out.add("====================================");
        writeLines(screen, tg, promptRow + 4, out);

        // mostrar en todas las bases el reusltado de la suma o resta
        StringBuilder popup = new StringBuilder();
        popup.append("Resultado final:\n");
        popup.append(String.format("Binario: %s:%s:%s\n",  resultado.hora, resultado.minuto, resultado.segundo));
        popup.append(String.format("Decimal: %02d:%02d:%02d\n", hDec, mDec, sDec));
        popup.append(String.format("Octal  : %s:%s:%s\n",   octHora,   octMin,   octSeg));
        popup.append(String.format("Hex    : %s:%s:%s\n",   hexHora,  hexMin,  hexSeg));

        JOptionPane.showMessageDialog(null, popup.toString(), "Resultado", JOptionPane.INFORMATION_MESSAGE);

        // Preguntar si desea salir (preferencia Lanterna)
        int askRow = promptRow + 10;
        tg.putString(1, askRow, "Presiona Y para salir o N para volver al menú: ");
        screen.refresh();

        String resp = readSingleCharWithTimeout(screen, tg, askRow, 42, 8);
        if (resp == null || resp.trim().isEmpty()) {
            // fallback modal confirm
            int sel = JOptionPane.showConfirmDialog(null, "¿Deseas salir del programa? (Sí = salir, No = volver al menú)", "Salir?", JOptionPane.YES_NO_OPTION);
            if (sel == JOptionPane.YES_OPTION) {
                try { screen.stopScreen(); } catch (IOException ignored) {}
                System.exit(0);
            } else {
                // limpiar y volver al menu
                clearArea(screen, tg, MENU_START_ROW, 30);
                drawMenu(screen, tg);
                return;
            }
        } else {
            resp = resp.trim().toUpperCase();
            writeLines(screen, tg, askRow + 1, List.of("Elección: " + resp));
            if ("Y".equals(resp) || "S".equals(resp)) { // S para si (español) o Y para yes
                try { screen.stopScreen(); } catch (IOException ignored) {}
                System.exit(0);
            } else {
                clearArea(screen, tg, MENU_START_ROW, 30);
                drawMenu(screen, tg);
                return;
            }
        }
    }

    // ------------------ Opcion 2 (manual) - MODIFICADA: ahora ingresa DECIMALES y devuelve en bases 2,8,10,16
    private static void opcion2_horasManuales(Screen screen, TextGraphics tg) throws IOException {
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

        // Tomar la hora actual como BASE
        int[] horaActual = reloj.getHoraActual();
        int baseHora = horaActual[0], baseMin = horaActual[1], baseSeg = horaActual[2];
        int totalBaseSeg = baseHora * 3600 + baseMin * 60 + baseSeg;

        // Mostrar la hora base
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

        // Pedimos cada componente EN DECIMAL (validamos) y convertimos a binario internamente
        for (int i = 0; i < cantidad; i++) {
            // HORA (0-23) para el delta
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

            // MINUTO (0-59)
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

            // SEGUNDO (0-59)
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

            // Convertir a binario usando conversor y almacenar en TiempoBinario
            String hBin = conversor.decimalABase(hDec, 2);
            String mBin = conversor.decimalABase(mDec, 2);
            String sBin = conversor.decimalABase(sDec, 2);

            tiempos[i] = new TiempoBinario(hBin, mBin, sBin);
            deltasSeg[i] = hDec * 3600 + mDec * 60 + sDec;

            // Mostrar lo ingresado (decimal + binario) para confirmación
            writeLines(screen, tg, rowCursor, List.of(
                    String.format("Ingresado tiempo %d -> %02d:%02d:%02d (binario: %s:%s:%s)", i+1, hDec, mDec, sDec, hBin, mBin, sBin)
            ));

            rowCursor += 5;
        }

        //sale una pantalla emergente que preunta que proceso u operacion deseo realizar
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

        // Aplicar todos los deltas respecto de la hora actual (base)
        long resultadoSegundos = totalBaseSeg;
        for (int i = 0; i < cantidad; i++) {
            if ("S".equals(opcion)) resultadoSegundos += deltasSeg[i];
            else if ("R".equals(opcion)) resultadoSegundos -= deltasSeg[i];
            else { writeLines(screen, tg, promptRow + 4, List.of("Opción no válida.")); return; }
        }

        TiempoBinario resultado = segundosATiempoBinario((int) resultadoSegundos);
        int hDec = Integer.parseInt(resultado.hora, 2);
        int mDec = Integer.parseInt(resultado.minuto, 2);
        int sDec = Integer.parseInt(resultado.segundo, 2);

        // Mostrar resultado en las 4 bases
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

        int askRow = promptRow + 10;
        tg.putString(1, askRow, "Presiona Y para salir o N para volver al menú: ");
        screen.refresh();

        String resp = readSingleCharWithTimeout(screen, tg, askRow, 42, 8);
        if (resp == null || resp.trim().isEmpty()) {
            int sel = JOptionPane.showConfirmDialog(null, "¿Deseas salir del programa? (Sí = salir, No = volver al menú)", "Salir?", JOptionPane.YES_NO_OPTION);
            if (sel == JOptionPane.YES_OPTION) {
                try { screen.stopScreen(); } catch (IOException ignored) {}
                System.exit(0);
            } else {
                clearArea(screen, tg, MENU_START_ROW, 30);
                drawMenu(screen, tg);
                return;
            }
        } else {
            resp = resp.trim().toUpperCase();
            writeLines(screen, tg, askRow + 1, List.of("Elección: " + resp));
            if ("Y".equals(resp) || "S".equals(resp)) {
                try { screen.stopScreen(); } catch (IOException ignored) {}
                System.exit(0);
            } else {
                clearArea(screen, tg, MENU_START_ROW, 30);
                drawMenu(screen, tg);
                return;
            }
        }
    }

    // ------------------ main ------------------
    public static void main(String[] args) throws IOException {
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        Screen screen = factory.createScreen();
        screen.startScreen();
        screen.doResizeIfNecessary();

        TextGraphics tg = screen.newTextGraphics();

        // dibujar inicial
        drawClock(tg);
        drawMenu(screen, tg);
        screen.refresh();

        // updater del reloj
        ScheduledFuture<?> updater = scheduler.scheduleAtFixedRate(() -> {
            try {
                drawClock(tg);
                screen.refresh();
            } catch (IOException ignored) {}
        }, 1, 1, TimeUnit.SECONDS);

        try {
            boolean running = true;
            drawMenu(screen, tg);
            screen.refresh();

            while (running) {
                KeyStroke key = screen.pollInput(); // no bloqueante
                if (key == null) {
                    try { Thread.sleep(50); } catch (InterruptedException ignored) {}
                    continue;
                }
                switch (key.getKeyType()) {
                    case Character:
                        char c = key.getCharacter();
                        if (c == '1') {
                            opcion1_horasAutomatico(screen, tg);
                            drawMenu(screen, tg);
                        } else if (c == '2') {
                            opcion2_horasManuales(screen, tg);
                            drawMenu(screen, tg);
                        } else if (c == '3') {
                            running = false;
                        }
                        break;
                    case EOF:
                        running = false;
                        break;
                    default:
                }
                screen.refresh();
            }
        } finally {
            updater.cancel(true);
            scheduler.shutdownNow();
            screen.stopScreen();
        }
    }
}
