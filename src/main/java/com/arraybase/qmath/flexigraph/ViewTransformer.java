package com.arraybase.qmath.flexigraph;


public class ViewTransformer {
    Matrix4x4 viewMatrix;               // View matrix.
    float screenDistance;               // Distance from eye to screen.
    float screenHeight;                 // Height of screen.
    float k;                            // -screenDistance/screenHeight * h2.
    int w2, h2;                         // Display half width and half height.


    public ViewTransformer(Matrix4x4 m, int w2, int h2) {
        viewMatrix = m.copy();
        this.w2 = w2;
        this.h2 = h2;
    }

    public void setScreenDistanceAndHeight(float d, float h) {
        screenDistance = d;
        screenHeight = h;

        k = -screenDistance / screenHeight * h2;
    }

    public Vector4 transform(Vector4 vin) {

        /*
         * World to eye coordinate transformation
         */

        Vector4 vout = vin.copy().mul(viewMatrix);


        /*
         * Perspective transformation
         */
        float z = vout.v[2];
        if (z == 0.0f) z = 1.0f;

        vout.v[0] = vout.v[0] / z * k;
        vout.v[1] = vout.v[1] / z * k;

        return vout;
    }

    public IntVector2 project(Vector4 p) {
        return new IntVector2((int)(p.v[0]) + w2, h2 - (int)(p.v[1]));
    }
}
