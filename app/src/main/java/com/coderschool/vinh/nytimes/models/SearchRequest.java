package com.coderschool.vinh.nytimes.models;

import java.util.HashMap;
import java.util.Map;

public class SearchRequest {
    private int page;
    private String searchQuery;
    private Filter searchFilter;

    public SearchRequest() {
        page = 0;
        searchQuery = "";
        searchFilter = null;
    }

    public SearchRequest(int page, String searchQuery, Filter searchFilter) {
        this.page = page;
        this.searchQuery = searchQuery;
        this.searchFilter = searchFilter;
    }

    public Map<String, String> getParam() {
        Map<String, String> map = new HashMap<>();
        map.put("page", String.valueOf(page));

        if (!searchQuery.equals("")) {
            map.put("q", searchQuery);
        }

        if (searchFilter != null) {
            String day = searchFilter.day >= 10 ?
                    String.valueOf(searchFilter.day) :
                    "0" + String.valueOf(searchFilter.day);
            String month = searchFilter.month >= 10 ?
                    String.valueOf(searchFilter.month) :
                    "0" + String.valueOf(searchFilter.month);
            String year = String.valueOf(searchFilter.year);
            map.put("begin_date", year + month + day);

            if (searchFilter.sortOrder.equals("Newest")) {
                map.put("sort", "newest");
            } else if (searchFilter.sortOrder.equals("Oldest")) {
                map.put("sort", "oldest");
            }
            // TODO: 27/10/17 Fix change API
//            String newDesk = "news_desk:(";
//            if (searchFilter.isArts == 1) {
//                newDesk += "\"Arts\"";
//            }
//            if (searchFilter.isFashionStyle == 1) {
//                if (!newDesk.equals("news_desk:(")) {
//                    newDesk += "%20\"Fashion & Style\"";
//                } else {
//                    newDesk += "\"Fashion & Style\"";
//                }
//            }
//            if (searchFilter.isSports == 1) {
//                if (!newDesk.equals("news_desk:(")) {
//                    newDesk += "%20\"Sports\"";
//                } else {
//                    newDesk += "\"Sports\"";
//                }
//            }
//            newDesk += ")";
//            if (newDesk.equals("news_desk:()")) {
//                map.put("fq", newDesk);
//            }
        }

        return map;
    }

    public Filter getSearchFilter() {
        return searchFilter;
    }

    public void setSearchFilter(Filter searchFilter) {
        this.searchFilter = searchFilter;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
