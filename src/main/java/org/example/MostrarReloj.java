//ayudara a mostrar la hora actual de la computadora
package org.example;

import java.time.LocalTime;

public class MostrarReloj {
    //atributos
    LocalTime horaActual = LocalTime.now();
    //utilizo horaActual para obtener los siguientes parametros
    int hora = horaActual.getHour();
    int minuto = horaActual.getMinute();
    int segundo = horaActual.getSecond();

    //metodos

    /**
     * getHoraActual, obtiene la hora actual de la computadora por medio
     * de un arreglo de int
     **/
    public int[] getHoraActual() {
        return new int[]{hora, minuto, segundo};
    }

    /**getFormatoHoraActual manejo de un formato estandar para la hora**/
    public String getFormatoHoraActual() {
        // %02d= los espacios que tendran los numeros
        return String.format("%02d:%02d:%02d", hora, minuto, segundo);
    }

}
