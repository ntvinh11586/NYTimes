package com.coderschool.vinh.nytimes.models;

@Parcel
    public static class Headline {

        @SerializedName("main")
        String main;

        public String getHeadline() {
            return main;
        }
    }