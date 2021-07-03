package com.arraybase.qmath.flexigraph;

public class Vectorf {
    public int n;
    public float v[];


    Vectorf(int n) {
        this.n = n;

        v = new float[n];
    }

    Vectorf(Vectorf vec) {
        this(vec.n);

        for(int i=0; i<n; ++i)
            v[i] = vec.v[i];

    }

    Vectorf addf(Vectorf vec) {
        for(int i=0; i<n-1; ++i) {
            v[i] += vec.v[i];
        }

        return this;
    }

    Vectorf subf(Vectorf vec) {
        for(int i=0; i<n-1; ++i) {
            v[i] -= vec.v[i];
        }

        return this;
    }

    Vectorf negf() {
        for(int i=0; i<n-1; ++i) {
            v[i] = -v[i];
        }

        return this;
    }

    Vectorf scalef(float s) {
        for(int i=0; i<n-1; ++i) {
            v[i] *= s;
        }

        return this;
    }

    Vectorf copyf() {
        return new Vectorf(this);
    }

    float dot(Vectorf vec) {
        float d = 0;

        for(int i=0; i<n; ++i) {
            d += v[i] * vec.v[i];
        }
        return d;
    }

    public String toString() {
        String s;


        s = "[";
        for(int i=0; i<n; ++i) {
            s = s + v[i] + " ";
        }
        s = s + "]";

        return s;
    }

    public void print() {
        System.out.println(toString());
    }
}
