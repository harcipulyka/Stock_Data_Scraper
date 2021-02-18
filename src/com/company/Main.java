package com.company;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Main {

    public static final int threads = 10;
    public static void main(String[] args) throws InterruptedException, IOException {

        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.SEVERE);

        long startTime = System.currentTimeMillis();

        List<String> tickers = getTickers();
        List<List<String>> chops = chopUpList(tickers);
        List<Scraper> scrapers = new ArrayList<>();
        for(List<String> l : chops) {
            scrapers.add(new Scraper(l));
        }
        List<Thread> threads = new ArrayList<>();
        for(Scraper s : scrapers) {
            threads.add(new Thread(s));
        }
        for(Thread t : threads) {
            t.start();
        }
        for(Thread t : threads) {
            t.join();
        }
        List<Entry> entries = new ArrayList<>();
        for(Scraper s : scrapers) {
            entries.addAll(s.threadOnly);
        }

        writeTickers(entries);
        //appendGoodFile(entries);
        System.out.println((System.currentTimeMillis() - startTime) / 1000);
    }

    private static List<String> getTickers() {
        try {
            String tickers = Files.readString(Path.of("C:\\Users\\balazs\\Downloads\\6000.txt"));
            //String tickers = Files.readString(Path.of("/Users/raczbalazs/Downloads/6000.txt"));
            String[] tickers2 = tickers.split("\r\n");
            return Arrays.stream(tickers2).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void writeTickers(List<Entry> entries) {
        String s = entries.stream().map(Entry::toString).collect(Collectors.joining("\r\n"));
        try {
            Files.writeString((Path.of("C:\\Users\\balazs\\Downloads\\6000result.txt")), s);
            //Files.writeString((Path.of("/Users/raczbalazs/Downloads/6000result.txt")), s);
        }  catch (IOException e) {
            System.err.println("Problem writing the data" + e);
        }
    }

    private static List<List<String>> chopUpList(List<String> entries) {
        int size = entries.size();
        List<List<String>> partitions = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            int start = i * (size / threads);
            int end = (i + 1) * (size / threads);
            if(i + 1 == threads) {
                partitions.add(entries.subList(start, entries.size()));
            } else {
                partitions.add(entries.subList(start, end));
            }
        }

        return partitions;
    }

    private static void appendGoodFile(List<Entry> entries) throws IOException{
        Path database = Path.of("/Users/raczbalazs/Downloads/database.txt");

        List<String> lines = Files.readAllLines(database);
        if(lines.size() != entries.size()) {
            System.err.println("Big OOPSIE, The size of the entries intended to be written out did not match, that of the output files!");
            System.err.println("The entries contained " + entries.size() + " entries, and there were " + lines.size() + " lines in the output file");
            throw new IOException("Program shut down in order to avoid any more unnecessary damage!");
        } else {
            for(int i = 0; i < lines.size(); i++) {
                String newPart = entries.get(i).toCSV();
                lines.set(i, lines.get(i) + newPart);
            }
            String done = String.join("\r\n", lines);
            Files.writeString(database, done);
            System.out.println("DONE");
        }
    }
}
