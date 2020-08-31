/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

/**
 *
 * @author Ana
 */
public class PasswordValidator {

    public static boolean valid(String password) {
        char[] pass = password.toCharArray();
        char[] specials = {'#', '*', '.', '!', '?', '$', '"', '^', '+', '-', ':', ';', 
            '<', '>', '/', '(', ')', '[', ']', '{', '}', '@', ':', ';', ',', '~', '=',
            '%', '_', '&', '£'};
        if (pass.length < 8) {
            return false;
        }
        
        int specialCount = 0;
        int digitCount = 0;
        int lowerCount = 0;
        int upperCount = 0;
        for (int i = 0; i < pass.length; i++) {
            if (Character.isLowerCase(pass[i])) {
                lowerCount++;
            }
            if (Character.isUpperCase(pass[i])) {
                upperCount++;
            }
            if (Character.isDigit(pass[i])) {
                digitCount++;
            }
            for (int j = 0; j < specials.length; j++) {
                if (pass[i] == specials[j]) {
                    specialCount++;
                    break;
                }
            }
        }
        if (lowerCount < 1 || upperCount < 1 || digitCount < 1 || specialCount < 1) {
            return false;
        }
        return true;
    }

}


