package org.example;

import org.example.operaacionesAritmeticas.OperacionAritmetica;
import org.example.reloj.ConversorBase;
import org.example.reloj.MostrarReloj;
import org.example.reloj.RelojPantalla;

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
    }


    /**
     * Método auxiliar para probar SOLO la función de Complemento a Uno.
     */
    private static void testComplementoUno() {
        System.out.println("--- PRUEBAS DE COMPLEMENTO A UNO (C1) ---");

        // Caso 1: Binario de 8 bits (00001010 = 10 en decimal)
        String binario1 = "00001010";
        String c1_1 = calculadora.complementoAuno(binario1);

        System.out.printf("Original 1 (8 bits): %s\n", binario1);
        System.out.printf("C1 Resultado:        %s (Esperado: 11110101)\n", c1_1);

        // Caso 2: Binario de 4 bits (1100 = 12 en decimal)
        String binario2 = "1100";
        String c1_2 = calculadora.complementoAuno(binario2);

        System.out.printf("\nOriginal 2 (4 bits): %s\n", binario2);
        System.out.printf("C1 Resultado:        %s (Esperado: 0011)\n", c1_2);

        System.out.println("\n=========================================");
    }

    // MODIFICACIÓN CLAVE: Método auxiliar para probar el Complemento a Dos (C2)
    private static void testComplementoDos() {
        System.out.println("--- PRUEBAS DE COMPLEMENTO A DOS (C2) ---");

        // Caso 1: C2 de 5 (0101) en 4 bits. C1="1010". C2="1011". Representa -5.
        String binario1 = "0101";
        String c2_1 = calculadora.complementoAdos(binario1);

        System.out.printf("Original 1 (5): %s\n", binario1);
        System.out.printf("C2 Resultado:   %s (Esperado: 1011, que es -5)\n", c2_1);

        // Caso 2: C2 de 1 (0001) en 4 bits. C1="1110". C2="1111". Representa -1.
        String binario2 = "0001";
        String c2_2 = calculadora.complementoAdos(binario2);

        System.out.printf("\nOriginal 2 (1): %s\n", binario2);
        System.out.printf("C2 Resultado:   %s (Esperado: 1111, que es -1)\n", c2_2);

        // Caso 3: C2 de 8 (1000) en 4 bits. C1="0111". C2="1000". Representa -8.
        String binario3 = "1000";
        String c2_3 = calculadora.complementoAdos(binario3);

        System.out.printf("\nOriginal 3 (8): %s\n", binario3);
        System.out.printf("C2 Resultado:   %s (Esperado: 1000, que es -8)\n", c2_3);

        System.out.println("\n=========================================");
    }

    // MODIFICACIÓN CLAVE: Método auxiliar para probar la Resta Binaria usando C2
    private static void testRestaBinaria() {
        System.out.println("--- PRUEBAS DE RESTA BINARIA (A - B) CON C2 ---");

        // Caso 1: Positivo - Menor Positivo -> Resultado Positivo (8 - 3 = 5). Usando 4 bits.
        // 8 (1000) - 3 (0011) -> 1000 + C2(0011) = 1000 + 1101 = 10101 (se ignora el acarreo) -> Resultado: 0101 (5)
        String b1_caso1 = "1000"; // 8
        String b2_caso1 = "0011"; // 3
        String resta1 = calculadora.restaBinaria(b1_caso1, b2_caso1);
        System.out.println("Caso 1: 8 (1000) - 3 (0011) = 5");
        System.out.println("  Resultado: " + resta1 + " (Esperado: 0101)");

        // Caso 2: Positivo - Mayor Positivo -> Resultado Negativo (3 - 8 = -5). Usando 4 bits.
        // 3 (0011) - 8 (1000) -> 0011 + C2(1000) = 0011 + 1000 = 1011 (no hay acarreo) -> Resultado: 1011 (-5)
        String b1_caso2 = "0011"; // 3
        String b2_caso2 = "1000"; // 8
        String resta2 = calculadora.restaBinaria(b1_caso2, b2_caso2);
        System.out.println("\nCaso 2: 3 (0011) - 8 (1000) = -5");
        System.out.println("  Resultado: " + resta2 + " (Esperado: 1011)");

        // Caso 3: Resta con alineación (10 - 2 = 8). Alineado a 4 bits.
        // 10 (1010) - 2 (0010) -> 1010 + C2(0010) = 1010 + 1110 = 11000 (se ignora el acarreo) -> Resultado: 1000 (8)
        String b1_caso3 = "1010"; // 10
        String b2_caso3 = "10";   // 2 (será 0010 al alinearse)
        String resta3 = calculadora.restaBinaria(b1_caso3, b2_caso3);
        System.out.println("\nCaso 3: 10 (1010) - 2 (10) = 8");
        System.out.println("  Resultado: " + resta3 + " (Esperado: 1000)");

        // Caso 4: Resta de dos números iguales (7 - 7 = 0). Usando 4 bits.
        // 7 (0111) - 7 (0111) -> 0111 + C2(0111) = 0111 + 1001 = 10000 (se ignora el acarreo) -> Resultado: 0000 (0)
        String b1_caso4 = "0111"; // 7
        String b2_caso4 = "0111"; // 7
        String resta4 = calculadora.restaBinaria(b1_caso4, b2_caso4);
        System.out.println("\nCaso 4: 7 (0111) - 7 (0111) = 0");
        System.out.println("  Resultado: " + resta4 + " (Esperado: 0000)");

        System.out.println("\n=========================================");
    }


    /**horasAutomatico toma dos horas de la computadora y me da 3 segundos de diferencia para poder agarrar
     * las dos horas**/
    public static void horasAutomatico ()throws InterruptedException{
        System.out.println("\n--------- Hora automatica -----------");

        //creo un arreglo para obtener las horas
        int[] horaActual1 = reloj.getHoraActual();
        Thread.sleep(3000); // Espera 3 segundos antes de obtener el demas
        int[] horaActual2 = reloj.getHoraActual();

        //obteniendo hora 1
        int hora1 = horaActual1[0];
        int minuto1 = horaActual1[1];
        int segundo1 = horaActual1[2];

        //obteniendo la hora 2
        int hora2 = horaActual2[0];
        int minuto2 = horaActual2[1];
        int segundo2 = horaActual2[2];

        //utilizo mi metodo para pasarlo a mi base = binario
        String binarioHora1 = conversor.decimalABase(hora1, 2);
        String binarioMinuto1 = conversor.decimalABase(minuto1,2);
        String binarioSegundo1 = conversor.decimalABase(segundo1,2);

        String binarioHora2 = conversor.decimalABase(hora2, 2);
        String binarioMinuto2 = conversor.decimalABase(minuto2,2);
        String binarioSegundo2 = conversor.decimalABase(segundo2,2);

        //muestro

        System.out.printf("Hora 1 -> %02d:%02d:%02d    (%s : %s : %s)\n",
                hora1, minuto1, segundo1, binarioHora1, binarioMinuto1, binarioSegundo1);
        System.out.printf("Hora 2 -> %02d:%02d:%02d      (%s : %s : %s)\n",
                hora2, minuto2, segundo2, binarioHora2, binarioMinuto2, binarioSegundo2);

        Scanner scanner = new Scanner(System.in);
        System.out.print("\n¿Desea realizar una Suma (S) o una Resta (R)? ");
        //necesita ser mayuscula para que atienda que operacion realizr
        String opcion = scanner.nextLine().trim().toUpperCase();

        String resHora, resMin, resSeg;

        String resultado;
        if (opcion.equals("S")) {
            resHora = calculadora.sumaBinaria(binarioHora1, binarioHora2);
            resMin = calculadora.sumaBinaria(binarioMinuto1, binarioMinuto2);
            resSeg = calculadora.sumaBinaria(binarioSegundo1, binarioSegundo2);
            System.out.println("Resultado de la suma binaria: " + resHora +" : "+ resMin+" : " + resSeg);
        } else if (opcion.equals("R")) {
            resHora = calculadora.restaBinaria(binarioHora1, binarioHora2);
            resMin = calculadora.restaBinaria(binarioMinuto1, binarioMinuto2);
            resSeg = calculadora.restaBinaria(binarioSegundo1, binarioSegundo2);
            System.out.println("Resultado de la resta binaria: " + resHora +" : "+ resMin +" : "+ resSeg);
        } else {
            System.out.println("Opción no válida.");
            return;
        }

    }

    /**horas manuales me ayuda a capturar por medio del teclado las horas binarias que el usuario va a
     * utilizar**/
    public static void horasManuales(Scanner scanner){
        System.out.println("\n--- OPCIÓN 2: INGRESAR HORAS MANUALMENTE ---");
        System.out.println("Formato esperado: hora:minuto:segundo (todos en binario)");
        System.out.println("Ejemplo: 1101:101010:1011 (equivale a 13:42:11)");
        System.out.println("");

        // Ingreso de las dos horas
        System.out.print("Ingrese la primera hora en binario: ");
        String horaBin1 = scanner.nextLine().trim();

        System.out.print("Ingrese la segunda hora en binario: ");
        String horaBin2 = scanner.nextLine().trim();

        // Separar cada parte por los ":"
        String[] partes1 = horaBin1.split(":");
        String[] partes2 = horaBin2.split(":");

        if (partes1.length != 3 || partes2.length != 3) {
            System.out.println("Error: formato incorrecto. Use hora:minuto:segundo");
            return;
        }

        String binHora1 = partes1[0];
        String binMin1 = partes1[1];
        String binSeg1 = partes1[2];

        String binHora2 = partes2[0];
        String binMin2 = partes2[1];
        String binSeg2 = partes2[2];

        System.out.print("\n¿Desea realizar una Suma (S) o una Resta (R)? ");
        String opcion = scanner.nextLine().trim().toUpperCase();

        String resHora, resMin, resSeg;

        if (opcion.equals("S")) {
            resHora = calculadora.sumaBinaria(binHora1, binHora2);
            resMin = calculadora.sumaBinaria(binMin1, binMin2);
            resSeg = calculadora.sumaBinaria(binSeg1, binSeg2);
            System.out.println("\n Resultado de la suma:");
        } else if (opcion.equals("R")) {
            resHora = calculadora.restaBinaria(binHora1, binHora2);
            resMin = calculadora.restaBinaria(binMin1, binMin2);
            resSeg = calculadora.restaBinaria(binSeg1, binSeg2);
            System.out.println("\n Resultado de la resta:");
        } else {
            System.out.println("Opción no válida.");
            return;
        }

        // Mostrar resultados
        System.out.printf("Hora resultado (binario): %s:%s:%s\n", resHora, resMin, resSeg);
    }

    /**menu ayudara a elegir las horas para el usuario**/
    public static void menu() throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        int opcion;
        System.out.println("¨========Eliga una de las opciones: ========");
        System.out.println("1. Tomar dos horas del reloj automaticamente");
        System.out.println("2. Ingresar las horas manual ");
        System.out.println("3. Salir");
        System.out.println("Eliga una opcion: \n");
        opcion = sc.nextInt();
        sc.nextLine();

        switch (opcion){
            case 1:
                    horasAutomatico();
                break;
            case 2:
                    horasManuales(sc);
                break;
            case 3:
                System.out.println("Saliendo del programa");
                System.exit(0);
                break;
            default:
                System.out.println("Opcion no encontrada");
        }

    }

    public static void main(String[] args) throws InterruptedException {
        RelojPantalla relojPantalla = new RelojPantalla(reloj, conversor);

        // Crear un hilo separado para el reloj (así puedes seguir usando el menú)
        Thread hiloReloj = new Thread(() -> {
            try {
                relojPantalla.iniciarRelojConsola();
            } catch (InterruptedException e) {
                System.out.println("Reloj detenido.");
            }
        });

        hiloReloj.start(); // inicia el reloj “en vivo”
        //llamando a menu
        menu();



        //llamndo a mi suma binaria para probar su funcionamiento
        sumaBinaria();
        testComplementoUno();
        // MODIFICACIÓN CLAVE: Llamada al método de prueba del Complemento a Dos
        testComplementoDos();
        // MODIFICACIÓN CLAVE: Llamada al método de prueba de la Resta Binaria
        testRestaBinaria();


        System.out.println(">>> INICIANDO RELOJ... <<<");
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