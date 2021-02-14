package com.company;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scraper {

    final static Entry WRONG_ENTRY = new Entry("NaN","NaN", "NaN", null);
    final static String BASE = "https://stocktwits.com/symbol/";

    final WebClient client;
    final List<String> links;


    public Scraper(List<String> links) {
        client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        this.links = links;
    }

    private HtmlPage getPage (String ticker) {
        HtmlPage page;
        try {
            page = client.getPage(BASE + ticker);
            return page;
        }catch(Exception e){
            System.err.println("Small problem with this link" + e);
            return null;
        }
    }

    public List<Entry> parsePages() {
        List<Entry> entries = new ArrayList<>();
        List<String> wrongOnes = new ArrayList<>(); //TODO only for debugging

        for (String s : links) {
            HtmlPage p = getPage(s);
            Entry newEntry = getData(p);
            if(newEntry == WRONG_ENTRY) wrongOnes.add(s); //TODO also only for debugging
            entries.add(newEntry);
        }

        wrongOnes.stream().forEach(System.err::println);
        return entries;
    }

    private Entry getData(HtmlPage p) {
        //this helps cleaning up the parse method
        if(p == null){
            System.err.println("Unrecovarable error with the URL: " + p.getUrl());
            return WRONG_ENTRY;
        }

        //followers
        HtmlStrong f = p.getFirstByXPath(".//strong");
        String followers = f.asText();

        //sentiment
        String page = p.asXml();
        int start = page.indexOf("\"sentimentChange\":");
        int end = page.indexOf(",\"volumeChange\":");
        if(start == -1 || end == -1) return WRONG_ENTRY;
        String substring = page.substring(start, end);
        String[] temp = substring.split(":");
        String sentiment = temp[1];

        //volume
        start = page.indexOf("\"volumeChange\":");
        end = page.indexOf(",\"priceData\"");
        if(start == -1 || end == -1) return WRONG_ENTRY;
        substring = page.substring(start, end);
        String[] temp2 = substring.split(":");
        String message = temp2[1];

        //fundamentals
        List<HtmlListItem> fundamentals = p.getByXPath("//li");
        Map<String, String> keyData = new HashMap<>();
        for(HtmlListItem l : fundamentals) {
            List<HtmlSpan> s = l.getByXPath("span");
            if(s.size() == 2) {
                String attributeName = s.get(0).asText();
                String value = s.get(1).asText();
                keyData.put(attributeName, value);
            }
        }

        //exchange market
        HtmlDivision div = p.getFirstByXPath("//div[@class='st_3BauJpd']");
        System.out.println(div.asText());


        Entry newEntry = new Entry(followers, sentiment, message, keyData);
        System.out.println(newEntry.toString());
        return newEntry;
    }

}
