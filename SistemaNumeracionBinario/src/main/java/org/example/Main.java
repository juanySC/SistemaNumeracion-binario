// java
package org.example;

import org.example.operaacionesAritmeticas.OperacionAritmetica;
import org.example.reloj.ConversorBase;
import org.example.reloj.MostrarReloj;
import org.example.reloj.RelojPantalla;

import java.util.Scanner;

public class Main {
    private static final MostrarReloj reloj = new MostrarReloj();
    private static final ConversorBase conversor = new ConversorBase();
    private static final OperacionAritmetica calculadora = new OperacionAritmetica();

    private static final Object printLock = new Object();

    private static String construirBloque(int hora, int minuto, int segundo) {
        String base10 = String.format("%02d:%02d:%02d", hora, minuto, segundo);
        String base2 = String.format("%s:%s:%s",
                conversor.decimalABase(hora, 2),
                conversor.decimalABase(minuto, 2),
                conversor.decimalABase(segundo, 2));
        String base8 = String.format("%s:%s:%s",
                conversor.decimalABase(hora, 8),
                conversor.decimalABase(minuto, 8),
                conversor.decimalABase(segundo, 8));
        String base16 = String.format("%s:%s:%s",
                conversor.decimalABase(hora, 16),
                conversor.decimalABase(minuto, 16),
                conversor.decimalABase(segundo, 16));

        return new StringBuilder()
                .append("___________________________________\n")
                .append("              RELOJ ACTUAL\n")
                .append("___________________________________\n")
                .append(String.format("Base 10: %s\n", base10))
                .append(String.format("Base  2: %s\n", base2))
                .append(String.format("Base  8: %s\n", base8))
                .append(String.format("Base 16: %s\n", base16))
                .append("====================================\n")
                .toString();
    }

    private static class ClockUpdater implements Runnable {
        private volatile boolean running = true;

        public void stop() { running = false; }

        @Override
        public void run() {
            while (running && !Thread.currentThread().isInterrupted()) {
                try {
                    int[] h = reloj.getHoraActual();
                    String horaFormateada = String.format("%02d:%02d:%02d", h[0], h[1], h[2]);
                    String binario = String.format("%s:%s:%s",
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

                    String display = "Hora: " + horaFormateada +
                            " | Binario: " + binario +
                            " | Octal: " + base8 +
                            " | Hex: " + base16;

                    synchronized (printLock) {
                        System.out.print("\r" + display); // sobrescribe la línea del reloj
                        System.out.flush();
                    }

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }


    // Mantener un único Scanner y leer fuera del lock
    public static void menu(Scanner sc) throws InterruptedException {
        synchronized (printLock) {
            System.out.println();
            System.out.println("¨========Eliga una de las opciones: ========");
            System.out.println("1. Tomar dos horas del reloj automaticamente");
            System.out.println("2. Ingresar las horas manual ");
            System.out.println("3. Salir");
            System.out.print("Eliga una opcion: ");
            System.out.flush();
        }

        String line = sc.nextLine().trim();
        int opcion;
        try {
            opcion = Integer.parseInt(line);
        } catch (NumberFormatException e) {
            synchronized (printLock) { System.out.println("Entrada no válida."); }
            return;
        }

        switch (opcion) {
            case 1:
                horasAutomatico(sc);
                break;
            case 2:
                horasManuales(sc);
                break;
            case 3:
                synchronized (printLock) { System.out.println("Saliendo del programa"); }
                System.exit(0);
                break;
            default:
                synchronized (printLock) { System.out.println("Opcion no encontrada"); }
        }
    }

    public static void horasAutomatico(Scanner scanner) throws InterruptedException {
        synchronized (printLock) { System.out.println("\n--------- Hora automatica -----------"); }
        int[] horaActual1 = reloj.getHoraActual();
        Thread.sleep(3000);
        int[] horaActual2 = reloj.getHoraActual();

        int hora1 = horaActual1[0], minuto1 = horaActual1[1], segundo1 = horaActual1[2];
        int hora2 = horaActual2[0], minuto2 = horaActual2[1], segundo2 = horaActual2[2];

        String binarioHora1 = conversor.decimalABase(hora1, 2);
        String binarioMinuto1 = conversor.decimalABase(minuto1,2);
        String binarioSegundo1 = conversor.decimalABase(segundo1,2);

        String binarioHora2 = conversor.decimalABase(hora2, 2);
        String binarioMinuto2 = conversor.decimalABase(minuto2,2);
        String binarioSegundo2 = conversor.decimalABase(segundo2,2);

        synchronized (printLock) {
            System.out.printf("Hora 1 -> %02d:%02d:%02d    (%s : %s : %s)\n",
                    hora1, minuto1, segundo1, binarioHora1, binarioMinuto1, binarioSegundo1);
            System.out.printf("Hora 2 -> %02d:%02d:%02d      (%s : %s : %s)\n",
                    hora2, minuto2, segundo2, binarioHora2, binarioMinuto2, binarioSegundo2);
            System.out.print("\n¿Desea realizar una Suma (S) o una Resta (R)? ");
            System.out.flush();
        }
        String opcion = scanner.nextLine().trim().toUpperCase();

        String resHora, resMin, resSeg;
        if ("S".equals(opcion)) {
            resHora = calculadora.sumaBinaria(binarioHora1, binarioHora2);
            resMin = calculadora.sumaBinaria(binarioMinuto1, binarioMinuto2);
            resSeg = calculadora.sumaBinaria(binarioSegundo1, binarioSegundo2);
            synchronized (printLock) { System.out.println("Resultado de la suma binaria: " + resHora +" : "+ resMin+" : " + resSeg); }
        } else if ("R".equals(opcion)) {
            resHora = calculadora.restaBinaria(binarioHora1, binarioHora2);
            resMin = calculadora.restaBinaria(binarioMinuto1, binarioMinuto2);
            resSeg = calculadora.restaBinaria(binarioSegundo1, binarioSegundo2);
            synchronized (printLock) { System.out.println("Resultado de la resta binaria: " + resHora +" : "+ resMin +" : "+ resSeg); }
        } else {
            synchronized (printLock) { System.out.println("Opción no válida."); }
        }
    }

    public static void horasManuales(Scanner scanner) {
        synchronized (printLock) {
            System.out.println("\n--- OPCIÓN 2: INGRESAR HORAS MANUALMENTE ---");
            System.out.println("Formato esperado: hora:minuto:segundo (todos en binario)");
            System.out.println("");
            System.out.print("Ingrese la primera hora en binario: ");
            System.out.flush();
        }
        String horaBin1 = scanner.nextLine().trim();

        synchronized (printLock) { System.out.print("Ingrese la segunda hora en binario: "); System.out.flush(); }
        String horaBin2 = scanner.nextLine().trim();

        String[] partes1 = horaBin1.split(":");
        String[] partes2 = horaBin2.split(":");
        if (partes1.length != 3 || partes2.length != 3) { synchronized (printLock) { System.out.println("Error: formato incorrecto."); } return; }

        synchronized (printLock) { System.out.print("\n¿Desea realizar una Suma (S) o una Resta (R)? "); System.out.flush(); }
        String opcion = scanner.nextLine().trim().toUpperCase();

        String resHora, resMin, resSeg;
        if ("S".equals(opcion)) {
            resHora = calculadora.sumaBinaria(partes1[0], partes2[0]);
            resMin = calculadora.sumaBinaria(partes1[1], partes2[1]);
            resSeg = calculadora.sumaBinaria(partes1[2], partes2[2]);
            synchronized (printLock) { System.out.println("\n Resultado de la suma:"); System.out.printf("Hora resultado (binario): %s:%s:%s\n", resHora, resMin, resSeg); }
        } else if ("R".equals(opcion)) {
            resHora = calculadora.restaBinaria(partes1[0], partes2[0]);
            resMin = calculadora.restaBinaria(partes1[1], partes2[1]);
            resSeg = calculadora.restaBinaria(partes1[2], partes2[2]);
            synchronized (printLock) { System.out.println("\n Resultado de la resta:"); System.out.printf("Hora resultado (binario): %s:%s:%s\n", resHora, resMin, resSeg); }
        } else {
            synchronized (printLock) { System.out.println("Opción no válida."); }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // iniciar updater daemon (reloj estático con \r)
        ClockUpdater updater = new ClockUpdater();
        Thread hiloReloj = new Thread(updater, "ClockUpdater");
        hiloReloj.setDaemon(true);
        hiloReloj.start();

        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                menu(sc);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception ex) {
                synchronized (printLock) {
                    System.out.println("Error en el menu: " + ex.getMessage());
                }
            }
        }

        updater.stop();
        hiloReloj.interrupt();
        try { hiloReloj.join(1000); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        sc.close();
    }

}
