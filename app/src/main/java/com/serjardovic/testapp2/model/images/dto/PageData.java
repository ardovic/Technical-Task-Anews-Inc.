package com.serjardovic.testapp2.model.images.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PageData {

    @SerializedName("images")
    @Expose
    private List<String> images = null;
    @SerializedName("next_page")
    @Expose
    private int nextPage;
    @SerializedName("current_page")
    @Expose
    private int currentPage;


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

    public void updateData(PageData data) {
        if(data.getNextPage() != 0) {
            setNextPage(data.getNextPage());
        } else {
            setNextPage(-1);
        }

        setCurrentPage(data.getCurrentPage());
        addImages(data.getImages());
    }

    public boolean hasNextPage() {
        return getNextPage() >= 0;
    }
}
