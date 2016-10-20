package com.coderschool.vinh.nytimes;

import org.parceler.Parcel;

/**
 * Created by Vinh on 10/20/2016.
 */

@Parcel
public class Filter {
    public int day;
    public int month;
    public int year;

    public String sortOrder;

    public int isArts;
    public int isFashionStyle;
    public int isSports;

    public Filter() {
        isArts = 0;
        isFashionStyle = 0;
        isSports = 0;
    }

    public Filter(int day, int month, int year,
                  String sortOrder,
                  int isArts, int isFashionStyle, int isSports){
        this.day = day;
        this.month = month;
        this.year = year;
        this.sortOrder = sortOrder;
        this.isArts = isArts;
        this.isFashionStyle = isFashionStyle;
        this.isSports = isSports;
    }
}

