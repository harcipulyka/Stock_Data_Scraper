package com.company;

public class Variables {
    public static final int THREADCOUNT = 10;

    public static final String PCTICKERS = "C:\\Users\\balazs\\Downloads\\final_tickerlist.txt";
    public static final String PCDEBUG = "C:\\Users\\balazs\\Downloads\\debug.txt";
    public static final String PCDATABASE = "C:\\Users\\balazs\\Downloads\\final_database.txt";
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
    public final static int undefinedInteger = 696969;
    public final static String undefinedString = "undefinedString";

    public final Version v;

    public Variables(Version v) {
        this.v= v;
    }

    public enum Version{
        PI, PC, MAC, DAW
    }

    public String getTickerPath() {
        switch (v) {
            case PC:
                return PCTICKERS;
            case PI:
                return PITICKERS;
            case MAC:
                return MACTICKERS;
            case DAW:
                return DAWTICKERS;
            default:
                throw new IllegalArgumentException();
        }
    }

    public String getDebugPath() {
        switch (v) {
            case PC:
                return PCDEBUG;
            case PI:
                return PIDEBUG;
            case MAC:
                return MACDEBUG;
            case DAW:
                return DAWDEBUG;
            default:
                throw new IllegalArgumentException();
        }
    }

    public String getDatabasePath() {
        switch (v) {
            case PC:
                return PCDATABASE;
            case PI:
                return PIDATABASE;
            case MAC:
                return MACDATABASE;
            case DAW:
                return DAWDATABASE;
            default:
                throw new IllegalArgumentException();
        }
    }

    public String getExcelPath() {
        switch (v) {
            case PC:
                return PCEXCEL;
            case PI:
                return PIEXCEL;
            case MAC:
                return MACEXCEL;
            case DAW:
                return DAWEXCEL;
            default:
                throw new IllegalArgumentException();
        }
    }
}
