package com.serjardovic.testapp2.model.images.dto;

public class PageWrapper {

    private PageData pageData;

    private String error;

    public PageData getPageData() {
        return pageData;
    }

    public String getError() {
        return error;
    }

    public PageWrapper(PageData pageData) {
        this.pageData = pageData;
    }

    public PageWrapper(String error) {
        this.error = error;
    }
}
