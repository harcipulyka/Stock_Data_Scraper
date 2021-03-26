package com.company;

public class Variables {
    public static final int THREADCOUNT = 10;

    public static final String PCTICKERS = "C:\\Users\\balazs\\Downloads\\6000-x.txt";
    //public static final String PCTICKERS = "C:\\Users\\balazs\\Desktop\\20.txt";
    public static final String PCDEBUG = "C:\\Users\\balazs\\Downloads\\1002.txt";
    public static final String PCDATABASE = "";
    public static final String PCEXCEL = "";

    public static final String MACTICKERS = "/Users/raczbalazs/Downloads/tickers.txt";
    public static final String MACDEBUG = "/Users/raczbalazs/Downloads/1002.txt";
    public final static String MACDATABASE = "/Users/raczbalazs/Downloads/database.txt";
    public final static String MACEXCEL = "";

    public final static String PITICKERS = "";
    public final static String PIDEBUG = "";
    public final static String PIDATABASE = "";
    public final static String PIEXCEL = "";

    public final static String DAWTICKERS = "";
    public final static String DAWDEBUG = "";
    public final static String DAWDATABASE = "";
    public final static String DAWEXCEL = "";

    public final static float undefinedFloat = 69.420f;
    public final static String undefinedString = "undefinedString";

    public final Version v;

    public Variables(Version v) {
        this.v= v;
    }

    public enum Version{
        PI, PC, MAC, DAW
    }

    public String getTickerPath() {
        return switch (v){
            case PC -> PCTICKERS;
            case PI -> PITICKERS;
            case MAC -> MACTICKERS;
            case DAW -> DAWTICKERS;
        };
    }

    public String getDebugPath() {
        return switch (v){
            case PC -> PCDEBUG;
            case PI -> PIDEBUG;
            case MAC -> MACDEBUG;
            case DAW -> DAWDEBUG;
        };
    }

    public String getDatabasePath() {
        return switch (v){
            case PC -> PCDATABASE;
            case PI -> PIDATABASE;
            case MAC -> MACDATABASE;
            case DAW -> DAWDATABASE;
        };
    }

    public String getExcelPath() {
        return switch (v){
            case PC -> PCEXCEL;
            case PI -> PIEXCEL;
            case MAC -> MACEXCEL;
            case DAW -> DAWEXCEL;
        };
    }
}
