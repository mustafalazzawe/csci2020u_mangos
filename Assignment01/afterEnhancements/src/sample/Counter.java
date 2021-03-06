/*
Assignment01 - Counter.java
Mustafa Al-Azzawe 100617392
Syed Daniyal Shah 100622173
*/

package sample;

import java.io.*;
import java.util.*;

public class Counter {

    //number of files being stored into
    private int numberHam;
    private int numberSpam;

    //map hold while training
    private Map<String,Integer> trainHamFrequency;
    private Map<String,Integer> trainSpamFrequency;

    //map hold probabilities
    private Map<String,Double> probFileSpam;
    private Map<String,Double> probWordSpam;
    private Map<String,Double> probWordHam;

    //map hold while testing
    private Map<String,Double> testHamProb;
    private Map<String,Double> testSpamProb;

    public Counter() {
        trainHamFrequency = new TreeMap<>();
        trainSpamFrequency = new TreeMap<>();

        probFileSpam = new TreeMap<>();
        probWordSpam = new TreeMap<>();
        probWordHam = new TreeMap<>();

        testHamProb = new TreeMap<>();
        testSpamProb = new TreeMap<>();

        numberHam = 0;
        numberSpam = 0;
    }

    //searches all directories, and if found a file not in directory, sends to searchTrain
    public void searchDirectory(File file)throws IOException{
        if (file.isDirectory()) {
            File[] filesInDir = file.listFiles();
            for (int i = 0; i < filesInDir.length; i++) {
                searchDirectory(filesInDir[i]);
            }
        }
        else {
            if(file.getAbsolutePath().contains("train"))
                searchTrain(file);
            else
                searchTest(file);
        }
    }
    //reads words from train file, saves to the map we made on top
    public void searchTrain(File file) throws IOException {

        Scanner scanner = new Scanner(file);
        if(file.getAbsolutePath().contains("train/ham")) {
            numberHam++;
            //read file word by word
            while (scanner.hasNext()) {
                String word = scanner.next().toLowerCase();
                if (isWord(word)) {
                    wordCount(word,trainHamFrequency);
                }
            }

        }
        //if current file in spam folder
        else if(file.getAbsolutePath().contains("train/spam")) {
            numberSpam++;
            while (scanner.hasNext()) {
                String word = scanner.next().toLowerCase();
                if (isWord(word)) {
                    wordCount(word,trainSpamFrequency);
                }
            }
        }
    }

    //reads words from test file, saves to map
    public void searchTest(File file) throws IOException {
        Scanner scanner = new Scanner(file);
        double probWordSpam = 0;
        while (scanner.hasNext()) {
            String word = scanner.next();
            if (isWord(word) && probFileSpam.containsKey(word))
                probWordSpam += calcSpamProb(word);
        }
        //calculate and display
        double fileIsSpam = 1/(1+(Math.pow(Math.E,probWordSpam)));

        if(file.getAbsolutePath().contains("test/ham")) {
            testHamProb.put(file.getName(),fileIsSpam);
            Inbox.setEmail(file.getName(), fileIsSpam, "Ham");
        }
        else if(file.getAbsolutePath().contains("test/spam")) {
            testSpamProb.put(file.getName(),fileIsSpam);
            Inbox.setEmail(file.getName(), fileIsSpam, "Spam");
        }
    }

    //increment duplicate words in map
    private void wordCount(String word, Map<String,Integer> map) {
        if (map.containsKey(word)) {
            int oldCount = map.get(word);
            map.put(word, oldCount+1);
        //else add to map
        } else {
            map.put(word, 1);
        }
    }
    //check if word is word
    private boolean isWord(String token) {
        String pattern = "^[a-zA-Z]*$";
        if (token.matches(pattern)) {
            return true;
        } else {
            return false;
        }
    }

    public void calcProb(){

        //calculate and store probability that word in trainHamFrequency map appears in ham file into probWordHam
        Set<String> keys = trainHamFrequency.keySet();
        Iterator<String> keyIterator = keys.iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            int count = trainHamFrequency.get(key);
            probWordHam.put(key,(double)count/numberHam);
        }
        //same concept as above except word in trainSpamFrequency
        keys = trainSpamFrequency.keySet();
        keyIterator = keys.iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            int count = trainSpamFrequency.get(key);
            probWordSpam.put(key,(double)count/numberSpam);
        }

        //calculate probability that a file is spam given a word in spam file
        keys = probWordSpam.keySet();
        keyIterator = keys.iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            if(probWordHam.containsKey(key))
                probFileSpam.put(key,probWordSpam.get(key)/(probWordSpam.get(key) + probWordHam.get(key)));
            else
                probFileSpam.put(key,1.0);
        }
    }


    public double calcSpamProb(String word) {
        double trainSpamProb = probFileSpam.get(word);
        return (Math.log(1-trainSpamProb) - Math.log(trainSpamProb));
    }


    public double calcAccuracy(){
        //calculate accuracy
        int correct = 0;
        Set<String> keys = testHamProb.keySet();
        Iterator<String> keyIterator = keys.iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            double count = testHamProb.get(key);
            if(count < 0.5){
                correct++;
            }
        }

        keys = testSpamProb.keySet();
        keyIterator = keys.iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            double count = testSpamProb.get(key);
            if(count >= 0.5){
                correct++;
            }
        }

        double accuracy = (double)correct/(testHamProb.size()+testSpamProb.size());
        return accuracy;
    }

    //calculate precision
    public double calcPrecision(){
        double precision;
        int truePos = 0;
        int falsePos = 0;

        Set<String> keys = testHamProb.keySet();
        Iterator<String> keyIterator = keys.iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            double count = testHamProb.get(key);
            if(count >= 0.5){
                falsePos++;
            }
        }

        keys = testSpamProb.keySet();
        keyIterator = keys.iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            double count = testSpamProb.get(key);
            if(count >= 0.5){
                truePos++;
            }
        }
        precision = (double)truePos / (falsePos+truePos);
        return precision;
    }
}