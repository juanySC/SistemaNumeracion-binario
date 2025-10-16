/*l sistema de cómputo la cual presentara en un sistema numérico base 10 y
lo convertirá a un sistema de numeración base 2, base 8 y base 16*/
package org.example.reloj;

public class ConversorBase {

    /**decimalABase nos ayuda a obtener la hora en base 10 y a transformarla en base
     * 2,8 y 16, es tipo String porque sera alfanumerico para la base 16
     * @param numero me ayuda a obtener el numero de la hora actual
     * @param base es a que tipo de base voy a convertir**/
    public String decimalABase(int numero, int base){
        //si el numero fuera 0 regreso 0
        if (numero == 0){
            return  "0";
        }

        //condiciones para las bases
        //creo una cadena para mi base hexadecima ya que incluye letras
        String hexadecimal = "0123456789ABCDEF";

        //construyo la cadena de resultado
        StringBuilder resultado = new StringBuilder();
        //para no modificar mi resultado original y este se vaya actualizando cada que pasa en la condicion
        int numTemp = numero;

        //condicion para dividir el numero entre la base siempre que sea mayor que cero
        while (numTemp>0){
            //divido respecto a la base
            int residuo = numTemp % base;

            //para hexadecimal necesito ontener la posicion

                //variable hexadecimal: me ayuda a buscar en la propia cadena que caracter elegir
                //insert nos ayuda a insertar en la posicion 0 el caracter que obtengo de la cadena hexadecimal
                //charAt obtiene el caracter una posicion especifica
                resultado.insert(0, hexadecimal.charAt(residuo));

            //ahora con lo que me queda sigo operando

            numTemp = numTemp/base;
        }
        //retorno el StringBuilder ya convertido en un String
        return resultado.toString();
    }
}
