package com.arraybase.qmath.flexigraph;


public class Vector4 extends Vectorf {
    public Vector4() {
        super(4);
        v[0] = v[1] = v[2] = 0;
        v[3] = 1;
    }

    public Vector4(Vector4 vec) {
        super(vec);
    }

    public Vector4(float x, float y, float z, float w) {
        super(4);
        v[0] = x;
        v[1] = y;
        v[2] = z;
        v[3] = w;
    }

    public Vector4 add(Vector4 vec) {
        return (Vector4)super.addf(vec);
    }

    public Vector4 sub(Vector4 vec) {
        return (Vector4)super.subf(vec);
    }

    public Vector4 neg() {
        return (Vector4)super.negf();
    }

    public Vector4 scale(float s) {
        return (Vector4)super.scalef(s);
    }

    public Vector4 copy() {
        return new Vector4(this);
    }

    public float magnitude() {
        double m;

        m = Math.sqrt(v[0]*v[0] + v[1]*v[1] + v[2]*v[2]);

        return (float)m;
    }


    public float dot(Vector4 vec) {
        return super.dot(vec);
    }

    public Vector4 cross(Vector4 vec) {
        Vector4 r = new Vector4();

        r.v[0] = v[1] * vec.v[2] - v[2] * vec.v[1];
        r.v[1] = v[2] * vec.v[0] - v[0] * vec.v[2];
        r.v[2] = v[0] * vec.v[1] - v[1] * vec.v[0];

        for(int i=0; i<4; ++i)
            v[i] = r.v[i];

        return this;
    }

    public Vector4 mul(Matrix4x4 m) {
        float nv[] = new float[4];

        for(int i=0; i<4; ++i) {
            nv[i] = 0;
            for(int j=0; j<4; ++j) {
                nv[i] = nv[i] + v[j] * m.v[i][j];
            }
        }
        for(int i=0; i<4; ++i)
            v[i] = nv[i];

        return this;
    }
}

