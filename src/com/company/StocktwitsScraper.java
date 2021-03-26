package com.company;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.util.ArrayList;
import java.util.List;


public class StocktwitsScraper implements Runnable {

    final static String BASE = "https://stocktwits.com/symbol/";
    public static Integer numberOfNot = 0;

    final WebClient client;
    final List<String> tickers;
    List<Data> result = new ArrayList<>();

    public StocktwitsScraper(List<String> tickers) {
        client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
        this.tickers = tickers;
    }

    private HtmlPage getPage (String ticker) {
        HtmlPage page;
        try {
            page = client.getPage(BASE + ticker);
            return page;
        }catch(Exception e){
            System.out.println("Error with the link " + ticker + " : " + e);
            return null;
        }
    }

    public List<Data> parsePages() {
        List<Data> entries = new ArrayList<>();

        for (String s : tickers) {
            HtmlPage p = getPage(s);
            if(p.getWebResponse().getStatusCode() == 404 || p == null) {
                System.out.println("The website returned 404 with the ticker " + s);
                entries.add(new Data(s, false, Variables.undefinedInteger, Variables.undefinedFloat, Variables.undefinedFloat, Variables.undefinedInteger, Variables.undefinedFloat));
            } else {
                Data newEntry = getDataJson(p, s);
                entries.add(newEntry);
            }
        }

        return entries;
    }

    private Data getDataJson(HtmlPage p, String ticker) {
        String pageString = p.asXml();
        int startJson = pageString.indexOf("window.INITIAL_STATE = ");
        int endJson = pageString.indexOf(";\n    window.JOBS_STATE");
        String jsonString = pageString.substring((startJson + 22), endJson);
        JsonObject companyData;
        try {
            JsonObject json = Json.parse(jsonString).asObject();
            companyData = json.get("stocks").asObject().get("inventory").asObject().get(ticker).asObject();
        } catch (NullPointerException e) {
            System.out.println("Error getting the json object for the " + numberOfNot  + "th time");
            numberOfNot++;
            return new Data(ticker, false, Variables.undefinedInteger, Variables.undefinedFloat, Variables.undefinedFloat, Variables.undefinedInteger, Variables.undefinedFloat);
        }
        JsonValue _foundCompany = companyData.get("notFound");
        JsonValue _trending = companyData.get("trending");
        JsonValue _trendingScore = companyData.get("trendingScore");
        JsonValue _msgVolume = companyData.get("volumeChange");
        JsonValue _sentiment = companyData.get("sentimentChange");
        JsonValue _followers = companyData.get("watchlistCount");

        boolean foundCompany; //true if found data, false if didnt
        int trending; //0 i found this variables and was true, 1 if found but was false, aand 2 if it didn't found it
        float trendingScore; //trending score is the trending score variable, if it found that variable, if didn't then its set to variables.undefinedFloat
        float msgVolume; //same as trending score, if finds it then it sets to that, if it doesnt find it, then the basic variable gets assigned to it
        float sentiment; //same as above two
        int followers;

        if(_trending != null) {
            if(_trending.asBoolean()) trending = 0;
            else trending = 1;
        } else trending = 2;

        if(_foundCompany != null) foundCompany = _foundCompany.asBoolean();
        else foundCompany = true;

        if(_trendingScore != null) trendingScore = _trendingScore.asFloat();
        else trendingScore = Variables.undefinedFloat;

        if(_msgVolume != null) msgVolume = _msgVolume.asFloat();
        else msgVolume = Variables.undefinedFloat;

        if(_sentiment != null) sentiment = _sentiment.asFloat();
        else sentiment = Variables.undefinedFloat;

        if(_followers != null) followers = _followers.asInt();
        else followers = Variables.undefinedInteger;

        return new Data(ticker, foundCompany, trending, trendingScore, msgVolume, followers, sentiment);
    }

    public void run() {
        result = parsePages();
    }

}
