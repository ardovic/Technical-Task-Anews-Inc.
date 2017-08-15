package com.serjardovic.testapp2;

import java.util.ArrayList;

class ImageDataInfo {

    private ImageData imageData;

    public ImageDataInfo() {
        imageData = new ImageData(new ArrayList<String>(), 0, 1);
    }

    ImageData getImageData() {
        return imageData;
    }

    void setImageData(ImageData imageData) {
        this.imageData = imageData;
    }

}
