//suma y resta binaria
package org.example.operaacionesAritmeticas;

public class OperacionAritmetica {

    /**completarConCeros me ayudara a rellenear el numero binario obtenido
     * ejemplo -> "010" para completar -> "0010" se llerellena con ceros a la izquierda**/
    public String completarConCeros(String binario, int tamanioIdeal){

        //Caso 1: si el binario tiene lo deseado simplemente se retorna
        if (binario.length() >= tamanioIdeal){
            return binario;
        }

        //Caso 2:recorre mi binario y saca los ceros que necesita para rellenar
        int ceroNecesarios = tamanioIdeal - binario.length();
        //construyo la cadena para los ceros
        StringBuilder cadenaCeros = new StringBuilder();
        
        //creo un ciclo para rellenar los ceros faltantes
        for (int i = 0; i < ceroNecesarios; i++) {
           cadenaCeros.append("0");
        }

        return cadenaCeros.toString() + binario;

    }

    /**sumaBinaria contiene la logica par apoder sumar dos horas**/
    public String sumaBinaria(String binario1, String binario2){

    }

}
