package com.coderschool.vinh.nytimes.models;

import org.parceler.Parcel;

import java.util.Calendar;
import java.util.List;

@Parcel
public class Filter {
    public Calendar date;
    public List<SearchCategory> categories;

    public int day;
    public int month;
    public int year;

    public String sortOrder;

    public int isArts;
    public int isFashionStyle;
    public int isSports;

    public Filter() {
    }

    public Filter(Calendar date, String order, List<SearchCategory> categories) {
        this.date = date;
        this.sortOrder = order;
        this.categories = categories;
    }

    public Filter(int day, int month, int year, String sortOrder, int isArts, int isFashionStyle, int isSports) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.sortOrder = sortOrder;
        this.isArts = isArts;
        this.isFashionStyle = isFashionStyle;
        this.isSports = isSports;
    }
}

