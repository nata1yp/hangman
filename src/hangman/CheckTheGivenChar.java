package hangman;

import java.util.List;
import java.util.Map;

public class CheckTheGivenChar {
    public static boolean CheckTheGivenCharMethod(String word, List<String> dictionaryList, char c, int position){

        int points;
        boolean foundedChar;
        if (word.charAt(position)==c){
            foundedChar = true;
            for (int i = 0; i < dictionaryList.size(); i++) {
                if (dictionaryList.get(i).charAt(position) != c) {
                    dictionaryList.remove(dictionaryList.get(i));
                    i--;
                }
            }
        }
        else{
            foundedChar = false;
            for (int i = 0; i < dictionaryList.size(); i++) {
                if (dictionaryList.get(i).charAt(position) == c) {
                    dictionaryList.remove(dictionaryList.get(i));
                    i--;
                }
            }
        }
        return foundedChar;
    }
}

