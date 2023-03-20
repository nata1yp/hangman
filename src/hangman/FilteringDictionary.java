package hangman;

import java.util.ArrayList;
import java.util.List;

public class FilteringDictionary {

    public static List<String> FilterWordLength(String words[]) {
        List<String> WordsWithRightLength = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() > 5) {
                WordsWithRightLength.add(words[i]);
            }
        }
        return WordsWithRightLength;
    }

    public static List<String> removeDuplicates(List<String> listWithDuplicates)
    {
        // Create a new ArrayList
        ArrayList<String> listWithoutDuplicates = new ArrayList<String>();

        // Traverse through the first list
        for (String element : listWithDuplicates) {

            // If this element is not present in newList
            // then add it
            if (!listWithoutDuplicates.contains(element)) {

                listWithoutDuplicates.add(element);
            }
        }
        // return the new list
        return listWithoutDuplicates;
    }

}


