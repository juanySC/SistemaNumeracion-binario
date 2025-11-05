/*l sistema de cómputo la cual presentara en un sistema numérico base 10 y
lo convertirá a un sistema de numeración base 2, base 8 y base 16*/
package org.example.reloj;

public class ConversorBase {

    /**decimalABase nos ayuda a obtener la hora en base 10 y a transformarla en base
     * 2,8 y 16, es tipo String porque sera alfanumerico para la base 16
     * @param numero me ayuda a obtener el numero de la hora actual
     * @param base es a que tipo de base voy a convertir**/
    public String decimalABase(int numero, int base){
        if (base < 2 || base > 16) throw new IllegalArgumentException("Base debe ser entre 2 y 16");
        //si el numero fuera 0 regreso 0
        if (numero == 0){
            return  "0";
        }

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

            //variable hexadecimal: me ayuda a buscar en la propia cadena que caracter elegir
            //insert nos ayuda a insertar en la posicion 0 el caracter que obtengo de la cadena hexadecimal
            //charAt obtiene el caracter una posicion especifica
            resultado.insert(0, hexadecimal.charAt(residuo));

            //opero el numero segun la base que tenga
            numTemp = numTemp/base;
        }
        //retorno el StringBuilder ya convertido en un String
        return resultado.toString();
    }

    public static void mostrarHoraBase(int h, int m, int s) {
        // Calculamos las bases
        String binario = String.format("%s:%s:%s",
                Integer.toBinaryString(h),
                Integer.toBinaryString(m),
                Integer.toBinaryString(s));

        String octal = String.format("%s:%s:%s",
                Integer.toOctalString(h),
                Integer.toOctalString(m),
                Integer.toOctalString(s));

        String hexa = String.format("%s:%s:%s",
                Integer.toHexString(h).toUpperCase(),
                Integer.toHexString(m).toUpperCase(),
                Integer.toHexString(s).toUpperCase());

        // Imprimimos todo en un bloque separado y legible
        System.out.println("___________________________________");
        System.out.println("              RELOJ ACTUAL");
        System.out.println("___________________________________");
        System.out.println("Base 10: " + String.format("%02d:%02d:%02d", h, m, s));
        System.out.println("Base  2: " + binario);
        System.out.println("Base  8: " + octal);
        System.out.println("Base 16: " + hexa);
        System.out.println("====================================");
    }

    /**
     * Convierte una cadena que representa un número en la base indicada a su valor decimal (int).
     * Admite bases entre 2 y 16. Para base > 10 acepta letras mayúsculas A-F.
     * Lanzará NumberFormatException si encuentra un carácter inválido.
     */
    public int baseStringToDecimal(String s, int base) {
        if (s == null || s.isEmpty()) {
            throw new NumberFormatException("Cadena vacía");
        }
        if (base < 2 || base > 16) {
            throw new IllegalArgumentException("Base debe ser entre 2 y 16");
        }
        s = s.trim().toUpperCase();
        int result = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int value;
            if (c >= '0' && c <= '9') {
                value = c - '0';
            } else if (c >= 'A' && c <= 'F') {
                value = 10 + (c - 'A');
            } else {
                throw new NumberFormatException("Carácter inválido para la base: " + c);
            }
            if (value >= base) {
                throw new NumberFormatException("Dígito '" + c + "' no válido para base " + base);
            }
            result = result * base + value;
        }
        return result;
    }

}