package com.coderschool.vinh.nytimes.models;

@Parcel
    public static class Multimedia {

        @SerializedName("url")
        String url;

        public String getUrl() {
            return url;
        }
    }