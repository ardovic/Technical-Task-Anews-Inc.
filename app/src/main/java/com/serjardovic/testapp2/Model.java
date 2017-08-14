package com.serjardovic.testapp2;

import java.util.ArrayList;
import java.util.List;

class Model {

    private List<SinglePostResponse> SinglePostResponseList;

    Model() {
        SinglePostResponseList = new ArrayList<>();
    }

    List<SinglePostResponse> getSinglePostResponseList() {
        return SinglePostResponseList;
    }

}
