package com.company;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
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

    public Entry(String follower,String sentiment, String message, String low52, String high52, String marketCap, String volume){
        this.follower = parseFollower(follower);
        this.sentiment = parseDouble(sentiment);
        this.message = parseDouble(message);
        this.low52 = parseDouble(low52);
        this.high52 = parseDouble(high52);
        this.marketCap = marketCap;
        this.volume = volume;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("\nFollowers: " + follower.getString())
        .append("\nSentiment: " + sentiment.getString())
        .append("\nMessage: " + message.getString())
        .append("\nMarket Cap: " + marketCap)
        .append("\nVolume: " + volume)
        .append("\nYearly low -> high: " + low52 .getString()+ " -> " + high52.getString());
        return s.toString();
    }

    public String toCSV() {
        StringBuilder s = new StringBuilder();
        s.append("," + follower.getString());
        s.append("," + sentiment.getString());
        s.append("," + message.getString());
        s.append("," + low52.getString());
        s.append("," + high52.getString());
        s.append("," + marketCap);
        s.append("," + volume);
        return s.toString();
    }

    public static String toExcel(List<Entry> daysOfTickerFromThisStock, String ticker) {
        StringBuilder s = new StringBuilder();
        int i = 0;
        for(Entry e : daysOfTickerFromThisStock) {
            s.append(ticker + ";");
            s.append(i + ";");
            s.append(e.follower.getString() + ";");
            s.append(e.sentiment.getString() + ";");
            s.append(e.message.getString() + ";");
            s.append(parseVolume(e.volume));
            s.append("\r\n");
            i++;
        }
        return s.toString();
    }

    private Pair parseFollower(String follower) {
        if(follower.equals("NaN")) return Pair.WRONG_PAIR;
        String s = follower.replaceAll(",", "");
        Integer i = Integer.parseInt(s);
        return new Pair(s, i);
    }

    private Pair parseDouble(String trend) {
        try {
            if(trend.contains("k")){
                trend = trend.replace(".", "");
                trend = trend.replace("k", "");
                trend += "0";
            }
            Double d = Double.parseDouble(trend);
            return new Pair(trend, d);
        } catch (NumberFormatException e) {
            return Pair.WRONG_PAIR;
        }
    }

    private static Double parseVolume(String volume) {
        if(volume.equals("NaN")) return 0.0;
        if(volume.contains("m")) return Double.parseDouble(volume.replace("m", "")) * 1000;
        if(volume.contains("k")) return Double.parseDouble(volume.replace("k", ""));
        return 0.0;
    }
}
