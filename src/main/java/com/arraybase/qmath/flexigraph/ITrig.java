package com.arraybase.qmath.flexigraph;

class ITrig {
    static float sin(int a) {
        float da = (float)(a * Math.PI/180.);

        return (float) Math.sin(da);
    }

    static float cos(int a) {
        float da = (float)(a * Math.PI/180.);

        return (float) Math.cos(da);
    }
}
