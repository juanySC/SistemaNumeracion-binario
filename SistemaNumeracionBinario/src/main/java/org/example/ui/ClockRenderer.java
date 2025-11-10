package org.example.ui;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import org.example.reloj.MostrarReloj;
import org.example.reloj.ConversorBase;

import java.io.IOException;
import java.util.concurrent.*;

public class ClockRenderer {
    private final MostrarReloj reloj;
    private final ConversorBase conversor;
    private final TextGraphics tg;
    private final Screen screen;
    private final int RELOJ_ROW;
    private final int SCREEN_WIDTH;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> updater;

    //constructor
    public ClockRenderer(MostrarReloj reloj, ConversorBase conversor, Screen screen, TextGraphics tg, int relojRow, int screenWidth) {
        this.reloj = reloj;
        this.conversor = conversor;
        this.screen = screen;
        this.tg = tg;
        this.RELOJ_ROW = relojRow;
        this.SCREEN_WIDTH = screenWidth;
    }

    // dibuja el reloj en diferentes bases numéricas
    public void drawClock() {
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

    // inicia actualizador periódico
    public void startAutoUpdate() {
        updater = scheduler.scheduleAtFixedRate(() -> {
            try {
                drawClock();
                screen.refresh();
            } catch (IOException ignored) {}
        }, 1, 1, TimeUnit.SECONDS);
    }

    // detiene el actualizador periódico
    public void stopAutoUpdate() {
        if (updater != null) updater.cancel(true);
        scheduler.shutdownNow();
    }
}
