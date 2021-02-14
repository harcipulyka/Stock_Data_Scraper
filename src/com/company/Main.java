package com.company;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static final String BASE = "https://stocktwits.com/symbol/";
    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        List<String> tickers = getTickers();

        Scraper _1 = new Scraper(tickers.subList(0,250));
        Scraper _2 = new Scraper(tickers.subList(250,500));
        Scraper _3 = new Scraper(tickers.subList(500, 750));
        Scraper _4 = new Scraper(tickers.subList(750, tickers.size()));

        Thread thread1 = new Thread(_1);
        Thread thread2 = new Thread(_2);
        Thread thread3 = new Thread(_3);
        Thread thread4 = new Thread(_4);

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

        thread1.join();
        thread2.join();
        thread3.join();
        thread4.join();

        List<Entry> entries = new ArrayList<>();


        entries.addAll(_1.threadOnly);
        entries.addAll(_2.threadOnly);
        entries.addAll(_3.threadOnly);
        entries.addAll(_4.threadOnly);

        writeTickers(entries);
        System.out.println((System.currentTimeMillis() - startTime) / 1000);
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
            Files.writeString((Path.of("C:\\Users\\balazs\\Downloads\\1002.txt")), s);
        }  catch (IOException e) {
            System.err.println(e);
        }
    }

}
