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
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.SEVERE);
        long startTime = System.currentTimeMillis();

        pi();

        System.out.println("There was approximetly " + StocktwitsScraper.numberOfNot + " times, when it run into a problem with creating the JSOn file");
        System.out.println("Whole proces took " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
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
    private static void appendGoodFile(List<Data> entries) throws IOException{
        Path database = Path.of(var.getDatabasePath());

        List<String> lines = Files.readAllLines(database);
        if(lines.size() != entries.size() + 1) {
            System.out.println("Big OOPSIE, The size of the entries intended to be written out did not match, that of the output files!");
            System.out.println("The entries contained " + entries.size() + " entries, and there were " + lines.size() + " lines in the output file");
            throw new IOException("Program shut down in order to avoid any more unnecessary damage!");
        } else {
            for(int i = 0; i < entries.size(); i++) {
                String newPart = entries.get(i).toCSV();
                lines.set(i + 1, lines.get(i + 1) + newPart);
            }
            //settings the date in the first row
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String date = "," + dtf.format(now);
            lines.set(0, lines.get(0) + date);
            String done = String.join("\r\n", lines);
            Files.writeString(database, done);
        }
    }

    //this method is maintained and used by the raspberry
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
}
