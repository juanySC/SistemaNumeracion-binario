package org.example.logic;

import org.example.modelos.TiempoBinario;
import org.example.reloj.ConversorBase;

public class TimeCalculator {
    private final ConversorBase conversor;

    //constructor
    public TimeCalculator(ConversorBase conversor) {
        this.conversor = conversor;
    }

    // es la conversion de binario a segundos
    public int binarioATotalSegundos(TiempoBinario t) {
        int h = Integer.parseInt(t.hora, 2);
        int m = Integer.parseInt(t.minuto, 2);
        int s = Integer.parseInt(t.segundo, 2);
        return h * 3600 + m * 60 + s;
    }

    public TiempoBinario segundosATiempoBinario(int totalSegundos) {
        totalSegundos = ((totalSegundos % 86400) + 86400) % 86400;
        int h = totalSegundos / 3600;
        int m = (totalSegundos % 3600) / 60;
        int s = totalSegundos % 60;
        String hBin = conversor.decimalABase(h, 2);
        String mBin = conversor.decimalABase(m, 2);
        String sBin = conversor.decimalABase(s, 2);
        return new TiempoBinario(hBin, mBin, sBin);
    }
}
