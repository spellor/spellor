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

public class Spellor {
    SpellorConfig config;
    private Map<String, Integer> dictionary = new HashMap<>();

    public Spellor(SpellorConfig config) {
        this.config = config;
    }

    /**
     * trains on the default data (/input/big.txt)
     */
    public void train() {
        try {
            trainOnData("input/big.txt");
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
                Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(dataPath).getPath().substring(1))))
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

    Stream<String> makeEdits(String word) {
        Stream<String> deletes = IntStream.range(0, word.length())
                .mapToObj((i) -> word.substring(0, i) + word.substring(i + 1));
        Stream<String> replaces = IntStream.range(0, word.length())
                .mapToObj((i) -> i).flatMap(
                        (i) -> "abcdefghijklmnopqrstuvwxyz".chars()
                                .mapToObj((c) -> word.substring(0, i) + (char)c + word.substring(i + 1)));
        Stream<String> inserts = IntStream.range(0, word.length() + 1)
                .mapToObj((i) -> i).flatMap(
                        (i) -> "abcdefghijklmnopqrstuvwxyz".chars()
                                .mapToObj((c) -> word.substring(0, i) + (char)c + word.substring(i)));
        Stream<String> transposes = IntStream.range(0, word.length() - 1).mapToObj(
                (i) -> word.substring(0, i) + word.substring(i + 1, i + 2) + word.charAt(i) + word.substring(i + 2));
        return Stream.of(deletes, replaces, inserts, transposes).flatMap((x) -> x);
    }

    Stream<String> knownWords(Stream<String> words){
        return words.filter(word -> dictionary.containsKey(word));
    }

    String correction(String word){
        Optional<String> getKnownOnes = knownWords(makeEdits(word))
                .max(Comparator.comparingInt(a -> dictionary.get(a)));
        if(getKnownOnes.isPresent()) {
            return dictionary.containsKey(word) ? word : getKnownOnes.get();
        }
        Optional<String> makeEditsToWord = knownWords(makeEdits(word).map( (w2)->makeEdits(w2))
                .flatMap((x)->x)).max(Comparator.comparingInt(a -> dictionary.get(a)));
        return makeEditsToWord.orElse(word);
    }
}
