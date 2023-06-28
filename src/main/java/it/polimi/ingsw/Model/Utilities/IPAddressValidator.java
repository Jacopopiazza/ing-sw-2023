package it.polimi.ingsw.Model.Utilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The IPAddressValidator class provides methods to validate IP addresses, ports and URLs.
 */
public abstract class IPAddressValidator {

    /**
     * Checks if the specified IP address is valid.
     * @param ip the IP address to check
     * @return true if the IP address is valid, false otherwise
     */
    public static boolean isValidIPAddress(String ip)
    {

        // Regex for digit from 0 to 255.
        String zeroTo255
                = "(\\d{1,2}|(0|1)\\"
                + "d{2}|2[0-4]\\d|25[0-5])";

        // Regex for a digit from 0 to 255 and
        // followed by a dot, repeat 4 times.
        // this is the regex to validate an IP address.
        String regex
                = zeroTo255 + "\\."
                + zeroTo255 + "\\."
                + zeroTo255 + "\\."
                + zeroTo255;

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the IP address is empty
        // return false
        if (ip == null) {
            return false;
        }
        if(ip.equals("localhost")){
            return true;
        }

        // Pattern class contains matcher() method
        // to find matching between given IP address
        // and regular expression.
        Matcher m = p.matcher(ip);

        // Return if the IP address
        // matched the ReGex
        return m.matches();
    }

    /**
     * Checks if the specified port is valid.
     * @param port the port to check
     * @return true if the port is valid, false otherwise
     */
    public static boolean isValidPort(String port){
        try{
            int portNumber = Integer.parseInt(port);
            if(portNumber < 0 || portNumber > 65535){
                return false;
            }
        }catch(NumberFormatException e){
            return false;
        }
        return true;
    }

    /**
     * Checks if the specified port is valid.
     * @param portNumber the port to check
     * @return true if the port is valid, false otherwise
     */
    public static boolean isValidPort(Integer portNumber){
        if(portNumber < 0 || portNumber > 65535){
            return false;
        }
        return true;
    }

    /**
     * Checks if the specified URL is valid.
     * @param url the URL to check
     * @return true if the URL is valid, false otherwise
     */
    public static boolean isValidURL(String url){
        try{
            URL localurl = new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }
}
