
package com.arraybase.qmath.flexigraph;

public class Matrix4x4 {
    float[][] v;

    public Matrix4x4() {
        v = new float[4][4];
        for(int i=0; i<4; ++i) {
            for(int j=0; j<4; ++j)
                if (i == j) v[i][j] = 1;
                else        v[i][j] = 0;
        }
    }


    public Matrix4x4 copy() {
        Matrix4x4 m = new Matrix4x4();

        for(int i=0; i<4; ++i) {
            for(int j=0; j<4; ++j) {
                m.v[i][j] = v[i][j];
            }
        }

        return m;
    }

    public Matrix4x4 mul(Matrix4x4 m1) {
        float mr[][] = new float[4][4];

        for(int i=0; i<4; ++i) {
            for(int j=0; j<4; ++j) {
                mr[i][j] = 0;

                for(int k=0; k <4; ++k) {
                    mr[i][j] += v[k][j] * m1.v[i][k];
                }
            }
        }
        for(int i=0; i<4; ++i)
            for(int j=0; j<4; ++j)
                v[i][j] = mr[i][j];

        return this;
    }


    public Matrix4x4 scale(float s) {
        for(int i=0; i<3; ++i)
            for(int j=0; j<4; ++j)
                v[i][j] = v[i][j] * s;

        return this;
    }

    public Matrix4x4 moveold(Vector4 tv) {
        for(int i=0; i<3; ++i)
                v[i][3] = v[i][3] + tv.v[i];

        return this;
    }

    public Matrix4x4 move(float x, float y, float z) {
        Matrix4x4 m = new Matrix4x4();

        m.v[0][3] = x;
        m.v[1][3] = y;
        m.v[2][3] = z;

        this.mul(m);

        return this;
    }

    public Matrix4x4 move(Vector4 vec) {
        return move(vec.v[0], vec.v[1], vec.v[2]);
    }

    public Matrix4x4 invertx() {
        Matrix4x4 m = new Matrix4x4();

        m.v[0][0] = -1;

        return mul(m);
    }

    public Matrix4x4 inverty() {
        Matrix4x4 m = new Matrix4x4();

        m.v[1][1] = -1;

        return mul(m);
    }

    public Matrix4x4 invertz() {
        Matrix4x4 m = new Matrix4x4();

        m.v[2][2] = -1;

        return mul(m);
    }

    public Matrix4x4 rotatex(int a) {

        float cos, sin;

        sin = ITrig.sin(a);
        cos = ITrig.cos(a);

        return rotatex(sin, cos);
    }

    public Matrix4x4 rotatex(float sin, float cos) {
        Matrix4x4 m = new Matrix4x4();

        m.v[1][1] =  cos;
        m.v[2][2] =  cos;
        m.v[1][2] = -sin;
        m.v[2][1] =  sin;

        return mul(m);
    }

    public Matrix4x4 rotatey(int a) {
        float cos, sin;

        sin = ITrig.sin(a);
        cos = ITrig.cos(a);

        return rotatey(sin, cos);
    }

    public Matrix4x4 rotatey(float sin, float cos) {
        Matrix4x4 m = new Matrix4x4();

        m.v[0][0] =  cos;
        m.v[2][2] =  cos;
        m.v[0][2] =  sin;
        m.v[2][0] = -sin;

        return mul(m);
    }

    public Matrix4x4 rotatez(int a) {
        float cos, sin;

        sin = ITrig.sin(a);
        cos = ITrig.cos(a);

        return rotatez(sin, cos);
    }

    public Matrix4x4 rotatez(float sin, float cos) {
        Matrix4x4 m = new Matrix4x4();

        m.v[0][0] =  cos;
        m.v[1][1] =  cos;
        m.v[0][1] = -sin;
        m.v[1][0] =  sin;

        return mul(m);
    }

    /**
     * Rotate about the axis, which goes through the point, by the
     * specified angle.
     */
    public Matrix4x4 rotateAbout(Vector4 point, Vector4 axis, int angle) {
        float sinx, cosx;
        float siny, cosy;
        float mx, m;
        Matrix4x4 mat = new Matrix4x4();

        // Rotation about X into the X-Z plane.
        mx = (float)Math.sqrt(axis.v[1] * axis.v[1] + axis.v[2] * axis.v[2]);
        if (mx == 0) {
            sinx = 0;
            cosx = 1;
        } else {
            sinx = axis.v[1]/mx;
            cosx = axis.v[2]/mx;
        }

        // Rotation about Y into the Y-Z plane.
        m = (float)Math.sqrt(axis.v[0] * axis.v[0] + mx*mx);
        if (m == 0) {
            siny = 0;
            cosy = 1;
        } else {
            siny = -axis.v[0]/m;
            cosy =  mx/m;
        }

        mat.move(point.copy().neg());
        mat.rotatex(sinx, cosx);
        mat.rotatey(siny, cosy);
        mat.rotatez(angle);
        mat.rotatey(-siny, cosy);
        mat.rotatex(-sinx, cosx);
        mat.move(point);

        return mul(mat);
    }

    public void print() {
        String s;


        for (int j=0; j<4; ++j) {
            s = "[[";
            for(int i=0; i<4; ++i)
                s = s + v[i][j] + " ";

            System.out.println(s + "]]");
        }
        System.out.println("");
    }

}

