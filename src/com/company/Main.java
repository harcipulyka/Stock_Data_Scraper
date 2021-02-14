package com.company;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static final String BASE = "https://stocktwits.com/symbol/";
    public static void main(String[] args) {
        List<String> tickers = getTickers();
        tickers.stream().forEach(x -> System.out.println(BASE + x));
        Scraper scraper = new Scraper(tickers);
        List<Entry> entries = scraper.parsePages();

        System.out.println("");
        System.out.println("");
        System.out.println("");
        writeTickers(entries);
    }

    public static List<String> getTickers() {
        try {
            String tickers = Files.readString(Path.of("C:\\Users\\balazs\\Downloads\\1000.txt"));
             String[] tickers2 = tickers.split("\r\n");
            return Arrays.stream(tickers2).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeTickers(List<Entry> entries) {
        String s = entries.stream().map(x -> x.toString()).collect(Collectors.joining("\r\n"));
        try {
            Files.writeString((Path.of("C:\\Users\\balazs\\Downloads\\1001.txt")), s);
        }  catch (IOException e) {
            System.err.println(e);
        }
    }

}
