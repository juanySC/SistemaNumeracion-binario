/*l sistema de cómputo la cual presentara en un sistema numérico base 10 y
lo convertirá a un sistema de numeración base 2, base 8 y base 16*/
package org.example.reloj;

public class ConversorBase {

    public String enteroBinario(int numero){
        //utilizare StringBuilder para construir la cadena necesaria
        StringBuilder binario = new StringBuilder();
        //para esto se tiene que obtener los residuos
        if (numero == 0){
            return "0";
        }else if (numero < 0){
            //tiro una excepcion
        throw  new IllegalArgumentException ("La conversion tiene que ser con numeros positivos, vuelva a intentar");
        }

        //guarda la cadena de resultado
        String hex = "0123456789ABCDEF";
        //creo mi numero temporal para que cada uno vaya a una lista y se añada
        int numTemp = numero;

        while (numTemp > 0){
            binario.insert(0, hex.charAt(numTemp % 2));
            numTemp = numTemp / 2;
        }

        return binario.toString();

    }
}
