package com.serjardovic.testapp2.model.images.dto;

import java.util.ArrayList;
import java.util.List;

public class PageData {
    private ArrayList<String> images = new ArrayList<>();
    private int currentPage;
    private int nextPage;


    public ArrayList<String> getImages() {
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
        setNextPage(data.getNextPage());
        setCurrentPage(data.getCurrentPage());
        addImages(data.getImages());
    }

    public void changeImageName(String oldName, String newName) {
        if(images.contains(oldName)) {
            for(int i = 0; i < images.size(); i++) {
                if(images.get(i).equals(oldName)) {
                    images.set(i, newName);
                }
            }
        }
    }

    public boolean hasNextPage() {
        return getNextPage() >= 0;
    }
}
