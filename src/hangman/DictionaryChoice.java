package hangman;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DictionaryChoice {
    public static List<String> DictionaryChoice(String id) throws IOException {
            String dictionary = "medialab/hangman-" + id + ".txt";
            List<String> dictionaryList = new ArrayList<String>();
            BufferedReader reader = new BufferedReader(new FileReader(dictionary));
            String line;
            while ((line = reader.readLine()) != null) {
                dictionaryList.add(line);
            }
            reader.close();
            return dictionaryList;
    }
}

