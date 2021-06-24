package com.company;

public class Variables {

    //this file was made, because I worked on this from my pc, mac and used it on my pi, so I made a file for the file paths
    //this way you only have to choose the device you are working on once

    public static final String PCTICKERS = "";
    public static final String PCDEBUG = "";
    public static final String PCDATABASE = "";
    public static final String PCEXCEL = "";

    public static final String MACTICKERS = "";
    public static final String MACDEBUG = "";
    public final static String MACDATABASE = "";
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
