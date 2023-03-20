package hangman;

import java.util.List;
import java.util.Random;

public class WordChoice {
    public static String WordChoice(List<String> dictionaryList){
        String word = dictionaryList.get(new Random().nextInt(dictionaryList.size()));
        return word;
    }
}

