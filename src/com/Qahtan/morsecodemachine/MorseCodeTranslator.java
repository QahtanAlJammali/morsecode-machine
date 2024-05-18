//created a package based on the nomenclature of how they are supposed to be named!
package com.Qahtan.morsecodemachine; 

import java.util.HashMap;
import java.util.Map;

public class MorseCodeTranslator {
    private static final Map<Character, String> morseMap = new HashMap<>();
    private static final Map<String, Character> englishMap = new HashMap<>();

    static {
        morseMap.put('A', ".-");
        morseMap.put('B', "-...");
        morseMap.put('C', "-.-.");
        morseMap.put('D', "-..");
        morseMap.put('E', ".");
        morseMap.put('F', "..-.");
        morseMap.put('G', "--.");
        morseMap.put('H', "....");
        morseMap.put('I', "..");
        morseMap.put('J', ".---");
        morseMap.put('K', "-.-");
        morseMap.put('L', ".-..");
        morseMap.put('M', "--");
        morseMap.put('N', "-.");
        morseMap.put('O', "---");
        morseMap.put('P', ".--.");
        morseMap.put('Q', "--.-");
        morseMap.put('R', ".-.");
        morseMap.put('S', "...");
        morseMap.put('T', "-");
        morseMap.put('U', "..-");
        morseMap.put('V', "...-");
        morseMap.put('W', ".--");
        morseMap.put('X', "-..-");
        morseMap.put('Y', "-.--");
        morseMap.put('Z', "--..");
        
        //for reverse translation (from Morse to English) I added another map with the reverse of the original map
        for (Map.Entry<Character, String> entry : morseMap.entrySet()) {
            englishMap.put(entry.getValue(), entry.getKey());
        }
    }
    
    //the method to convert english to morse
    public static String toMorseCode(String input) {
        StringBuilder morse = new StringBuilder();
        for (char c : input.toUpperCase().toCharArray()) {
            if (morseMap.containsKey(c)) {
                morse.append(morseMap.get(c)).append(" ");
            } else if (c == ' ') {
                morse.append("   "); 	// separate words with three spaces, since the letters are seperated with a single space.
            }
        }
        return morse.toString().trim();	// remove any trailing spaces. 
    }

    // the method to convert from morse code to english
    public static String fromMorseCode(String morseCode) {
        StringBuilder english = new StringBuilder();
        String[] words = morseCode.trim().split("   ");  // morse code words are separated by three spaces
        for (String word : words) {
            for (String letter : word.split(" ")) {
                if (englishMap.containsKey(letter)) {
                    english.append(englishMap.get(letter));
                }
            }
            english.append(" ");  // add a space after each word
        }
        return english.toString().trim();
    }
}
