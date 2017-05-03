package sample.decoder;

public class PDF417decoder {

    /**
     * Строка содержащая код в формате BASE36
     */
    public String inputCode;
    /**
     * Строка содержащая все цифры 36-ричной системы счисления
     */
    String base_sys = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /**
     * Основание системы
     */
    final int systemBase = 36;

    /**
     * Констуктор класса PDF417Decoder
     *
     * @param scanedCode Строка в формате BASE36
     */
    public PDF417decoder(String scanedCode) {
        inputCode = scanedCode;
    }

    /**
     * Извлекает из переданной на вход строки код продукции.
     * Возвращает строку содержащую отформатированный код продукта.
     *
     * @return finalCode Возвращате строку в BASE10
     */
    public String extractCode() {
        String finalCode;
        double found;
        long digit = 0;
        try {
            finalCode = inputCode.substring(4, 19);
            double power = finalCode.length() - 1;
            char arrChar[] = finalCode.toCharArray();

            for (int i = 0; i < arrChar.length; ++i) {
                found = base_sys.indexOf(arrChar[i]);
                long sum = (long) (found * Math.pow(systemBase, power));
                digit += (sum);
                --power;
            }
        } catch (ArrayIndexOutOfBoundsException outOfRange) {
            outOfRange.printStackTrace();
            return null;
        }
        finalCode = Long.toString(digit);
        while (finalCode.length() < 19) {
            finalCode = "0".concat(finalCode);
        }
        return finalCode;
    }
}