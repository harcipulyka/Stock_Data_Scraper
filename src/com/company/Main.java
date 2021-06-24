package com.company;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static final int THREADCOUNT = 10;
    public static Variables var = new Variables(Variables.Version.PC);

    public static void main(String[] args) throws InterruptedException, IOException {
        //this mutes the notifications of the htmlunit library
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.SEVERE);
        long startTime = System.currentTimeMillis();

        //pi();
        demo();

        System.out.println("Whole process took " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
    }

    //gets the tickers and returns them in a list of strings
    private static List<String> getTickers() {
        try {
            String tickers = Files.readString(Path.of(var.getTickerPath()));
            String[] tickers2 = tickers.split("\r\n");
            return Arrays.stream(tickers2).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //outputs a string simply to the parameter path
    private static void writeStringToPath(String s, String path) {
        try {
            Files.writeString((Path.of(path)), s);
        }  catch (IOException e) {
            System.out.println("Problem writing the data" + e);
        }
    }

    //helper functions, divides up a list to THREADCOUNT number of equal sublists
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
    private static void appendGoodFile(List<Data> entries) throws IOException{
        Path database = Path.of(var.getDatabasePath());
        List<String> lines = Files.readAllLines(database);

        if(lines.size() != entries.size() + 1) {
            System.out.println("The size of the entries intended to be written out did not match, the number of lines in the output file!");
            System.out.println("The entries contained " + entries.size() + " entries, and there were " + lines.size() + " lines in the output file");
            throw new IOException("Program shut down in order to avoid any more unnecessary damage!");
        }

        //adds the data to the existing database
        for(int i = 0; i < entries.size(); i++) {
            String newPart = entries.get(i).toCSV();
            lines.set(i + 1, lines.get(i + 1) + newPart);
        }

        //adds the current date to the header row
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String date = "," + dtf.format(now);
        lines.set(0, lines.get(0) + date);
        String done = String.join("\r\n", lines);

        //writes out the new version of the database
        Files.writeString(database, done);
    }

    //this is one way to use the scraper, this was written for daily scraping
    private static void pi() throws InterruptedException, IOException{
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("Process started on " + dtf.format(now));

        List<String> tickers = getTickers();
        List<List<String>> chops = chopUpList(tickers);
        List<StocktwitsScraper> scrapers = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();
        List<Data> entries = new ArrayList<>();

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

        appendGoodFile(entries);
        now = LocalDateTime.now();
        System.out.println("Process finished on " + dtf.format(now));
    }

    //small demo of the finwiz scraper
    private static void demo() throws InterruptedException {
        List<String> tickers = new ArrayList<>();
        tickers.add("AAPL");
        tickers.add("GME");
        tickers.add("TSLA");

        StocktwitsScraper s = new StocktwitsScraper(tickers);
        Finwiz f = new Finwiz(tickers);

        Thread t1 = new Thread(s);
        Thread t2 = new Thread(f);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        for (Ticker t : f.tickers) {
            System.out.println();
            System.out.println("!!! " + t.data.get("Full Name") + " !!!");
            System.out.println();
            t.data.entrySet().stream().forEach(x -> {
                System.out.println(x.getKey() + " : " + x.getValue());
            });
        }
    }
}
