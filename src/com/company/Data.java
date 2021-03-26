package com.company;

public class Data {
    String ticker;
    boolean found;
    int trending;
    float trendingScore;
    float msgVolume;
    int followers;
    float sentiment;

    public Data(String ticker, boolean found, int trending, float trendingScore, float msgVolume, int followers, float sentiment) {
        this.ticker = ticker;
        this.found = found;
        this.trending = trending;
        this.trendingScore = trendingScore;
        this.msgVolume = msgVolume;
        this.followers = followers;
        this.sentiment = sentiment;
    }

    public String toCSV() {
        StringBuilder s = new StringBuilder();
        s.append("," + ticker);
        s.append("," + found);
        s.append("," + trending);
        s.append("," + trendingScore);
        s.append("," + msgVolume);
        s.append("," + followers);
        s.append("," + sentiment);
        return s.toString();
    }
}
