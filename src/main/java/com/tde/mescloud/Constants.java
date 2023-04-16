package com.tde.mescloud;

public class Constants {

    private int version = 1;
    public static String CELEBRATION_TIME = "Celebration Time!! YEAH!";

    public String getVersion() {
        return "The actual version is 1.0." + version++ + " MES";
    }
}
