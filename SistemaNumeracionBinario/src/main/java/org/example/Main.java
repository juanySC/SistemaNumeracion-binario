package org.example;

import org.example.reloj.ConversorBase;
import org.example.reloj.MostrarReloj;

import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final MostrarReloj reloj = new MostrarReloj();
    public static void main(String[] args) throws InterruptedException {


    //prueba de clase MOSTRAR RELOJ
        Scanner scanner = new Scanner(System.in);
        String comando = "";

        System.out.println("--- RELOJ ---");
        System.out.println("Presione ENTER para iniciar. (Presione S para Salir en cualquier momento)");
        scanner.nextLine();

        // **LA CLAVE DEL TIEMPO REAL: El bucle while**
        while (!comando.equalsIgnoreCase("S")) {

            try {
                // 1. Limpia la consola para que el reloj "se mueva" en el mismo lugar
                System.out.print("\033[H\033[2J");
                System.out.flush();

                // 2. Obtiene e imprime la hora actualizada
                System.out.println("=".repeat(40));
                System.out.println("| HORA ACTUAL (Base 10): " + reloj.getFormatoHoraActual());
                System.out.println("=".repeat(40));

                // Aquí en el futuro irán las conversiones a Binario, Octal, y Hexadecimal.

                // 3. Menú e ingreso de comando (no bloqueante)
                System.out.println("\nS Salir");
                if (scanner.hasNextLine()) {
                    // Lee el comando (si hay entrada disponible)
                    comando = scanner.nextLine();
                }

                // 4. Pausa de 1 segundo (1000 milisegundos)
                Thread.sleep(500);

            } catch (InterruptedException e) {
                // Manejo de la interrupción del hilo
                Thread.currentThread().interrupt();
                System.out.println("Reloj detenido.");
                break;
            }
        }

        System.out.println("\nReloj apagado.");
        scanner.close();

        //probando mi clase de conversor para decimal a binario
        ConversorBase base = new ConversorBase();
        System.out.println(base.enteroBinario(10));

    }


}