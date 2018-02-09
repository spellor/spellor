/*
 * Copyright (c) spellor 2018.
 * Spell checker and corrector, written in Java 8
 *
 * @author Man Parvesh Singh Randhawa <manparveshsinghrandhawa@gmail.com>
 */

package com.manparvesh.spellor;

import com.manparvesh.spellor.config.SpellorConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.manparvesh.spellor.config.SpellorConfig.*;

public class Spellor {
    private SpellorConfig config;
    private Map<String, Integer> dictionary = new HashMap<>();

    /**
     * This constructor is currently not used much, since the ADVANCED_CONFIG is not implemented yet
     *
     * @param config The type of config you need for your project
     */
    public Spellor(SpellorConfig config) {
        this.config = config;
        train();
    }

    /**
     * Just creates an object with SIMPLE_CONFIG
     */
    public Spellor(){
        this(SIMPLE_CONFIG);
    }

    /**
     * trains on the default data (/input/big.txt)
     */
    private void train() {
        try {
            String filePath = "input/big.txt";
            // gets path of the file inside resources
            String dataPath = ClassLoader.getSystemResource(filePath).getPath().substring(1);
            trainOnData(dataPath);
        } catch (IOException e) {
            System.err.println("big.txt doesn't exist!");
        }
    }

    /**
     * Gets the number of words inside the file at the given path
     *
     * @param dataPath path of the file
     */
    public void trainOnData(String dataPath) throws IOException {
        String wordsString = new String(
                Files.readAllBytes(Paths.get(dataPath)))
                .toLowerCase()
                .replaceAll("[^a-z ]", " ");
        Stream.of(wordsString.split(" "))
                .filter(word -> word.trim().length() > 0)
                .forEach(word -> dictionary.compute(word, (key, value) ->
                        value == null ? 1 : value + 1
                ));
        //dictionary.forEach((key, value) ->
        //        System.out.println("Word: " + key + " Count: " + value)
        //);
    }

    private Stream<String> makeEdits(String word) {
        /**
         * deletes a character and checks if it matches anything in the map
         * */
        Stream<String> deletes = IntStream.range(0, word.length())
                .mapToObj((i) -> word.substring(0, i) + word.substring(i + 1));

        /**
         * replaces a character with some other character and checks if it matches anything in the map
         * */
        Stream<String> replaces = IntStream.range(0, word.length())
                .mapToObj((i) -> i).flatMap(
                        (i) -> "abcdefghijklmnopqrstuvwxyz".chars()
                                .mapToObj((c) -> word.substring(0, i) + (char)c + word.substring(i + 1)));

        /**
         * inserts a character in all places one by one and checks if it matches anything in the map
         * */
        Stream<String> inserts = IntStream.range(0, word.length() + 1)
                .mapToObj((i) -> i).flatMap(
                        (i) -> "abcdefghijklmnopqrstuvwxyz".chars()
                                .mapToObj((c) -> word.substring(0, i) + (char)c + word.substring(i)));

        /**
         * changes a character and checks if it matches anything in the map
         * */
        Stream<String> transposes = IntStream.range(0, word.length() - 1).mapToObj(
                (i) -> word.substring(0, i) + word.substring(i + 1, i + 2) + word.charAt(i) + word.substring(i + 2));

        return Stream.of(deletes, replaces, inserts, transposes).flatMap((x) -> x);
    }

    /**
     * Gives us a stream of words that are there in the dictionary
     *
     * @param words stream of the words that need to be checked
     * @return words that are there in the dictionary
     */
    private Stream<String> knownWords(Stream<String> words) {
        return words.filter(word -> dictionary.containsKey(word));
    }

    /**
     * Make the appropriate corrections and return the correct word
     *
     * @param word word to correct
     * @return corrected word
     */
    public String correction(String word) {
        Optional<String> getKnownOnes = knownWords(makeEdits(word))
                .max(Comparator.comparingInt(a -> dictionary.get(a)));
        if (getKnownOnes.isPresent()) {
            return dictionary.containsKey(word) ? word : getKnownOnes.get();
        }
        Optional<String> makeEditsToWord = knownWords(makeEdits(word).map((w2) -> makeEdits(w2))
                .flatMap((x) -> x)).max(Comparator.comparingInt(a -> dictionary.get(a)));
        return makeEditsToWord.orElse(word);
    }
}
