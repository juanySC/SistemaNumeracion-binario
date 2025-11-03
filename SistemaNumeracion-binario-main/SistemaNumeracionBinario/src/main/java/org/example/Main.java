package org.example;

import org.example.operaacionesAritmeticas.OperacionAritmetica;
import org.example.reloj.ConversorBase;
import org.example.reloj.MostrarReloj;
import org.example.reloj.RelojPantalla;
import org.example.reloj.TiempoBinario;

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

    private static int binarioATotalSegundos(TiempoBinario t) {
        int h = Integer.parseInt(t.hora, 2);
        int m = Integer.parseInt(t.minuto, 2);
        int s = Integer.parseInt(t.segundo, 2);
        return h * 3600 + m * 60 + s;
    }

    private static TiempoBinario segundosATiempoBinario(int totalSegundos) {
        totalSegundos = ((totalSegundos % 86400) + 86400) % 86400; // normalizar en un día

        int h = totalSegundos / 3600;
        int m = (totalSegundos % 3600) / 60;
        int s = totalSegundos % 60;

        String hBin = conversor.decimalABase(h, 2);
        String mBin = conversor.decimalABase(m, 2);
        String sBin = conversor.decimalABase(s, 2);

        return new TiempoBinario(hBin, mBin, sBin);
    }

    public static void menu(Scanner sc) throws InterruptedException {
        synchronized (printLock) {
            System.out.println();
            System.out.println("¨========Eliga una de las opciones: ========");
            System.out.println("1. Tomar horas del reloj automaticamente");
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
        synchronized (printLock) {
            System.out.println("\n--------- HORAS AUTOMÁTICAS -----------");
        }

        System.out.print("¿Cuántos tiempos deseas sumar? (2 a 6): ");
        int cantidad = Integer.parseInt(scanner.nextLine().trim());
        if (cantidad < 2 || cantidad > 6) {
            synchronized (printLock) { System.out.println("Cantidad inválida. Debe estar entre 2 y 6."); }
            return;
        }

        TiempoBinario[] tiempos = new TiempoBinario[cantidad];
        for (int i = 0; i < cantidad; i++) {
            int[] hms = reloj.getHoraActual(); // [hora, minuto, segundo]

            String hBin = conversor.decimalABase(hms[0], 2);
            String mBin = conversor.decimalABase(hms[1], 2);
            String sBin = conversor.decimalABase(hms[2], 2);

            tiempos[i] = new TiempoBinario(hBin, mBin, sBin);

            synchronized (printLock) {
                System.out.printf("Tiempo %d -> %02d:%02d:%02d (binario: %s:%s:%s)\n",
                        i + 1, hms[0], hms[1], hms[2], hBin, mBin, sBin);
            }
            Thread.sleep(2000);
        }

        synchronized (printLock) {
            System.out.print("\n¿Desea realizar una Suma (S) o una Resta (R)? ");
            System.out.flush();
        }
        String opcion = scanner.nextLine().trim().toUpperCase();

        int resultadoSegundos = binarioATotalSegundos(tiempos[0]);
        for (int i = 1; i < cantidad; i++) {
            int currentSegundos = binarioATotalSegundos(tiempos[i]);
            if ("S".equals(opcion)) {
                resultadoSegundos += currentSegundos;
            } else if ("R".equals(opcion)) {
                resultadoSegundos -= currentSegundos;
            } else {
                synchronized (printLock) { System.out.println("Opción no válida."); }
                return;
            }
        }

        TiempoBinario resultado = segundosATiempoBinario(resultadoSegundos);

        int hDec = Integer.parseInt(resultado.hora, 2);
        int mDec = Integer.parseInt(resultado.minuto, 2);
        int sDec = Integer.parseInt(resultado.segundo, 2);

        synchronized (printLock) {
            System.out.println("\n====================================");
            System.out.println("Resultado final:");
            System.out.printf("Binario: %s:%s:%s\n", resultado.hora, resultado.minuto, resultado.segundo);
            System.out.printf("Decimal: %02d:%02d:%02d\n", hDec, mDec, sDec);
            System.out.println("====================================");
        }
    }

    public static void horasManuales(Scanner scanner) {
        synchronized (printLock) {
            System.out.println("\n--- OPCIÓN 2: INGRESAR TIEMPOS MANUALMENTE ---");
            System.out.print("¿Cuántos tiempos deseas sumar? (2 a 6): ");
            System.out.flush();
        }

        int cantidad = Integer.parseInt(scanner.nextLine().trim());
        if (cantidad < 2 || cantidad > 6) {
            synchronized (printLock) { System.out.println("Cantidad inválida. Debe estar entre 2 y 6."); }
            return;
        }

        TiempoBinario[] tiempos = new TiempoBinario[cantidad];
        for (int i = 0; i < cantidad; i++) {
            synchronized (printLock) {
                System.out.printf("Ingrese la hora %d en binario (ejemplo: 1010 = 10): ", i + 1);
                System.out.flush();
            }
            String h = scanner.nextLine().trim();

            synchronized (printLock) {
                System.out.printf("Ingrese el minuto %d en binario (ejemplo: 1010 = 10): ", i + 1);
                System.out.flush();
            }
            String m = scanner.nextLine().trim();

            synchronized (printLock) {
                System.out.printf("Ingrese el segundo %d en binario (ejemplo: 1010 = 10): ", i + 1);
                System.out.flush();
            }
            String s = scanner.nextLine().trim();

            tiempos[i] = new TiempoBinario(h, m, s);
        }

        synchronized (printLock) {
            System.out.print("\n¿Desea realizar una Suma (S) o una Resta (R)? ");
            System.out.flush();
        }
        String opcion = scanner.nextLine().trim().toUpperCase();

        int resultadoSegundos = binarioATotalSegundos(tiempos[0]);
        for (int i = 1; i < cantidad; i++) {
            int currentSegundos = binarioATotalSegundos(tiempos[i]);
            if ("S".equals(opcion)) {
                resultadoSegundos += currentSegundos;
            } else if ("R".equals(opcion)) {
                resultadoSegundos -= currentSegundos;
            } else {
                synchronized (printLock) { System.out.println("Opción no válida."); }
                return;
            }
        }

        TiempoBinario resultado = segundosATiempoBinario(resultadoSegundos);

        int hDec = Integer.parseInt(resultado.hora, 2);
        int mDec = Integer.parseInt(resultado.minuto, 2);
        int sDec = Integer.parseInt(resultado.segundo, 2);

        synchronized (printLock) {
            System.out.println("\n====================================");
            System.out.println("Resultado final:");
            System.out.printf("Binario: %s:%s:%s\n", resultado.hora, resultado.minuto, resultado.segundo);
            System.out.printf("Decimal: %02d:%02d:%02d\n", hDec, mDec, sDec);
            System.out.println("====================================");
        }
    }

    public static void main(String[] args) throws InterruptedException {
       // ClockUpdater updater = new ClockUpdater();
        //Thread hiloReloj = new Thread(updater, "ClockUpdater");
        //hiloReloj.setDaemon(true);
        //hiloReloj.start();

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

    //    updater.stop();
     //   hiloReloj.interrupt();
      //  try { hiloReloj.join(1000); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        sc.close();
    }
}
