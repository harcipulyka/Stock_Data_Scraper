package com.company;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    //region constants
    public static final int THREADCOUNT = 10;

    public static final String PCTICKERS = "C:\\Users\\balazs\\Downloads\\1000.txt";
    public static final String MACTICKERS = "/Users/raczbalazs/Downloads/tickers.txt";
    public final static String PITICKERS = "";

    public final static String PCDEBUG = "C:\\Users\\balazs\\Downloads\\1002.txt";
    public final static String MACDEBUG = "/Users/raczbalazs/Downloads/1002.txt";
    public final static String PIDEBUG = "";

    public final static String MACDATABASE = "/Users/raczbalazs/Downloads/database.txt";
    //endregion

    public static void main(String[] args) throws InterruptedException, IOException {
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.SEVERE);

        long startTime = System.currentTimeMillis();

        if(args.length == 0) throw new IllegalArgumentException("Wrong syntax, specify sub program (pi, )");

        if (args[0].equals("pi")) pi();

        System.out.println((System.currentTimeMillis() - startTime) / 1000);
    }

    //gets the tickers and returns them in a list of strings
    private static List<String> getTickers() {
        try {
            String tickers = Files.readString(Path.of(MACTICKERS));
            String[] tickers2 = tickers.split("\r\n");
            return Arrays.stream(tickers2).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //outputs the tostring version of each entry to the file
    private static void writeTickers(List<Entry> entries) {
        String s = entries.stream().map(Entry::toString).collect(Collectors.joining("\r\n"));
        try {
            Files.writeString((Path.of(MACDEBUG)), s);
        }  catch (IOException e) {
            System.err.println("Problem writing the data" + e);
        }
    }

    //helper functions, chops up a list to threadcount number of lists
    private static List<List<String>> chopUpList(List<String> entries) {
        int size = entries.size();
        List<List<String>> partitions = new ArrayList<>();

        for (int i = 0; i < THREADCOUNT; i++) {
            int start = i * (size / THREADCOUNT);
            int end = (i + 1) * (size / THREADCOUNT);
            if(i + 1 == THREADCOUNT) {
                partitions.add(entries.subList(start, entries.size()));
            } else {
                partitions.add(entries.subList(start, end));
            }
        }

        return partitions;
    }

    //writes out the data to an existing csv file, if the line count is the same entry list size
    private static void appendGoodFile(List<Entry> entries) throws IOException{
        Path database = Path.of(MACDATABASE);

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

    //this method is maintained and used by the raspberry pi
    private static void pi() throws InterruptedException, IOException{
        List<String> tickers = getTickers();
        List<List<String>> chops = chopUpList(tickers);
        List<StocktwitsScraper> scrapers = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();
        List<Entry> entries = new ArrayList<>();

        for(List<String> l : chops) {
            StocktwitsScraper s = new StocktwitsScraper(l);
            scrapers.add(s);
            Thread t = new Thread(s);
            threads.add(t);
            t.start();
        }

        for(Thread t : threads) {
            t.join();
        }

        for(StocktwitsScraper s : scrapers) {
            entries.addAll(s.result);
        }

        writeTickers(entries);
        appendGoodFile(entries);
    }

    private static void scrapeFinwizFull() throws InterruptedException, IOException{
        List<String> tickerSymbols = getTickers();
        List<List<String>> chops = chopUpList(tickerSymbols);
        List<Finwiz> scrapers = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();
        List<Ticker> tickers = new ArrayList<>();

        for(List<String> l : chops) {
            Finwiz s = new Finwiz(Finwiz.FINWIZ.OVERVIEW, l);
            scrapers.add(s);
            Thread t = new Thread(s);
            threads.add(t);
            t.start();
        }

        for(Thread t : threads) {
            t.join();
        }

        for(Finwiz s : scrapers) {
            tickers.addAll(s.tickers);
        }
    }
}
