package org.example;

import org.example.operaacionesAritmeticas.OperacionAritmetica;
import org.example.reloj.ConversorBase;
import org.example.reloj.MostrarReloj;

import java.util.Scanner;

import static java.lang.Thread.sleep;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    //creando los objetos de las clases
    private static final MostrarReloj reloj = new MostrarReloj();
    private static final ConversorBase conversor = new ConversorBase();
    private static final OperacionAritmetica calculadora = new OperacionAritmetica();

    /**
     * Método auxiliar para probar la lógica de suma binaria antes de iniciar el reloj.
     */
    private static void sumaBinaria() {
        System.out.println("--- PRUEBAS DE SUMA BINARIA MANUAL ---");
        // --- CASO 1: Sin acarreo final ni alineación (5 + 2 = 7) ---
        String b1_caso1 = "101";
        String b2_caso1 = "10";
        String resultado1 = calculadora.sumaBinaria(b1_caso1, b2_caso1);
        System.out.println("\nCaso 1: 5 (101) + 2 (10)");
        System.out.println("  Resultado: " + resultado1 + " (Esperado: 111)");

        // --- CASO 2: Con acarreo final (7 + 1 = 8) ---
        String b1_caso2 = "111";
        String b2_caso2 = "1";
        String resultado2 = calculadora.sumaBinaria(b1_caso2, b2_caso2);
        System.out.println("\nCaso 2: 7 (111) + 1 (1)");
        System.out.println("  Resultado: " + resultado2 + " (Esperado: 1000)");

        // --- CASO 3: Misma longitud (12 + 3 = 15) ---
        String b1_caso3 = "1100";
        String b2_caso3 = "0011";
        String resultado3 = calculadora.sumaBinaria(b1_caso3, b2_caso3);
        System.out.println("\nCaso 3: 12 (1100) + 3 (0011)");
        System.out.println("  Resultado: " + resultado3 + " (Esperado: 1111)");

        // --- CASO 4: Alineación de binarios largos (25 + 10 = 35) ---
        String b1_caso4 = "11001";
        String b2_caso4 = "1010";
        String resultado4 = calculadora.sumaBinaria(b1_caso4, b2_caso4);
        System.out.println("\nCaso 4: 25 (11001) + 10 (1010)");
        System.out.println("  Resultado: " + resultado4 + " (Esperado: 100011)");

        System.out.println("\n=========================================");
        System.out.println(">>> INICIANDO RELOJ... <<<");
    }

    public static void main(String[] args) throws InterruptedException {

        //llamndo a mi suma binaria para probar su funcionamiento
        sumaBinaria();
        System.out.println("");
        while (true){
            System.out.println("");
            // Limpia la consola para simular la actualización del reloj
            System.out.print("\033[H\033[2J");
            System.out.flush();

            //para obtener mi hora
            int[] horaActual = reloj.getHoraActual();
            int hora = horaActual[0];
            int minuto = horaActual[1];
            int segundo = horaActual[2];

            //muestro la hora en base 10 (lo que obtengo en si de la compu
            System.out.println("La hora en base 10 es: " + reloj.getFormatoHoraActual());

            //obtendre la hora de base 10 a base 2
            String binarioHora = conversor.decimalABase(hora, 2);
            String binarioMinuto = conversor.decimalABase(minuto, 2);
            String binarioSegundo = conversor.decimalABase(segundo, 2);
            //muestro el resultado
            System.out.printf("La hora en base 2 es: %s : %s : %s\n",binarioHora, binarioMinuto, binarioSegundo);

            //obtendre la hora de base 10 a base 8

            String octalHora = conversor.decimalABase(hora, 8);
            String octalMinuto = conversor.decimalABase(minuto, 8);
            String octalSegundo = conversor.decimalABase(segundo, 8);
            //muestro el resultado
            System.out.printf("La hora en base 8 es: %s : %s : %s\n",octalHora, octalMinuto, octalSegundo);

            //obtendre la hora de base 8 a base 2

            String hexaHora = conversor.decimalABase(hora, 16);
            String hexaMinuto = conversor.decimalABase(minuto, 16);
            String hexaSegundo = conversor.decimalABase(segundo, 16);
            //muestro el resultado
            System.out.printf("La hora en base 16 es: %s : %s : %s\n",hexaHora,hexaMinuto,hexaSegundo);


            //hare una pausa de 1 segundo
            Thread.sleep(1000);
            //obtengo la hora actual con mi metodo de mostrar reloj
            System.out.println("Hora actual: " + reloj.getFormatoHoraActual());

        }



    }


}