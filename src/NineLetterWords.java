import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NineLetterWords {
    public static void main(String[] args) throws IOException {
        // Load the dictionary and categorize words by length
        Map<Integer, Set<String>> lengthToWordsMap = loadDictionaryByLength();

        // Get all 9-letter words
        Set<String> nineLetterWords = lengthToWordsMap.getOrDefault(9, new HashSet<>());

        // Memoization map to store reducibility results
        Map<String, Boolean> memo = new HashMap<>();

        // Check each 9-letter word
        List<String> validWords = new ArrayList<>();
        for (String word : nineLetterWords) {
            if (isReducible(word, lengthToWordsMap, memo)) {
                validWords.add(word);
            }
        }
        System.out.println(validWords.size());
    }

    private static Map<Integer, Set<String>> loadDictionaryByLength() throws IOException {
        List<String> words = loadAllWords();
        Map<Integer, Set<String>> lengthToWordsMap = new HashMap<>();
            words.forEach(word -> {
                int length = word.length();
                if (length <= 9) {
                    lengthToWordsMap.computeIfAbsent(length, k -> new HashSet<>()).add(word);
                }
            });
        return lengthToWordsMap;
    }

    private static boolean isReducible(String word, Map<Integer, Set<String>> lengthToWordsMap, Map<String, Boolean> memo) {


        // Base case: single-letter words are always reducible if they are "I" or "A"
        if (word.length() == 1 && (word.equals("I") || word.equals("A"))) {
            memo.put(word, true);
            return true;
        }

        //If word doesn't contain valid one-letter words we skip it.
        if (!word.contains("I") && !word.contains("A")) {
            return false;
        }

        // Check memoization map
        if (memo.containsKey(word)) {
            return memo.get(word);
        }

        // Try removing each letter and check if the resulting word is reducible
        for (int i = 0; i < word.length(); i++) {
            String shorterWord = word.substring(0, i) + word.substring(i + 1);
            if ((shorterWord.equals("A") || shorterWord.equals("I")) ||
                    (lengthToWordsMap.getOrDefault(shorterWord.length(), new HashSet<>()).contains(shorterWord) &&
                    isReducible(shorterWord, lengthToWordsMap, memo))) {
                memo.put(word, true);
                return true;
            }
        }

        // If no reduction leads to a valid sequence, mark as not reducible
        memo.put(word, false);
        return false;
    }

    private static List<String> loadAllWords() throws IOException {
        URL wordsUrl = new URL("https://raw.githubusercontent.com/nikiiv/JavaCodingTestOne/master/scrabble-words.txt");

        BufferedReader br = new BufferedReader(new InputStreamReader(wordsUrl.openConnection().getInputStream()));
        List<String> ret = br.lines().skip(2).collect(Collectors.toList());
        return ret;
    }
}
