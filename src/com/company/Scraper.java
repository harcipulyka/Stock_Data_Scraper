package com.company;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.util.ArrayList;
import java.util.List;

public class Scraper {

    final static String BASE = "https://stocktwits.com/symbol/";

    final WebClient client;
    final List<String> links;


    public Scraper(List<String> links) {
        client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        this.links = links;
    }

    private HtmlPage getPage (String url) {
        HtmlPage page;
        try {
            page = client.getPage(BASE + url);
            return page;
        }catch(Exception e){
            System.err.println("Small problem with this link" + e);
            return null;
        }
    }

    public List<Entry> getPages () {
        List<Entry> entries = new ArrayList<>();
        List<String> wrongOnes = new ArrayList<>(); //TODO only for debugging
        for (String s : links) {
            HtmlPage p = getPage(s);
            if(p == null) {
                System.err.println("Error with the site : " + BASE + s);
                entries.add(new Entry("Wrong data", "Wrong data", "Wrong data"));
                wrongOnes.add(s);
            } else {
                Entry newEntry = getData(p);
                if(newEntry.message.equals("Wrong data")) wrongOnes.add(s);
                entries.add(newEntry);
            }
        }
        wrongOnes.stream().forEach(System.err::println);

        return entries;
    }

    private Entry getData(HtmlPage p) {

        HtmlStrong followers = p.getFirstByXPath(".//strong");

        String page = p.asXml();
        int start = page.indexOf("\"sentimentChange\":");
        int end = page.indexOf(",\"volumeChange\":");
        if(start == -1 || end == -1) return new Entry("Wrong data", "Wrong data", "Wrong data");
        String substring = page.substring(start, end);
        String[] temp = substring.split(":");

        start = page.indexOf("\"volumeChange\":");
        end = page.indexOf(",\"priceData\"");
        if(start == -1 || end == -1) return new Entry("Wrong data", "Wrong data", "Wrong data");
        substring = page.substring(start, end);
        String[] temp2 = substring.split(":");


        Entry newEntry = new Entry(followers.asText(), temp[1], temp2[1]);

        System.out.println(newEntry.toString());
        System.out.println();

        return newEntry;
    }

}
