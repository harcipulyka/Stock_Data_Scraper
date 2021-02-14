package com.company;

import java.util.Map;

public class Entry {
    Pair follower;
    Pair sentiment;
    Pair message;
    Pair low52;
    Pair high52;
    String marketCap;
    String volume;

    public Entry(String follower, String sentiment, String message, Map<String, String> keyData) {
        if(follower.equals("NaN")){
            this.follower = this.sentiment = this.message = this.low52 = this.high52 = Pair.WRONG_PAIR;
            this.marketCap = this.volume = "NaN";
        } else {
            this.follower = parseFollower(follower);
            this.sentiment = parseDouble(sentiment);
            this.message = parseDouble(message);

            String low52 = keyData.get("52wk Low");
            String high52 = keyData.get("52wk High");
            String volume = keyData.get("Volume");
            String marketCap = keyData.get("Mkt Cap");

            this.low52 = low52 == null ? Pair.WRONG_PAIR : parseDouble(low52);
            this.high52 = high52 == null ? Pair.WRONG_PAIR : parseDouble(high52);
            this.volume = volume == null ? "NaN" : volume;
            this.marketCap = marketCap == null ? "Nan" : marketCap;
        }
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("\nFollowers: " + follower.getString());
        s.append("\nSentiment: " + sentiment.getString());
        s.append("\nMessage: " + message.getString());
        s.append("\nMarket Cap: " + marketCap);
        s.append("\nVolume: " + volume);
        s.append("\nYearly low -> high: " + low52 .getString()+ " -> " + high52.getString());
        return s.toString();
    }

    private Pair parseFollower(String follower) {
        String s = follower.replaceAll(",", "");
        Integer i = Integer.parseInt(s);
        return new Pair(s, i);
    }

    private Pair parseDouble(String trend) {
        Double d = Double.parseDouble(trend);
        return new Pair(trend, d);
    }
}
