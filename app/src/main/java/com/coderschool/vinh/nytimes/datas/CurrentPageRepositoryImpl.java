package com.coderschool.vinh.nytimes.datas;

import com.coderschool.vinh.nytimes.repositories.CurrentPageRepository;

public class CurrentPageRepositoryImpl implements CurrentPageRepository {
    private int page;

    public CurrentPageRepositoryImpl() {
        page = 0;
    }

    @Override
    public int getCurrentPage() {
        return page;
    }

    @Override
    public synchronized void moveToNextPage() {
        page++;
    }

    @Override
    public void resetCurrentPage() {
        page = 0;
    }
}
