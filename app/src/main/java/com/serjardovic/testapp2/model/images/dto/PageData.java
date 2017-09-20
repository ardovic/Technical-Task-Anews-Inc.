package com.serjardovic.testapp2.model.images.dto;

import java.util.ArrayList;
import java.util.List;

public class PageData {
    private List<String> images = new ArrayList<>();
    private int currentPage;
    private int nextPage;


    public List<String> getImages() {
        return images;
    }

    public void addImages(List<String> images) {
        this.images.addAll(images);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }


}
