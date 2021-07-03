package testing.grounds;

import flanagan.analysis.RegressionFunction;
import flanagan.math.Fmath;

class Amersham2 implements RegressionFunction {
    Amersham2() {
    }

    public double function(double[] var1, double[] var2) {
        return this.calcResponse(var1[0], var1[1], var1[2], var1[3], var2[0], var1[4]);
    }

    public double calcResponse(double var1, double var3, double var5, double var7, double var9, double var11) {
        double var13 = var1 + var3 + var9;
        double var15 = 2.0D * var5 * (var3 - var7) / var3;
        double var17 = var13 + var5 + Math.sqrt(Fmath.square(var13 - var5) + 4.0D * var1 * var5);
        double var19 = var11 * (var15 / var17 + var7 / var3);
        return var19;
    }
}
