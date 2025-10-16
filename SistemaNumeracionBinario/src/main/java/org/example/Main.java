package org.example;

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

    public static void main(String[] args) throws InterruptedException {

        while (true){
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