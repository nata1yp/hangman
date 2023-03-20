package hangman;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileCreator {
    public static void FileCreatorMethod(String value, String id) throws IOException, UndersizeException, UnbalancedException {
        String strippedInput = value.replaceAll("\\W", " ");
        String word[] = strippedInput.split(" ");

        List<String> WordsWithRightLength = new ArrayList<>();
        WordsWithRightLength = FilteringDictionary.FilterWordLength(word);

        List<String> WordsWithoutDuplicates = new ArrayList<>();
        WordsWithoutDuplicates = FilteringDictionary.removeDuplicates(WordsWithRightLength);

        List<String> DictionaryList = new ArrayList<>();
        DictionaryList = WordsWithoutDuplicates;

        int counter = 0;
        boolean unbalanced = false;
        for (int i = 0; i < DictionaryList.size(); i++) {
            if (DictionaryList.get(i).length() >= 9) {
                counter++;
            }
        }
        int percentage = counter * 100 / DictionaryList.size();
        if (percentage < 20) {
            unbalanced = true;
        }

        if (DictionaryList.size() < 20) {
            throw new UndersizeException("Undersize Exception");
        } else if (unbalanced) {
            throw new UnbalancedException("Unbalanced Exception");
        } else {
            String directory = "medialab/hangman-" + id + ".txt";
            FileWriter fw = new FileWriter(directory);
            for (int i = 0; i < DictionaryList.size(); i++) {
                fw.write(DictionaryList.get(i));
                fw.write("\n");
            }
            fw.close();
        }
    }
}
