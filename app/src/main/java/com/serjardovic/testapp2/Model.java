package com.serjardovic.testapp2;

import java.util.ArrayList;
import java.util.List;

class Model {

    private ImageData imageData;

    Model() {
        imageData = new ImageData(new ArrayList<String>(), 0, 1);
    }

    public ImageData getImageData() {
        return imageData;
    }

    public void setImageData(ImageData imageData) {
        this.imageData = imageData;
    }


}
