package com.company;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Finwiz implements Runnable{

    private static final String BASE = "https://finviz.com/quote.ashx?t=";

    private final WebClient client;
    private final List<String> symbols;

    //this way you can reach the result once it ran
    public List<Ticker> tickers = new ArrayList<>();

    public Finwiz(List<String> symbols) {
        this.symbols = symbols;
        this.client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
    }

    public void run() {
        try {
            for(String s : symbols) {
                HtmlPage p = client.getPage(BASE + s);
                if(p.getWebResponse().getStatusCode() == 404 || p == null) {
                    System.out.println("404 error with link " + BASE + s);
                } else {
                    parseData(p);
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
                } else { ;
                    key = c.asText();
                }
            }
            tickers.add(new Ticker(data));
        }
    }

    private void parseData(HtmlPage p){
        HtmlTable table = p.getFirstByXPath("//table[@class='snapshot-table2']");

        if(table == null) {
            System.err.println("No table found on the website " + p.getUrl());
            return;
        }

        int y = table.getRowCount();
        HashMap<String, String> data = new HashMap<>();

        for(int i = 0; i < y; i++) {
            String key = "If you can see this there is a problem";
            for(HtmlTableCell c : table.getRow(i).getCells()){
                if(c.getAttribute("class").equals("snapshot-td2")){
                    HtmlBold br = c.getFirstByXPath(".//b");
                    String value = br.asText();
                    data.put(key, value);
                    key = "If you can see this there is a problem";
                } else {
                    key = c.asText();
                }
            }
        }

        //this is for the sector and industry
        HtmlAnchor industryA = p.getFirstByXPath("/html/body/div[4]/div/table[1]/tbody/tr/td/table[1]/tbody/tr[3]/td/a[1]");
        HtmlAnchor sectorA = p.getFirstByXPath("/html/body/div[4]/div/table[1]/tbody/tr/td/table[1]/tbody/tr[3]/td/a[2]");
        try {
            String industry = industryA.asText();
            String sector = sectorA.asText();
            data.put("Industry", industry);
            data.put("Sector", sector);
        } catch (NullPointerException e) {
            System.err.println("Nullpointer exception in parsing the sector and the industry");
            data.put("Industry", "NaN");
            data.put("Sector", "NaN");
        }

        //this is for the full name
        HtmlBold b = p.getFirstByXPath("/html/body/div[4]/div/table[1]/tbody/tr/td/table[1]/tbody/tr[2]/td/a/b");
        try{
            String fullName = b.asText();
            data.put("Full Name", fullName);
        } catch (NullPointerException e) {
            System.err.println("Nullpointer exception in parsing the sector and the industry");
            data.put("Full Name", "NaN");
        }

        this.tickers.add(new Ticker(data));
    }
}
