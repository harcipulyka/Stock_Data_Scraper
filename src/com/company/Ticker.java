package com.company;

import java.util.HashMap;
import java.util.Map;

public class Ticker {
    HashMap<String, String> data;

    public Ticker(HashMap<String, String> data) {
        this.data = data;
    }

    private enum Exchange {
        NYSE, NASDAQ
    }

    private enum assetType {
        STOCK, ETF
    }
}
