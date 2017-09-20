package com.serjardovic.testapp2.model;

import com.serjardovic.testapp2.model.images.ImageDataInfo;
import com.serjardovic.testapp2.model.images.PageInfo;

public class Model {

    public final ImageDataInfo imageDataInfo;
    public final PageInfo pageInfo;


    public Model() {
        imageDataInfo = new ImageDataInfo();
        pageInfo = new PageInfo();
    }


}
