//ayudara a mostrar la hora actual de la computadora
package org.example;

import java.time.LocalTime;

public class MostrarReloj {
    //atributos
    LocalTime horaActual = LocalTime.now();
    //utilizo horaActual para obtener los siguientes parametros
    int hora =  horaActual.getHour();
    int minuto = horaActual.getMinute();
    int segundo = horaActual.getSecond();

    //metodos
    /**getHoraActual, obtiene la hora actual de la computadora por medio
     * de un arreglo de int**/
    public int[] getHoraActual(){
        return new int[]{hora, minuto, segundo};
    }

}
