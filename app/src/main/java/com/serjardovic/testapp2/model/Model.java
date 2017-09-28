package com.serjardovic.testapp2.model;

import com.serjardovic.testapp2.model.images.PageInfo;

public class Model {

    private final PageInfo pageInfo;

    public Model() {
        pageInfo = new PageInfo();
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }
}
