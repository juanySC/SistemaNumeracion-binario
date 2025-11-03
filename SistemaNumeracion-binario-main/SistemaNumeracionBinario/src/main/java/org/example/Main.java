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
        synchronized (printLock) {
            System.out.println("\n--------- HORAS AUTOMÁTICAS -----------");
        }

        System.out.print("¿Cuántas horas deseas sumar? (2 a 6): ");
        int cantidad = Integer.parseInt(scanner.nextLine().trim());
        if (cantidad < 2 || cantidad > 6) {
            synchronized (printLock) { System.out.println("Cantidad inválida. Debe estar entre 2 y 6."); }
            return;
        }

        String[] horasBinarias = new String[cantidad];
        for (int i = 0; i < cantidad; i++) {
            int[] horaActual = reloj.getHoraActual();
            String horaBin = conversor.decimalABase(horaActual[0], 2);
            horasBinarias[i] = horaBin;
            synchronized (printLock) {
                System.out.printf("Hora %d -> %02d (binario: %s)\n", i + 1, horaActual[0], horaBin);
            }
            Thread.sleep(2000);
        }

        synchronized (printLock) {
            System.out.print("\n¿Desea realizar una Suma (S) o una Resta (R)? ");
            System.out.flush();
        }
        String opcion = scanner.nextLine().trim().toUpperCase();

        String resultado = horasBinarias[0];
        for (int i = 1; i < cantidad; i++) {
            if ("S".equals(opcion)) {
                resultado = calculadora.sumaBinaria(resultado, horasBinarias[i]);
            } else if ("R".equals(opcion)) {
                resultado = calculadora.restaBinaria(resultado, horasBinarias[i]);
            } else {
                synchronized (printLock) { System.out.println("Opción no válida."); }
                return;
            }
        }

        int resultadoDecimal = Integer.parseInt(resultado, 2) % 24;
        synchronized (printLock) {
            System.out.println("\n====================================");
            System.out.println("Resultado final:");
            System.out.printf("Binario: %s\n", resultado);
            System.out.printf("Decimal (hora ajustada 0–23): %02d\n", resultadoDecimal);
            System.out.println("====================================");
        }
    }


    public static void horasManuales(Scanner scanner) {
        synchronized (printLock) {
            System.out.println("\n--- OPCIÓN 2: INGRESAR HORAS MANUALMENTE ---");
            System.out.print("¿Cuántas horas deseas sumar? (2 a 6): ");
            System.out.flush();
        }

        int cantidad = Integer.parseInt(scanner.nextLine().trim());
        if (cantidad < 2 || cantidad > 6) {
            synchronized (printLock) { System.out.println("Cantidad inválida. Debe estar entre 2 y 6."); }
            return;
        }

        String[] horasBinarias = new String[cantidad];
        for (int i = 0; i < cantidad; i++) {
            synchronized (printLock) {
                System.out.printf("Ingrese la hora %d en binario (solo horas, ej. 1010 = 10): ", i + 1);
                System.out.flush();
            }
            horasBinarias[i] = scanner.nextLine().trim();
        }

        synchronized (printLock) {
            System.out.print("\n¿Desea realizar una Suma (S) o una Resta (R)? ");
            System.out.flush();
        }
        String opcion = scanner.nextLine().trim().toUpperCase();

        String resultado = horasBinarias[0];
        for (int i = 1; i < cantidad; i++) {
            if ("S".equals(opcion)) {
                resultado = calculadora.sumaBinaria(resultado, horasBinarias[i]);
            } else if ("R".equals(opcion)) {
                resultado = calculadora.restaBinaria(resultado, horasBinarias[i]);
            } else {
                synchronized (printLock) { System.out.println("Opción no válida."); }
                return;
            }
        }

        int resultadoDecimal = Integer.parseInt(resultado, 2) % 24;
        synchronized (printLock) {
            System.out.println("\n====================================");
            System.out.println("Resultado final:");
            System.out.printf("Binario: %s\n", resultado);
            System.out.printf("Decimal (hora ajustada 0–23): %02d\n", resultadoDecimal);
            System.out.println("====================================");
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
