package com.company;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Finwiz implements Runnable{

    private static final String BASE = "https://finviz.com/quote.ashx?t=";

    private final FINWIZ type;
    private final WebClient client;
    private final List<String> symbols;

    //this way you can reach the result once it ran
    public List<Ticker> tickers = new ArrayList<>();

    public Finwiz(FINWIZ type, List<String> symbols) {
        this.type = type;
        this.symbols = symbols;
        this.client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
    }

    public enum FINWIZ{
        OVERVIEW, BRIEF
    }

    public void run() {
        try {
            for(String s : symbols) {
                HtmlPage p = client.getPage(BASE + s);
                if(p.getWebResponse().getStatusCode() == 404 || p == null) {
                    System.out.println("404 error with link " + BASE + s);
                } else {
                    switch (type) {
                        case OVERVIEW -> parseOverview(p);
                        case BRIEF -> parseBrief(p);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error getting the page " + e);
        }

    }

    private void parseOverview(HtmlPage p){
        HtmlTable table = p.getFirstByXPath("//table[@class='snapshot-table2']");

        if(table == null) {
            System.out.println("No table found on the website " + p.getUrl());
            return;
        }

        int y = table.getRowCount();
        //StringBuilder s = new StringBuilder();
        //s.append("\n" + p.getUrl() + "\n");

        for(int i = 0; i < y; i++) {
            HashMap<String, String> data = new HashMap<>();
            String key = "";
            String value = "";
            for(HtmlTableCell c : table.getRow(i).getCells()){
                if(c.getAttribute("class").equals("snapshot-td2")){
                    HtmlBold br = c.getFirstByXPath(".//b");
                    //s.append(" : " + br.asText() + "\n");
                    value = br.asText();
                    data.put(key, value);
                    key = value = "";
                } else {
                    //s.append(c.asText());
                    key = c.asText();
                }
            }
            tickers.add(new Ticker(data));
        }


        //s.append("\n");
        //System.out.println(s.toString());
    }

    private void parseBrief(HtmlPage p){

    }
}
