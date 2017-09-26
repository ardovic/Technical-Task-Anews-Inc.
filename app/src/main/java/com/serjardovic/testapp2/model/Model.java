package com.serjardovic.testapp2.model;

import com.serjardovic.testapp2.model.images.ImageInfo;
import com.serjardovic.testapp2.model.images.PageInfo;

public class Model {

    public final PageInfo pageInfo;
    public final ImageInfo imageInfo;

    public Model() {
        pageInfo = new PageInfo();
        imageInfo = new ImageInfo();
    }
}
