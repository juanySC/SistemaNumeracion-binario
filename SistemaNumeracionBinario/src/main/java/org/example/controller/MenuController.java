package org.example.controller;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import org.example.operaacionesAritmeticas.OperacionAritmetica;
import org.example.reloj.ConversorBase;
import org.example.reloj.MostrarReloj;

import java.io.IOException;
import java.util.concurrent.*;

public class MenuController {
    private final MostrarReloj reloj;
    private final ConversorBase conversor;
    private final OperacionAritmetica calculadora;

    // pantalla
    private Screen screen;
    private TextGraphics tg;

    // layout
    private static final int RELOJ_ROW = 1;
    private static final int MENU_START_ROW = 3;
    private static final int SCREEN_WIDTH = 100;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> updater;

    //constructor
    public MenuController(MostrarReloj reloj, ConversorBase conversor, OperacionAritmetica calculadora) {
        this.reloj = reloj;
        this.conversor = conversor;
        this.calculadora = calculadora;
    }

   /**drawClock ayuda a visualizar el cambio del reloj en timepo real, o sea que lo
    * renderiza**/
    private void drawClock() {
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

        // limpiar línea reloj
        tg.putString(1, RELOJ_ROW, " ".repeat(Math.max(1, SCREEN_WIDTH)));
        String compact = String.format("Hora: %s | Bin: %s | Oct: %s | Hex: %s",
                base10, base2, base8, base16);
        if (compact.length() > SCREEN_WIDTH) compact = compact.substring(0, SCREEN_WIDTH);
        tg.putString(1, RELOJ_ROW, compact);
    }

    /**drawMenu ayuda a dibujar el reloj**/
    private void drawMenu() throws IOException {
        // limpia área
        String blank = " ".repeat(Math.max(1, SCREEN_WIDTH));
        for (int r = 0; r < 30; r++) tg.putString(1, MENU_START_ROW + r, blank);

        tg.setForegroundColor(com.googlecode.lanterna.TextColor.ANSI.YELLOW);
        tg.putString(1, MENU_START_ROW, "¨========Eliga una de las opciones: ========");
        tg.setForegroundColor(com.googlecode.lanterna.TextColor.ANSI.WHITE);
        tg.putString(1, MENU_START_ROW + 1, "1. Tomar horas del reloj automaticamente");
        tg.putString(1, MENU_START_ROW + 2, "2. Ingresar las horas manual ");
        tg.putString(1, MENU_START_ROW + 3, "3. Salir");
        tg.putString(1, MENU_START_ROW + 4, "Eliga una opcion: ");
        tg.setForegroundColor(com.googlecode.lanterna.TextColor.ANSI.CYAN);
        tg.putString(1, MENU_START_ROW + 6, "Presione 1 / 2 / 3. Para respuestas escritas, escriba y pulse Enter.");
        tg.setForegroundColor(com.googlecode.lanterna.TextColor.ANSI.WHITE);

        screen.refresh();
    }

    /**start inicia la UI / menú ayuda a iniciar la aplicacion y el flujo ademas de
     * controlar las opciones**/
    public void start() throws IOException {
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        screen = factory.createScreen();
        screen.startScreen();
        screen.doResizeIfNecessary();
        tg = screen.newTextGraphics();

        // dibuja lo principal que se muestra al inicio
        drawClock();
        drawMenu();

        // actualiza cada segundo el reloj
        updater = scheduler.scheduleAtFixedRate(() -> {
            try {
                drawClock();
                screen.refresh();
            } catch (IOException ignored) {}
        }, 1, 1, TimeUnit.SECONDS);

        try {
            boolean running = true;
            while (running) {
                KeyStroke key = screen.pollInput(); // no bloqueante
                if (key == null) {
                    try { Thread.sleep(40); } catch (InterruptedException ignored) {}
                    continue;
                }

                switch (key.getKeyType()) {
                    case Character:
                        char c = key.getCharacter();
                        if (c == '1') {
                            // ejecutar opción 1
                            OptionAutomatic.execute(screen, tg, reloj, conversor, calculadora);
                            drawClock(); //actualizar reloj
                            drawMenu(); //actualizar menu
                        } else if (c == '2') {
                            // ejecutar opción 2
                            OptionManual.execute(screen, tg, reloj, conversor, calculadora);
                            drawClock();
                            drawMenu();
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
            // cleanup
            if (updater != null) updater.cancel(true);
            scheduler.shutdownNow();
            try { screen.stopScreen(); } catch (IOException ignored) {}
        }
    }
}
