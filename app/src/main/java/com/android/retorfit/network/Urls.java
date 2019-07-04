package com.android.retorfit.network;

public class Urls {
    public static final String URL = "http://47.110.47.83:7060/api/";

    public static String registerUrl() {
        return URL + "user/register";
    }
    public static String loginUrl() {
        return URL + "user/login";
    }
}
