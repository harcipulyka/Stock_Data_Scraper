package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListEntry {
    String ticker;
    List<Integer> followers;
    List<Double> sentiment;
    List<Double> message;
    List<Integer> volume;

    public ListEntry (List<Entry> entries, String ticker) {
        this.ticker = ticker;

        followers = entries
                .stream()
                .map(x -> x.follower.getInteger())
                .filter(x -> x != Integer.MIN_VALUE)
                .collect(Collectors.toList());

        sentiment = entries
                .stream()
                .map(x -> x.sentiment.getDouble())
                .filter(x -> x != Double.MIN_VALUE)
                .collect(Collectors.toList());

        message = entries
                .stream()
                .map(x -> x.message.getDouble())
                .filter(x -> x != Double.MIN_VALUE)
                .collect(Collectors.toList());

        volume = entries
                .stream()
                .map(x -> parseVolume(x.volume))
                .filter(x -> x != Integer.MIN_VALUE)
                .collect(Collectors.toList());
    }

    private Integer parseVolume(String volume) {
        return 0;
    }

    public Double differenceBetweenLastAndFirst (List<Integer> data) {
        if(data.size() == 0){
            return 0.0;
        }

        Integer first = data.get(0);
        Integer last = data.get(data.size() - 1);

        Double difference = (double)last - (double)first;
        return 100 * (difference / (double) first);
    }
}
