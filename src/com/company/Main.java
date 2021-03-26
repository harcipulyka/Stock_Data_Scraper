package com.company;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static final int THREADCOUNT = 10;
    public static Variables var = new Variables(Variables.Version.PC);

    public static void main(String[] args) throws InterruptedException, IOException {
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.SEVERE);
        long startTime = System.currentTimeMillis();

        //excelOverwrite();
        pi();
        //finwiz();

        System.out.println((System.currentTimeMillis() - startTime) / 1000);
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

    //outputs the tostring version of each entry to the file
    private static void writeTickers(List<Entry> entries) {
        String s = entries.stream().map(Entry::toString).collect(Collectors.joining("\r\n"));
        try {
            Files.writeString((Path.of(var.getDebugPath())), s);
        }  catch (IOException e) {
            System.err.println("Problem writing the data" + e);
        }
    }

    //outputs a string simply to the parameter path
    private static void writeStringToPath(String s, String path) {
        try {
            Files.writeString((Path.of(path)), s);
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
        Path database = Path.of(var.getDatabasePath());

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

    //this method is maintained and used by the raspberry
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

        //writeTickers(entries);
        //appendGoodFile(entries);
    }

    //reads in the tickers, and scrapes finwiz for those tickers, no output atm
    private static void finwiz() throws InterruptedException{
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

    //returns all the data entry by ticker
    private static Map<String, List<Entry>> getData() throws IOException {
        List<String[]> data = new ArrayList<>();
        HashMap<String, List<Entry>> daysOfData = new HashMap<>();
        Files.readAllLines(Path.of(var.getDatabasePath())).stream().forEach(x -> data.add(x.split(",")));

        int numberOfDays = (data.get(0).length - 1) / 7;
        int size = data.get(0).length;

        for (String[] line : data) {
            if (line.length != size) System.err.println("Big fail, the number of columns in each row is not identical");
            String ticker = line[0];
            List<Entry> daysOfDataTemp = new ArrayList<>();
            for (int i = 0; i < numberOfDays; i++) {
                String followers = line[1 + i * 7 + 0];
                String sentiment = line[1 + i * 7 + 1];
                String message = line[1 + i * 7 + 2];
                String low52 = line[1 + i * 7 + 3];
                String high52 = line[1 + i * 7 + 4];
                String marketCap = line[1 + i * 7 + 5];
                String volume = line[1 + i * 7 + 6];
                Entry newEntry = new Entry(followers, sentiment, message, low52, high52, marketCap, volume);
                daysOfDataTemp.add(newEntry);
            }
            daysOfData.put(ticker, daysOfDataTemp);
        }

        return daysOfData;
    }

    //this converts the database file to an excel readable
    private static boolean excelOverwrite() throws IOException {
        List<String[]> data = new ArrayList<>();
        Files.readAllLines(Path.of(var.getDatabasePath())).stream().forEach(x -> data.add(x.split(",")));

        int numberOfDays = (data.get(0).length - 1) / 7;
        int size = data.get(0).length;

        String tmp = "Symbol,Day,Followers,Sentiment,Message,Volume (1000)\r\n";

        for (String[] line : data) {
            if (line.length != size) System.err.println("Big fail, the number of columns in each row is not identical");
            String ticker = line[0];
            List<Entry> daysOfDataFromTicker = new ArrayList<>();
            for (int i = 0; i < numberOfDays; i++) {
                String followers = line[1 + i * 7 + 0];
                String sentiment = line[1 + i * 7 + 1];
                String message = line[1 + i * 7 + 2];
                String low52 = line[1 + i * 7 + 3];
                String high52 = line[1 + i * 7 + 4];
                String marketCap = line[1 + i * 7 + 5];
                String volume = line[1 + i * 7 + 6];
                Entry newEntry = new Entry(followers, sentiment, message, low52, high52, marketCap, volume);
                daysOfDataFromTicker.add(newEntry);
            }
            tmp += Entry.toExcel(daysOfDataFromTicker, ticker);
        }

        System.out.println(tmp);
        writeStringToPath(tmp, var.getExcelPath());

        return true;
    }
}
