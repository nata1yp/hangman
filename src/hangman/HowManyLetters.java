package hangman;

import java.util.List;

public class HowManyLetters {
    public static float[] countNumOfLetters(List<String> dictionaryList){
        int numOfWords = dictionaryList.size();
        float wordOf6 = 0;
        float wordOf7to9 = 0;
        float wordOf10orMore = 0;
        for (int i=0; i<numOfWords; i++){
            String currentWord = dictionaryList.get(i);
            if (currentWord.length()==6){
                wordOf6++;
            }
            else if ((currentWord.length()>=7) && (currentWord.length()<=9)){
                wordOf7to9++;
            }
            else if (currentWord.length()>=10){
                wordOf10orMore++;
            }
        }

        float[] lettersOfWords = new float[] {wordOf6*100/numOfWords, wordOf7to9*100/numOfWords, wordOf10orMore*100/numOfWords};
        return lettersOfWords;
    }
}
