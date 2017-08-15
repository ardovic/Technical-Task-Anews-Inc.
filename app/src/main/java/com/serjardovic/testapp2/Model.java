package com.serjardovic.testapp2;

class Model {

    private ImageDataInfo imageDataInfo;

    Model() {
        imageDataInfo = new ImageDataInfo();
    }

    public ImageDataInfo getImageDataInfo() {
        return imageDataInfo;
    }

    public void setImageDataInfo (ImageDataInfo imageDataInfo) {
        this.imageDataInfo = imageDataInfo;
    }

}
