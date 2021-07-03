package com.arraybase.qmath.flexigraph;


public class Vector3 extends Vectorf {
    public Vector3() {
        super(3);
    }

    public Vector3(float x, float y, float z) {
        super(3);

        v[0] = x;
        v[1] = y;
        v[2] = z;
    }
}
