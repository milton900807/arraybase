package testing.grounds;

import flanagan.analysis.RegressionFunction3;
import flanagan.math.Fmath;

class AmershamDual implements RegressionFunction3 {
    private double[][] xErrors = (double[][])null;
    private double[] yErrors = null;

    AmershamDual() {
    }

    public double[] function(double[] var1, double[] var2, int var3) {
        double[] var4 = new double[]{this.calcResponse(var1[0], var1[1], var1[2], var1[3], var2[0], var1[4]), 0.0D};
        if (var3 >= 0) {
            var4[1] = this.calcSquareWeight(var1[0], var1[1], var1[2], var1[3], var2[0], var1[4], this.xErrors[0][var3], this.yErrors[var3]);
        }

        return var4;
    }

    public double calcResponse(double var1, double var3, double var5, double var7, double var9, double var11) {
        double var13 = var1 + var3 + var9;
        double var15 = 2.0D * var5 * (var3 - var7) / var3;
        double var17 = var13 + var5 + Math.sqrt(Fmath.square(var13 - var5) + 4.0D * var1 * var5);
        double var19 = var11 * (var15 / var17 + var7 / var3);
        return var19;
    }

    public double calcSquareWeight(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15) {
        double var17 = 2.0D * var5 * (var3 - var7) / var3;
        double var19 = var1 + var3 + var9;
        double var21 = Math.sqrt(Fmath.square(var19 - var5) + 4.0D * var1 * var5);
        double var23 = 1.0D + (var19 - var5) / var21;
        double var25 = var11 * var17 * var23;
        double var27 = var19 + var5 + var21;
        var27 *= var27;
        double var29 = var25 / var27;
        return var15 * var15 + var29 * var29 * var13 * var13;
    }

    public void setXerrors(double[][] var1) {
        this.xErrors = var1;
    }

    public void setYerrors(double[] var1) {
        this.yErrors = var1;
    }
}
