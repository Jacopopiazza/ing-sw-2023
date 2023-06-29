package it.polimi.ingsw.Utilities;

/**
 * The NumberToWord class provides a method to convert a numeric array into its corresponding word representation.
 */
@SuppressWarnings("ALL")
public class NumberToWord {

    /**
     * Converts a numeric array into its corresponding word representation.
     *
     * @param num the numeric array to convert.
     * @return the word representation of the numeric array.
     */
    public static String getWord(char[] num) {
        String ret = "";
        int x, len;

        String[] oneDigit = new String[]{"Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
        String[] twoDigits = new String[]{"", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        String[] multipleOfTens = new String[]{"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
        String[] powerOfTens = new String[]{"Hundred", "Thousand"};

        if( ( num == null ) || (num.length == 0) ) return "None";
        if( num.length > 4 ) return "Many";

        if( num.length == 1 ) return oneDigit[num[0] - '0'];

        x = 0;
        len = num.length;
        while( x < num.length ){
            // if there are three or four digits last
            if( len >= 3 ){
                if( (num[x] - '0') > 0 ){
                    ret += oneDigit[num[x] - '0'];
                    ret += powerOfTens[len - 3];
                }
                len--;
            }
            // else (if there are one or two digits last)
            else{
                // if it's between 10 and 19
                if( ( num[x] - '0' ) == 1 ){
                    ret += twoDigits[num[x + 1] - '0' + 1];
                    return ret;
                }
                // else
                else{
                    // if it's not between 00 and 09
                    if( ( num[x] - '0' ) > 0 )
                        ret += multipleOfTens[num[x] - '0'];
                    x++;
                    // and if it's not a multiple of ten
                    if( ( num[x] - '0' ) > 0 )
                        ret += oneDigit[num[x] - '0'];
                }
            }
            x++;
        }
        return ret;
    }
}
