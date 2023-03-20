package hangman;

import java.util.*;
import java.util.List;

import static java.util.stream.Collectors.toMap;

public class Round {

    /**
     * This method takes as parameters a word and a list of words.
     * It calculates the length of the given word.
     * For each word in the list the method checks if their length is the same as the given word.
     * If the length of a word in the list is not the same as the given word's length, the word is removed from the list.
     * The method returns the list only with the words have the same length as the given word.
     * @param word String word is the hidden word that the player has to find
     * @param dictionaryList list of Strings with all the words in a dictionary
     * @return list of Strings with words having the same length as the given word.
     */

    public static List<String> RightLengthMethod(String word, List<String> dictionaryList) {
        int i, j;
        for (i = 0; i < dictionaryList.size(); i++) {
            if (dictionaryList.get(i).length() != word.length()) {
                dictionaryList.remove(dictionaryList.get(i));
                i--;
            }
        }
        return dictionaryList;
    }


    /**
     * This method takes as parameters a word and a list of words with the same length.
     * It creates a list, named 'letters', of lists of characters
     * For each word in the given list the method gets the letter in a specific position and add it to a list of characters,
     * and after adds the created list of characters to the list named 'letters'.
     * For example: For each word in the given list the method gets the letter in position 0 and add the letter in the same list.
     * After that the method adds the list with every letter in position 0 to the list named 'letters'
     * This process is repeated for each position in the words of the given list.
     * Returns a list with size same as the length of the given word.
     * Each element of this list is another list with characters. The size of these lists is the same as the size of the given list.
     *
     * @param word String word is the hidden word that the player has to find
     * @param dictionaryList list of Strings with the same length as the given word
     * @return list with size same as the length of the given word. The list includes lists of characters, one list for every position of the given word.
     */
    public static List<List<Character>> FindTheCharacters(String word, List<String> dictionaryList) {
        int i, j;
        List<List<Character>> letters = new ArrayList<List<Character>>(word.length());
        for (i = 0; i < word.length(); i++) {
            letters.add(new ArrayList<Character>());
        }
        for (i = 0; i < dictionaryList.size(); i++) {
            for (j = 0; j < word.length(); j++) {
                char element = dictionaryList.get(i).charAt(j);
                letters.get(j).add(element);
            }
        }
        return letters;
    }


    /**
     * This method takes as parameter a list of characters.
     * It counts how many times each character of the list shows up
     * and create a map which includes entries of key-value pairs.
     * Every key is a character from the given list and value is the value of occurences of the character in the list
     * @param list list of characters
     * @return map whose key-value pairs are characters and the occurrences of them
     */
    public static Map<Character, Float> countFrequencies(List<Character> list)
    {
        // hashmap to store the frequency of element
        Map<Character, Float> CharacterOccurrences = new HashMap<Character, Float>();

        for (Character i : list) {
            Float j = CharacterOccurrences.get(i);
            CharacterOccurrences.put(i, (j == null) ? 1 : j + 1);
        }
        Map<Character, Float> SortedCharacterOccurences = CharacterOccurrences
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
        return SortedCharacterOccurences;
    }


    /**
     * This method takes as parameters a map whose key-value pairs are characters and the occurrences of them
     * and an integer.
     * It calculates the probability of occurrence for each character in the map dividing the value of each entry
     * with the given integer.
     * @param CharOcc map whose key-value pairs are characters and the occurrences of them
     * @param size int that represents the sum of all values from the map
     * @return map whose key-value pairs are characters and the probability of occurrence of them
     */
    public static  Map<Character, Float> CalculateProbability(Map<Character, Float> CharOcc, int size){
        for (Map.Entry<Character, Float> entry : CharOcc.entrySet()){
            CharOcc.put(entry.getKey(),entry.getValue()/size);
        }
        return CharOcc;
    }

    /**
     * This method calls sequentially every other method in the class.
     * It takes the returned result of another method and call the next method with that as parameter.
     * Finally it creates a list of maps which are the returned results of another method.
     * @param word String word is the hidden word that the player has to find
     * @param dictionaryList list of Strings with all the words in the given dictionary
     * @return list of maps, where every entry of each map is a pair of key-value.
     *         Every key is a character and the value is the probability of occurrence for the key.
     */
    public static  List<Map<Character, Float>> FilteringDictionaryList(String word, List<String> dictionaryList){
        List<String> RightLengthList = RightLengthMethod(word, dictionaryList);
        List<List<Character>> letters = FindTheCharacters(word,RightLengthList);
        List<Map<Character, Float>> ListOfProbPerSpace = new ArrayList<Map<Character, Float>>(word.length());

        for (int i = 0; i < word.length(); i++){
            Map<Character, Float> CharacterOccurrences = countFrequencies(letters.get(i));
            Map<Character, Float> ProbabilityPerChar = CalculateProbability(CharacterOccurrences, dictionaryList.size());
            ListOfProbPerSpace.add(ProbabilityPerChar);
        }
        return ListOfProbPerSpace;
    }
}

