package com.company;

public class Data {
    String ticker;
    int trending;
    float trendingScore;
    float msgVolume;
    int followers;
    float sentiment;

    public Data(String ticker, int trending, float trendingScore, float msgVolume, int followers, float sentiment) {
        this.ticker = ticker;
        this.trending = trending;
        this.trendingScore = trendingScore;
        this.msgVolume = msgVolume;
        this.followers = followers;
        this.sentiment = sentiment;
    }
}
