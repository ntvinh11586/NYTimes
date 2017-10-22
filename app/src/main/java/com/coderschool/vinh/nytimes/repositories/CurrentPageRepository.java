package com.coderschool.vinh.nytimes.repositories;

public interface CurrentPageRepository {
    int getCurrentPage();
    void moveToNextPage();
    void resetCurrentPage();
}
