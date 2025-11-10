package org.example;

import org.example.controller.MenuController;
import org.example.operaacionesAritmeticas.OperacionAritmetica;
import org.example.reloj.ConversorBase;
import org.example.reloj.MostrarReloj;

public class Main {
    public static void main(String[] args) {
        try {
            // las clases para realizar las operaciones y visulizacion del menu
            MostrarReloj reloj = new MostrarReloj();
            ConversorBase conversor = new ConversorBase();
            OperacionAritmetica calculadora = new OperacionAritmetica();

            //el menu en lanterna o sea que inicia la aplicacion y el controlador
            MenuController controller = new MenuController(reloj, conversor, calculadora);
            controller.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al iniciar la aplicación. Pega este stacktrace aquí y lo corrijo.");
            System.exit(1);
        }
    }
}
