package com.oldking.myapp;

import java.io.Serializable;

/**
 * Created by OldKing on 2018/5/31 0031.
 */

public class Blog implements Serializable {

    private String name;

    public Blog(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
