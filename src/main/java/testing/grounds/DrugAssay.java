package testing.grounds;


import flanagan.analysis.Regression;
import flanagan.analysis.Stat;
import flanagan.interpolation.CubicInterpolation;
import flanagan.interpolation.CubicSpline;
import flanagan.interpolation.LinearInterpolation;
import flanagan.io.Db;
import flanagan.io.FileChooser;
import flanagan.io.FileInput;
import flanagan.io.FileOutput;
import flanagan.math.ArrayMaths;
import flanagan.math.Conv;
import flanagan.math.Fmath;
import flanagan.physchem.ImmunoChemistry;
import flanagan.plot.PlotGraph;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class DrugAssay extends Regression {
    private double[] analyteConcns = null;
    private double[] log10AnalyteConcns = null;
    private double[] logeAnalyteConcns = null;
    private boolean analyteEntered = false;
    private int analyteConcnFlag = 0;
    private int nAnalyteConcns = 0;
    private double[] responses = null;
    private double[] log10Responses = null;
    private double[] logeResponses = null;
    private boolean responsesEntered = false;
    private int nResponses = 0;
    private int responsesFlag = 0;
    private boolean responsesPlot = true;
    private double responsesMax = 0.0D;
    private double responsesMin = 0.0D;
    private double responsesRange = 0.0D;
    private int nWeights = 0;
    private double weightsMean = 0.0D;
    private double weightsSD = 0.0D;
    private int weightOption = 0;
    private boolean setDataOneDone = false;
    private double[] interpolationConcns = null;
    private double[] calculatedResponses = null;
    private String[] propagatedErrors = null;
    private int nInterp = 1000;
    private CubicSpline interp = null;
    private LinearInterpolation linterp = null;
    private CubicSpline errorp = null;
    private CubicSpline cs = null;
    private CubicInterpolation ci = null;
    private LinearInterpolation li = null;
    private String titleZero = null;
    private String titleOne = null;
    private String filename = "ImmunoAssayOutput.txt";
    private String dataFilename = null;
    private boolean dataRead = false;
    private double interpAnalyteStart = 0.0D;
    private double interpAnalyteEnd = 0.0D;
    private double interpResponseStart = 0.0D;
    private double interpResponseEnd = 0.0D;
    private int interpStartIndex = 0;
    private int interpEndIndex = 0;
    private boolean ambigCheck = false;
    private boolean curveDirection = true;
    private double workingResponseMin = 0.0D;
    private double workingResponseMax = 0.0D;
    private double minimumAerror = 0.0D;
    private double maximumAerror = 0.0D;
    private double meanAerror = 0.0D;
    private double sdAerror = 0.0D;
    private int nTurningPoints = 0;
    private int[] turnIndices = null;
    private int nWorking = 0;
    private int polyDegree = 0;
    private int bestPolyDegree = 0;
    private int polyNterms = 0;
    private String compFilename = "ImmunoAssayComparison.txt";
    private int resultFlag = -1;
    private double significance = 0.05D;
    private double sampleResponse = 0.0D;
    private double sampleConcn = 0.0D;
    private double sampleError = 0.0D;
    private int nPlot = 0;
    private int plotOptions = 0;
    private boolean supressPlot = false;
    private int nMethods = 15;
    private String[] methodNames;
    private int[] methodIndices;
    private int methodUsed;
    private boolean sampleErrorFlag;
    private boolean amershamFlag1;
    private double bottom;
    private double top;
    private boolean compWindow;
    private String[] outliers;
    private double residualsMean;
    private double residualsSD;
    private double confidenceLevel;
    private double[] critSize;
    private double[] critValuesOne;
    private double[] critValuesFive;
    private CubicSpline critValues;
    private double anscombeC;
    private boolean outlierFlag;
    private boolean degSet;
    private boolean nTermsSet;
    private boolean fourBotTopSet;
    private boolean fiveBotTopSet;

    public DrugAssay() {
        this.methodNames = new String[this.nMethods];
        this.methodIndices = new int[this.nMethods];
        this.methodUsed = 8;
        this.sampleErrorFlag = true;
        this.amershamFlag1 = true;
        this.bottom = 0.0D;
        this.top = 0.0D;
        this.compWindow = false;
        this.outliers = null;
        this.residualsMean = 0.0D;
        this.residualsSD = 0.0D;
        this.confidenceLevel = 0.95D;
        this.critSize = new double[]{3.0D, 4.0D, 5.0D, 6.0D, 7.0D, 8.0D, 9.0D, 10.0D, 12.0D, 14.0D, 15.0D, 16.0D, 18.0D, 20.0D, 30.0D, 40.0D, 50.0D, 60.0D, 100.0D, 120.0D};
        this.critValuesOne = new double[]{1.15D, 1.49D, 1.75D, 1.94D, 2.1D, 2.22D, 2.32D, 2.41D, 2.55D, 2.66D, 2.71D, 2.75D, 2.82D, 2.88D, 3.1D, 3.24D, 3.34D, 3.41D, 3.6D, 3.66D};
        this.critValuesFive = new double[]{1.15D, 1.46D, 1.67D, 1.82D, 1.94D, 2.03D, 2.11D, 2.18D, 2.29D, 2.37D, 2.41D, 2.44D, 2.5D, 2.56D, 2.74D, 2.87D, 2.96D, 3.03D, 3.21D, 3.27D};
        this.critValues = null;
        this.anscombeC = 0.0D;
        this.outlierFlag = false;
        this.degSet = false;
        this.nTermsSet = false;
        this.fourBotTopSet = false;
        this.fiveBotTopSet = false;
        this.titleZero = "Program ImmunoAssay";
//        super.supressErrorMessages = true;
        super.trueErrors = false;
        this.methodList();
    }

    public DrugAssay(String var1) {
        this.methodNames = new String[this.nMethods];
        this.methodIndices = new int[this.nMethods];
        this.methodUsed = 8;
        this.sampleErrorFlag = true;
        this.amershamFlag1 = true;
        this.bottom = 0.0D;
        this.top = 0.0D;
        this.compWindow = false;
        this.outliers = null;
        this.residualsMean = 0.0D;
        this.residualsSD = 0.0D;
        this.confidenceLevel = 0.95D;
        this.critSize = new double[]{3.0D, 4.0D, 5.0D, 6.0D, 7.0D, 8.0D, 9.0D, 10.0D, 12.0D, 14.0D, 15.0D, 16.0D, 18.0D, 20.0D, 30.0D, 40.0D, 50.0D, 60.0D, 100.0D, 120.0D};
        this.critValuesOne = new double[]{1.15D, 1.49D, 1.75D, 1.94D, 2.1D, 2.22D, 2.32D, 2.41D, 2.55D, 2.66D, 2.71D, 2.75D, 2.82D, 2.88D, 3.1D, 3.24D, 3.34D, 3.41D, 3.6D, 3.66D};
        this.critValuesFive = new double[]{1.15D, 1.46D, 1.67D, 1.82D, 1.94D, 2.03D, 2.11D, 2.18D, 2.29D, 2.37D, 2.41D, 2.44D, 2.5D, 2.56D, 2.74D, 2.87D, 2.96D, 3.03D, 3.21D, 3.27D};
        this.critValues = null;
        this.anscombeC = 0.0D;
        this.outlierFlag = false;
        this.degSet = false;
        this.nTermsSet = false;
        this.fourBotTopSet = false;
        this.fiveBotTopSet = false;
        this.titleZero = "Program ImmunoAssay: " + var1;
//        super.supressErrorMessages = true;
        super.trueErrors = false;
        this.methodList();
    }

    public DrugAssay(double[] var1, double[] var2) {
        this.methodNames = new String[this.nMethods];
        this.methodIndices = new int[this.nMethods];
        this.methodUsed = 8;
        this.sampleErrorFlag = true;
        this.amershamFlag1 = true;
        this.bottom = 0.0D;
        this.top = 0.0D;
        this.compWindow = false;
        this.outliers = null;
        this.residualsMean = 0.0D;
        this.residualsSD = 0.0D;
        this.confidenceLevel = 0.95D;
        this.critSize = new double[]{3.0D, 4.0D, 5.0D, 6.0D, 7.0D, 8.0D, 9.0D, 10.0D, 12.0D, 14.0D, 15.0D, 16.0D, 18.0D, 20.0D, 30.0D, 40.0D, 50.0D, 60.0D, 100.0D, 120.0D};
        this.critValuesOne = new double[]{1.15D, 1.49D, 1.75D, 1.94D, 2.1D, 2.22D, 2.32D, 2.41D, 2.55D, 2.66D, 2.71D, 2.75D, 2.82D, 2.88D, 3.1D, 3.24D, 3.34D, 3.41D, 3.6D, 3.66D};
        this.critValuesFive = new double[]{1.15D, 1.46D, 1.67D, 1.82D, 1.94D, 2.03D, 2.11D, 2.18D, 2.29D, 2.37D, 2.41D, 2.44D, 2.5D, 2.56D, 2.74D, 2.87D, 2.96D, 3.03D, 3.21D, 3.27D};
        this.critValues = null;
        this.anscombeC = 0.0D;
        this.outlierFlag = false;
        this.degSet = false;
        this.nTermsSet = false;
        this.fourBotTopSet = false;
        this.fiveBotTopSet = false;
        this.titleZero = "Program ImmunoAssay";
        this.nAnalyteConcns = var1.length;
        this.analyteConcns = Conv.copy(var1);
        this.analyteConcnFlag = 0;
        this.analyteEntered = true;
        this.nResponses = var2.length;
        this.responses = Conv.copy(var2);
        this.responsesFlag = 0;
        this.responsesEntered = true;
        super.xErrorsEntered = false;
        super.yErrorsEntered = false;
//        super.supressErrorMessages = true;
        super.trueErrors = false;
        this.methodList();
    }

    public DrugAssay(double[] var1, double[] var2, double[] var3) {
        this.methodNames = new String[this.nMethods];
        this.methodIndices = new int[this.nMethods];
        this.methodUsed = 8;
        this.sampleErrorFlag = true;
        this.amershamFlag1 = true;
        this.bottom = 0.0D;
        this.top = 0.0D;
        this.compWindow = false;
        this.outliers = null;
        this.residualsMean = 0.0D;
        this.residualsSD = 0.0D;
        this.confidenceLevel = 0.95D;
        this.critSize = new double[]{3.0D, 4.0D, 5.0D, 6.0D, 7.0D, 8.0D, 9.0D, 10.0D, 12.0D, 14.0D, 15.0D, 16.0D, 18.0D, 20.0D, 30.0D, 40.0D, 50.0D, 60.0D, 100.0D, 120.0D};
        this.critValuesOne = new double[]{1.15D, 1.49D, 1.75D, 1.94D, 2.1D, 2.22D, 2.32D, 2.41D, 2.55D, 2.66D, 2.71D, 2.75D, 2.82D, 2.88D, 3.1D, 3.24D, 3.34D, 3.41D, 3.6D, 3.66D};
        this.critValuesFive = new double[]{1.15D, 1.46D, 1.67D, 1.82D, 1.94D, 2.03D, 2.11D, 2.18D, 2.29D, 2.37D, 2.41D, 2.44D, 2.5D, 2.56D, 2.74D, 2.87D, 2.96D, 3.03D, 3.21D, 3.27D};
        this.critValues = null;
        this.anscombeC = 0.0D;
        this.outlierFlag = false;
        this.degSet = false;
        this.nTermsSet = false;
        this.fourBotTopSet = false;
        this.fiveBotTopSet = false;
        this.titleZero = "Program ImmunoAssay";
        this.nAnalyteConcns = var1.length;
        this.analyteConcns = Conv.copy(var1);
        this.analyteConcnFlag = 0;
        this.analyteEntered = true;
        this.nResponses = var2.length;
        this.responses = Conv.copy(var2);
        this.responsesFlag = 0;
        this.responsesEntered = true;
        this.nWeights = var3.length;
        super.yErrors = super.checkForZeroWeights(var3);
        if (super.weightOpt) {
            super.weightFlag = 1;
        }

        super.xErrorsEntered = false;
        super.yErrorsEntered = true;
//        super.supressErrorMessages = true;
        super.trueErrors = false;
        this.methodList();
    }

    public DrugAssay(double[] var1, double[] var2, double[] var3, double[] var4) {
        this.methodNames = new String[this.nMethods];
        this.methodIndices = new int[this.nMethods];
        this.methodUsed = 8;
        this.sampleErrorFlag = true;
        this.amershamFlag1 = true;
        this.bottom = 0.0D;
        this.top = 0.0D;
        this.compWindow = false;
        this.outliers = null;
        this.residualsMean = 0.0D;
        this.residualsSD = 0.0D;
        this.confidenceLevel = 0.95D;
        this.critSize = new double[]{3.0D, 4.0D, 5.0D, 6.0D, 7.0D, 8.0D, 9.0D, 10.0D, 12.0D, 14.0D, 15.0D, 16.0D, 18.0D, 20.0D, 30.0D, 40.0D, 50.0D, 60.0D, 100.0D, 120.0D};
        this.critValuesOne = new double[]{1.15D, 1.49D, 1.75D, 1.94D, 2.1D, 2.22D, 2.32D, 2.41D, 2.55D, 2.66D, 2.71D, 2.75D, 2.82D, 2.88D, 3.1D, 3.24D, 3.34D, 3.41D, 3.6D, 3.66D};
        this.critValuesFive = new double[]{1.15D, 1.46D, 1.67D, 1.82D, 1.94D, 2.03D, 2.11D, 2.18D, 2.29D, 2.37D, 2.41D, 2.44D, 2.5D, 2.56D, 2.74D, 2.87D, 2.96D, 3.03D, 3.21D, 3.27D};
        this.critValues = null;
        this.anscombeC = 0.0D;
        this.outlierFlag = false;
        this.degSet = false;
        this.nTermsSet = false;
        this.fourBotTopSet = false;
        this.fiveBotTopSet = false;
        this.titleZero = "Program ImmunoAssay";
        this.nAnalyteConcns = var1.length;
        this.analyteConcns = Conv.copy(var1);
        this.analyteConcnFlag = 0;
        this.analyteEntered = true;
        this.nResponses = var2.length;
        this.responses = Conv.copy(var2);
        this.responsesFlag = 0;
        this.responsesEntered = true;
        this.nWeights = var4.length;
        super.yErrors = var4;
        double[][] var5 = new double[1][this.nWeights];
        var5[0] = var3;
        super.xErrors = var5;
        super.jointZeroCheck(var5, var4);
        if (super.weightOpt) {
            super.weightFlag = 1;
        }

        super.xErrorsEntered = true;
        super.yErrorsEntered = true;
        super.weightOpt = true;
        super.weightFlag = 1;
//        super.supressErrorMessages = true;
        super.trueErrors = true;
        this.methodList();
    }

    private void methodList() {
        this.methodNames[0] = "CubicSpline";
        this.methodIndices[0] = 0;
        this.methodNames[1] = "Five parameter logistic function";
        this.methodIndices[1] = 13;
        this.methodNames[2] = "Five parameter logistic function (top and bottom fixed)";
        this.methodIndices[2] = 6;
        this.methodNames[3] = "Four parameter logistic function";
        this.methodIndices[3] = 5;
        this.methodNames[4] = "Four parameter logistic function (top and bottom fixed)";
        this.methodIndices[4] = 7;
        this.methodNames[5] = "Best fit polynomial";
        this.methodIndices[5] = 8;
        this.methodNames[6] = "Polynomial of user supplied degree ";
        this.methodIndices[6] = 9;
        this.methodNames[7] = "Non-integer polynomial";
        this.methodIndices[7] = 3;
        this.methodNames[8] = "Sigmoid threshold function";
        this.methodIndices[8] = 1;
        this.methodNames[9] = "Sips sigmoid function";
        this.methodIndices[9] = 10;
        this.methodNames[10] = "Shifted rectangular hyperbola";
        this.methodIndices[10] = 11;
        this.methodNames[11] = "Rectangular hyperbola";
        this.methodIndices[11] = 12;
        this.methodNames[12] = "Amersham equation";
        this.methodIndices[12] = 4;
        this.methodNames[13] = "Cubic interpolation";
        this.methodIndices[13] = 2;
        this.methodNames[14] = "Linear interpolation";
        this.methodIndices[14] = 14;
    }

    public void enterAnalyteConcns(double[] var1) {
        this.setDataOneDone = false;
        this.nAnalyteConcns = var1.length;
        this.analyteConcns = Conv.copy(var1);
        this.analyteConcnFlag = 0;
        this.analyteEntered = true;
    }

    public void enterAnalyteConcnsAsLog10(double[] var1) {
        this.setDataOneDone = false;
        this.nAnalyteConcns = var1.length;
        this.log10AnalyteConcns = Conv.copy(var1);
        this.analyteConcns = this.antiLog10(var1);
        this.analyteConcnFlag = 1;
        this.analyteEntered = true;
    }

    public void enterAnalyteConcnsAsLogE(double[] var1) {
        this.setDataOneDone = false;
        this.nAnalyteConcns = var1.length;
        this.logeAnalyteConcns = Conv.copy(var1);
        this.analyteConcns = this.antiLoge(var1);
        this.analyteConcnFlag = 2;
        this.analyteEntered = true;
    }

    public void enterResponses(double[] var1) {
        this.setDataOneDone = false;
        this.nResponses = var1.length;
        this.responses = Conv.copy(var1);
        this.responsesFlag = 0;
        this.responsesEntered = true;
    }

    public void enterResponsesAsLog10(double[] var1) {
        this.setDataOneDone = false;
        this.nResponses = var1.length;
        this.log10Responses = Conv.copy(var1);
        this.responses = this.antiLog10(var1);
        this.responsesFlag = 1;
        this.responsesEntered = true;
    }

    public void enterResponsesAsLogE(double[] var1) {
        this.setDataOneDone = false;
        this.nResponses = var1.length;
        this.logeResponses = Conv.copy(var1);
        this.responses = this.antiLoge(var1);
        this.responsesFlag = 2;
        this.responsesEntered = true;
    }

    public void enterWeights(double[] var1) {
        this.setDataOneDone = false;
        this.nWeights = var1.length;
        super.yErrors = super.checkForZeroWeights(var1);
        super.yErrorsEntered = true;
        if (super.weightOpt) {
            super.weightFlag = 1;
        }

    }

    public void enterWeightsAslog10(double[] var1) {
        this.setDataOneDone = false;
        this.nWeights = var1.length;
        super.yErrors = this.antiLog10(super.checkForZeroWeights(var1));
        if (super.weightOpt) {
            super.weightFlag = 1;
        }

        super.yErrorsEntered = true;
    }

    public void enterWeightsAslogE(double[] var1) {
        this.setDataOneDone = false;
        this.nWeights = var1.length;
        super.yErrors = this.antiLoge(super.checkForZeroWeights(var1));
        if (super.weightOpt) {
            super.weightFlag = 1;
        }

        super.yErrorsEntered = true;
    }

    public void enterMultiplicativeWeights(double[] var1) {
        this.setDataOneDone = false;
        this.nWeights = var1.length;
        super.yErrors = Conv.copy(super.checkForZeroWeights(var1));

        for (int var2 = 0; var2 < this.nWeights; ++var2) {
            super.yErrors[var2] = 1.0D / Math.abs(super.yErrors[var2]);
        }

        if (super.weightOpt) {
            super.weightFlag = 1;
        }

        super.yErrorsEntered = true;
    }

    public void enterMultiplicativeWeightsAsLog10(double[] var1) {
        this.setDataOneDone = false;
        this.nWeights = var1.length;
        super.yErrors = this.antiLog10(super.checkForZeroWeights(Conv.copy(var1)));

        for (int var2 = 0; var2 < this.nWeights; ++var2) {
            super.yErrors[var2] = 1.0D / Math.abs(super.yErrors[var2]);
        }

        if (super.weightOpt) {
            super.weightFlag = 1;
        }

        super.yErrorsEntered = true;
    }

    public void enterMultiplicativeWeightsAsLogE(double[] var1) {
        this.setDataOneDone = false;
        this.nWeights = var1.length;
        super.yErrors = this.antiLoge(super.checkForZeroWeights(Conv.copy(var1)));

        for (int var2 = 0; var2 < this.nWeights; ++var2) {
            super.yErrors[var2] = 1.0D / Math.abs(super.yErrors[var2]);
        }

        if (super.weightOpt) {
            super.weightFlag = 1;
        }

        super.yErrorsEntered = true;
    }

    public void setWeightsAsResponses() {
        super.yErrorsEntered = false;
        this.weightOption = 1;
        if (this.nResponses > 0) {
            super.yErrors = new double[this.nResponses];

            for (int var1 = 0; var1 < this.nResponses; ++var1) {
                super.yErrors[var1] = Math.abs(this.responses[var1]);
                super.yErrorsEntered = true;
            }

            super.yErrors = super.checkForZeroWeights(super.yErrors);
            if (super.weightOpt) {
                super.weightFlag = 1;
            }

            super.yErrorsEntered = true;
        }

    }

    public void setWeightsAsSqrtResponses() {
        super.yErrorsEntered = false;
        this.weightOption = 2;
        if (this.nResponses > 0) {
            super.yErrors = new double[this.nResponses];

            for (int var1 = 0; var1 < this.nResponses; ++var1) {
                super.yErrors[var1] = Math.sqrt(Math.abs(this.responses[var1]));
            }

            super.yErrors = super.checkForZeroWeights(super.yErrors);
            if (super.weightOpt) {
                super.weightFlag = 1;
            }

            super.yErrorsEntered = true;
        }

    }

    public void enterTitle(String var1) {
        this.titleZero = var1;
    }

    public void readFromFile() {
        this.setDataOneDone = false;
        FileChooser var1 = new FileChooser();
        this.dataFilename = var1.selectFile();
//        this.read(var1);
    }

    public void readFromFile(String var1) {
        this.setDataOneDone = false;
        this.dataFilename = var1;
        FileInput var2 = new FileInput(var1);
        this.read(var2);
    }

    private int separatorPosition(String var1) {
        var1 = var1.trim();
        int var2 = var1.indexOf(58);
        if (var2 == -1) {
            var2 = var1.indexOf(59);
        }

        if (var2 == -1) {
            var2 = var1.indexOf(44);
        }

        if (var2 == -1) {
            var2 = var1.indexOf(9);
        }

        if (var2 == -1) {
            var1.indexOf(32);
        }

        return var2;
    }

    private void read(FileInput var1) {
        this.nAnalyteConcns = var1.numberOfLines() - 1;
        this.titleZero = var1.readLine();
        this.nResponses = this.nAnalyteConcns;
        this.analyteConcns = new double[this.nAnalyteConcns];
        this.responses = new double[this.nAnalyteConcns];
        super.yErrors = new double[this.nAnalyteConcns];
        int var2 = 0;
        String var3 = null;
        String var4 = null;
        boolean var5 = false;
        boolean var6 = true;
        boolean var7 = true;
        boolean var8 = true;

        for (int var9 = 0; var9 < this.nAnalyteConcns; ++var9) {
            var3 = var1.readLine().trim();
            int var11 = this.separatorPosition(var3);
            if (var11 == -1) {
                throw new IllegalArgumentException("Input file line " + (var9 + 1) + ": analyte concentration and response value required for all data points");
            }

            var4 = var3.substring(0, var11);
            this.analyteConcns[var9] = Double.parseDouble(var4);
            var3 = var3.substring(var11 + 1).trim();
            int var10 = var3.length();
            if (var10 < 1) {
                throw new IllegalArgumentException("Input file line " + (var9 + 1) + ": response value required for all data points");
            }

            int var12 = this.separatorPosition(var3);
            if (var12 == -1) {
                var4 = var3;
            } else {
                var4 = var3.substring(0, var12);
            }

            this.responses[var9] = Double.parseDouble(var4);
            if (var12 != -1) {
                var3 = var3.substring(var12 + 1).trim();
                var10 = var3.length();
                if (var10 > 0) {
                    int var13 = this.separatorPosition(var3);
                    if (var13 == -1) {
                        var4 = var3.trim();
                    } else {
                        var4 = var3.substring(0, var13).trim();
                    }

                    super.yErrors[var9] = Double.parseDouble(var4);
                    if (super.yErrors[var9] == 1.0D) {
                        ++var2;
                    }
                }
            }
        }

        this.analyteConcnFlag = 0;
        this.analyteEntered = true;
        this.responsesEntered = true;
        if (var2 != this.nAnalyteConcns) {
            this.nWeights = this.nAnalyteConcns;
            super.yErrors = super.checkForZeroWeights(super.yErrors);
            if (super.weightOpt) {
                super.weightFlag = 1;
            }

            super.yErrorsEntered = true;
        }

        this.dataRead = true;
    }

    public double[] getAnalyteConcns() {
        return this.analyteConcns;
    }

    public double[] getResponses() {
        return this.responses;
    }

    public String getTitle() {
        return this.titleZero;
    }

    public void cubicSpline() {
        if (this.nAnalyteConcns < 3) {
            throw new IllegalArgumentException("Method cubicSpline requres at least 3 data points; only " + this.nAnalyteConcns + " were supplied");
        } else {
            this.methodUsed = 0;
            this.sampleErrorFlag = false;
            this.titleOne = "Cubic spline ";
            if (!this.setDataOneDone) {
                this.setDataOne();
            }

            this.cs = new CubicSpline(this.analyteConcns, this.responses);

            for (int var1 = 0; var1 < this.nInterp; ++var1) {
                this.calculatedResponses[var1] = this.cs.interpolate(this.interpolationConcns[var1]);
            }

            if (!this.supressPlot) {
                this.plott();
            }

            this.curveCheck(this.methodIndices[this.methodUsed]);
        }
    }

    public void cubicInterpolation() {
        if (this.nAnalyteConcns < 2) {
            throw new IllegalArgumentException("Method cubicInterpolation requres at least 2 data points; only " + this.nAnalyteConcns + " were supplied");
        } else {
            this.methodUsed = 1;
            this.sampleErrorFlag = false;
            this.titleOne = "Cubic interpolation ";
            if (!this.setDataOneDone) {
                this.setDataOne();
            }

            this.ci = new CubicInterpolation(this.analyteConcns, this.responses, 0);

            for (int var1 = 0; var1 < this.nInterp; ++var1) {
                this.calculatedResponses[var1] = this.ci.interpolate(this.interpolationConcns[var1]);
            }

            if (!this.supressPlot) {
                this.plott();
            }

            this.curveCheck(this.methodIndices[this.methodUsed]);
        }
    }

    public void linearInterpolation() {
        if (this.nAnalyteConcns < 2) {
            throw new IllegalArgumentException("Method cubicInterpolation requres at least 2 data points; only " + this.nAnalyteConcns + " were supplied");
        } else {
            this.methodUsed = 14;
            this.sampleErrorFlag = false;
            this.titleOne = "Linear interpolation ";
            if (!this.setDataOneDone) {
                this.setDataOne();
            }

            this.li = new LinearInterpolation(this.analyteConcns, this.responses);

            for (int var1 = 0; var1 < this.nInterp; ++var1) {
                this.calculatedResponses[var1] = this.li.interpolate(this.interpolationConcns[var1]);
            }

            if (!this.supressPlot) {
                this.plott();
            }

            this.curveCheck(this.methodIndices[this.methodUsed]);
        }
    }

    public void polynomialFit(int var1) {
        if (this.nAnalyteConcns < var1 + 2) {
            throw new IllegalArgumentException("Method polynomialFit(" + var1 + ") requres at least " + (var1 + 2) + " data points; only " + this.nAnalyteConcns + " were supplied");
        } else {
            this.methodUsed = 2;
            this.sampleErrorFlag = true;
            this.degSet = true;
            this.polyDegree = var1;
            this.titleOne = "Polynomial fitting: r = c[0] + c[1].a +  c[1].a^2 + ... + c[n].a^n; degree (n) = " + var1;
            if (!this.setDataOneDone) {
                this.setDataOne();
            }

            super.polynomial(var1);

            for (int var2 = 0; var2 < this.nInterp; ++var2) {
                this.calculatedResponses[var2] = 0.0D;

                for (int var3 = 0; var3 <= var1; ++var3) {
                    this.calculatedResponses[var2] += super.best[var3] * Math.pow(this.interpolationConcns[var2], (double) var3);
                }
            }

            if (!this.supressPlot) {
                this.plott();
            }

            this.curveCheck(this.methodIndices[this.methodUsed]);
        }
    }

    public int bestPolynomialFit() {
        this.methodUsed = 3;
        this.sampleErrorFlag = true;
        this.titleOne = "Best polynomial fitting: r = c[0] + c[1].a +  c[1].a^2 + ... + c[n].a^n; best fit degree (n) = ";
        if (!this.setDataOneDone) {
            this.setDataOne();
        }

        ArrayList var1 = super.bestPolynomial();
        this.bestPolyDegree = ((Integer) var1.get(0)).intValue();
        this.titleOne = this.titleOne + " " + this.bestPolyDegree;

        for (int var2 = 0; var2 < this.nInterp; ++var2) {
            this.calculatedResponses[var2] = 0.0D;

            for (int var3 = 0; var3 <= this.bestPolyDegree; ++var3) {
                this.calculatedResponses[var2] += super.best[var3] * Math.pow(this.interpolationConcns[var2], (double) var3);
            }
        }

        if (!this.supressPlot) {
            this.plott();
        }

        this.curveCheck(this.methodIndices[this.methodUsed]);
        return super.bestPolynomialDegree;
    }

    public void nonIntegerPolynomialFit(int var1) {
        if (this.nAnalyteConcns < var1 + 1) {
            throw new IllegalArgumentException("Method nonIntegerPolynomial requres at least " + (var1 + 1) + " data points; only " + this.nAnalyteConcns + " were supplied");
        } else {
            this.methodUsed = 4;
            this.polyNterms = var1;
            this.nTermsSet = true;
            this.sampleErrorFlag = true;
            this.titleOne = "Non-integer polynomial fitting: r = c[0] + c[1].a^c[n] + c[2].a^c[n+1] + ... + c[n].a^c[2n-1]";
            if (!this.setDataOneDone) {
                this.setDataOne();
            }

            super.nonIntegerPolynomial(var1);

            for (int var2 = 0; var2 < this.nInterp; ++var2) {
                this.calculatedResponses[var2] = super.best[0];

                for (int var3 = 1; var3 < this.polyNterms; ++var3) {
                    this.calculatedResponses[var2] += super.best[var3] * Math.pow(this.interpolationConcns[var2], super.best[var1 + var3 - 1]);
                }
            }

            if (!this.supressPlot) {
                this.plott();
            }

            this.curveCheck(this.methodIndices[this.methodUsed]);
        }
    }

    public void nonIntegerPolynomialFit() {
        this.nonIntegerPolynomialFit(3);
    }

    public void sigmoidThresholdFit() {
        if (this.nAnalyteConcns < 4) {
            throw new IllegalArgumentException("Method sigmoidThresholdFit requres at least 4 data points; only " + this.nAnalyteConcns + " were supplied");
        } else {
            this.methodUsed = 5;
            this.sampleErrorFlag = true;
            this.titleOne = "Sigmoid threshold fitting: r = A/(1 + exp(-alpha(a - theta)))";
            if (!this.setDataOneDone) {
                this.setDataOne();
            }

            super.sigmoidThreshold();

            for (int var1 = 0; var1 < this.nInterp; ++var1) {
                this.calculatedResponses[var1] = super.best[2] / (1.0D + Math.exp(-super.best[0] * (this.interpolationConcns[var1] - super.best[1])));
            }

            if (!this.supressPlot) {
                this.plott();
            }

            this.curveCheck(this.methodIndices[this.methodUsed]);
        }
    }

    public void sipsSigmoidFit() {
        if (this.nAnalyteConcns < 4) {
            throw new IllegalArgumentException("Method sipsSigmoidFit requres at least 4 data points; only " + this.nAnalyteConcns + " were supplied");
        } else {
            this.methodUsed = 6;
            this.sampleErrorFlag = true;
            this.titleOne = "Sips sigmoid fitting: r = Aa^n/(theta^n + a^n)";
            if (!this.setDataOneDone) {
                this.setDataOne();
            }

            super.addConstraint(0, -1, 0.0D);
            super.sigmoidHillSips();
            super.removeConstraints();

            for (int var1 = 0; var1 < this.nInterp; ++var1) {
                this.calculatedResponses[var1] = super.best[2] * Math.pow(this.interpolationConcns[var1], super.best[1]) / (Math.pow(super.best[0], super.best[1]) + Math.pow(this.interpolationConcns[var1], super.best[1]));
            }

            if (!this.supressPlot) {
                this.plott();
            }

            this.curveCheck(this.methodIndices[this.methodUsed]);
        }
    }

    public void fourParameterLogisticFit() {
        if (this.nAnalyteConcns < 5) {
            throw new IllegalArgumentException("Method fourParameterLogisticFit requres at least 5 data points; only " + this.nAnalyteConcns + " were supplied");
        } else {
            this.methodUsed = 7;
            this.sampleErrorFlag = true;
            this.titleOne = "Four parameter logistic fitting: r = top + (bottom - top)/(1 + (a/C50)^HillSlope)";
            if (!this.setDataOneDone) {
                this.setDataOne();
            }

            super.addConstraint(2, -1, 0.0D);
            super.ec50();
            super.removeConstraints();

            for (int var1 = 0; var1 < this.nInterp; ++var1) {
                this.calculatedResponses[var1] = super.best[0] + (super.best[1] - super.best[0]) / (1.0D + Math.pow(this.interpolationConcns[var1] / super.best[2], super.best[3]));
            }

            if (!this.supressPlot) {
                this.plott();
            }

            this.curveCheck(this.methodIndices[this.methodUsed]);
        }
    }

    public void ec50Fit() {
        this.fourParameterLogisticFit();
    }

    public void fourParameterLogisticFit(double bottom, double top) {
        if (this.nAnalyteConcns < 3) {
            throw new IllegalArgumentException("Method fourParameterLogisticFit requres at least 3 data points; only " + this.nAnalyteConcns + " were supplied");
        } else {
            this.bottom = bottom;
            this.top = top;
            this.methodUsed = 13;
            this.fourBotTopSet = true;
            this.sampleErrorFlag = true;
            this.titleOne = "Four parameter logistic fitting: r = top + (bottom - top)/(1 + (a/C50)^HillSlope) [top and bottom fixed]";
            if (!this.setDataOneDone) {
                this.setDataOne();
            }

            super.addConstraint(0, -1, 0.0D);
            super.ec50(bottom, top);
            super.removeConstraints();

            for (int var5 = 0; var5 < this.nInterp; ++var5) {
//                 this.calculatedResponses[var5] = top + (bottom - top) / (1.0D + Math.pow(this.interpolationConcns[var5] / super.best[0], super.best[1]));

                this.calculatedResponses[var5] = top + (bottom - top) / (1.0D + Math.pow(this.interpolationConcns[var5]/ super.best[0], 1));
//                this.calculatedResponses[var5] = bottom + (bottom-top) / (1.0D + Math.log10((?this.best[0]-this.interpolationConcns[var5])*super.best[1]));
            }

            if (!this.supressPlot) {
                this.plotOptions = 1;
                this.plott();
            }

            this.curveCheck(this.methodIndices[this.methodUsed]);
        }
    }

    public void ec50Fit(double var1, double var3) {
        this.fourParameterLogisticFit(var1, var3);
    }

    public void fiveParameterLogisticFit() {
        if (this.nAnalyteConcns < 6) {
            throw new IllegalArgumentException("Method fiveParameterLogisticFit requres at least 6 data points; only " + this.nAnalyteConcns + " were supplied");
        } else {
            this.methodUsed = 8;
            this.sampleErrorFlag = true;
            this.titleOne = "Five parameter logistic fitting: r = top + (bottom - top)/((1 + (a/C50)^HillSlope)^asymm)";
            if (!this.setDataOneDone) {
                this.setDataOne();
            }

            super.addConstraint(2, -1, 0.0D);
            super.fiveParameterLogistic();
            super.removeConstraints();

            for (int var1 = 0; var1 < this.nInterp; ++var1) {
                this.calculatedResponses[var1] = super.best[0] + (super.best[1] - super.best[0]) / Math.pow(1.0D + Math.pow(this.interpolationConcns[var1] / super.best[2], super.best[3]), super.best[4]);
            }

            if (!this.supressPlot) {
                this.plott();
            }

            this.curveCheck(this.methodIndices[this.methodUsed]);
        }
    }

    public void fiveParameterLogisticFit(double var1, double var3) {
        if (this.nAnalyteConcns < 5) {
            throw new IllegalArgumentException("Method fiveParameterLogisticFit requres at least 5 data points; only " + this.nAnalyteConcns + " were supplied");
        } else {
            this.methodUsed = 13;
            this.sampleErrorFlag = true;
            this.bottom = var1;
            this.top = var3;
            this.fiveBotTopSet = true;
            this.titleOne = "Five parameter logistic fitting: r = top + (bottom - top)/((1 + (a/C50)^HillSlope)^asymm) [top and bottom fixed]";
            if (!this.setDataOneDone) {
                this.setDataOne();
            }

            super.addConstraint(0, -1, 0.0D);
            super.fiveParameterLogistic(var1, var3);
            super.removeConstraints();

            for (int var5 = 0; var5 < this.nInterp; ++var5) {
                this.calculatedResponses[var5] = var3 + (var1 - var3) / Math.pow(1.0D + Math.pow(this.interpolationConcns[var5] / super.best[0], super.best[1]), super.best[2]);
            }

            if (!this.supressPlot) {
                this.plott();
            }

            this.curveCheck(this.methodIndices[this.methodUsed]);
        }
    }

    public void shiftedRectangularHyperbolaFit() {
        if (this.nAnalyteConcns < 4) {
            throw new IllegalArgumentException("Method shiftedRectangularHyperbolaFit requres at least 4 data points; only " + this.nAnalyteConcns + " were supplied");
        } else {
            this.methodUsed = 9;
            this.sampleErrorFlag = true;
            this.titleOne = "Rectangular hyperbola fitting: r = A.a/(theta + a) + alpha)";
            if (!this.setDataOneDone) {
                this.setDataOne();
            }

            super.shiftedRectangularHyperbola();

            for (int var1 = 0; var1 < this.nInterp; ++var1) {
                this.calculatedResponses[var1] = this.best[2] * this.interpolationConcns[var1] / (this.best[0] + this.interpolationConcns[var1]) + this.best[1];
            }

            if (!this.supressPlot) {
                this.plott();
            }

            this.curveCheck(this.methodIndices[this.methodUsed]);
        }
    }

    public void rectangularHyperbolaFit() {
        if (this.nAnalyteConcns < 3) {
            throw new IllegalArgumentException("Method rectangularHyperbolaFit requres at least 3 data points; only " + this.nAnalyteConcns + " were supplied");
        } else {
            this.methodUsed = 10;
            this.sampleErrorFlag = true;
            this.titleOne = "Rectangular hyperbola fitting: r = A.a/(theta + a))";
            if (!this.setDataOneDone) {
                this.setDataOne();
            }

            super.rectangularHyperbola();

            for (int var1 = 0; var1 < this.nInterp; ++var1) {
                this.calculatedResponses[var1] = this.best[1] * this.interpolationConcns[var1] / (this.best[0] + this.interpolationConcns[var1]);
            }

            if (!this.supressPlot) {
                this.plott();
            }

            this.curveCheck(this.methodIndices[this.methodUsed]);
        }
    }

    public void amershamFit() {
        this.methodUsed = 11;
        if (!this.setDataOneDone) {
            this.setDataOne();
        }

        this.isCompetitive();
        this.amershamFlag1 = true;
        double[] var1 = new double[]{0.0D, 0.0D, 0.0D, 0.0D, 0.0D};
        this.fitAmersham(var1);
    }

    public void amershamFit(double[] var1) {
        this.methodUsed = 11;
        if (!this.setDataOneDone) {
            this.setDataOne();
        }

        this.isCompetitive();
        this.amershamFlag1 = false;
        this.fitAmersham(var1);
    }

    private void fitAmersham(double[] var1) {
        if (this.nAnalyteConcns < 6) {
            throw new IllegalArgumentException("Method amershamFit requres at least 6 data points; only " + this.nAnalyteConcns + " were supplied");
        } else {
            this.sampleErrorFlag = true;
            this.titleOne = "Amersham equation fitting: r = S(2.P(1-N/L)/(K+P+L+a+[(K-P+L+a)^2+4KP]^0.5)+N/L)";
            double[] var2 = new double[5];
            double[] var3 = new double[5];
            double var4 = 0.0D;
            double var6 = 0.0D;
            double var8 = 0.0D;
            double var10 = 0.0D;
            double var12 = this.responsesMax;
            int var15;
            int var17;
            if (this.amershamFlag1) {
                boolean var14 = false;

                for (var15 = 0; var15 < this.nAnalyteConcns; ++var15) {
                    if (this.responses[var15] < this.responsesMax / 2.0D) {
                        if (var15 != this.nAnalyteConcns - 1) {
                            var6 = (this.analyteConcns[var15] + this.analyteConcns[var15 + 1]) / 2.0D;
                        } else {
                            var6 = this.analyteConcns[var15];
                        }

                        var14 = true;
                    }

                    if (var14) {
                        break;
                    }
                }

                if (!var14) {
                    var6 = this.analyteConcns[this.nAnalyteConcns - 1];
                }

                var4 = var6 / 100.0D;
                var8 = var6 / 1000.0D;
                var2[0] = var4;
                var2[1] = var6;
                var2[2] = var8;
                var2[3] = var10;
                var2[4] = var12;

                for (var15 = 0; var15 < 5; ++var15) {
                    var3[var15] = 0.1D * var2[var15];
                }

                var3[3] = var8 / 100.0D;
            } else {
                var2[0] = var1[0];
                var2[1] = var1[1];
                var2[2] = var1[2];
                var2[3] = var1[3];
                var2[4] = var1[4];

                for (var17 = 0; var17 < 5; ++var17) {
                    var3[var17] = 0.1D * var2[var17];
                }

                if (var3[3] == 0.0D) {
                    var3[3] = var3[2] / 100.0D;
                }
            }

            for (var17 = 0; var17 < 5; ++var17) {
                super.addConstraint(var17, -1, 0.0D);
            }

            double[] var16;
            if (super.xErrorsEntered) {
                AmershamDual var18 = new AmershamDual();
                var18.setYerrors(super.yErrors);
                var18.setXerrors(super.xErrors);
                super.simplex(var18, var2, var3);
                super.removeConstraints();

                for (var15 = 0; var15 < this.nInterp; ++var15) {
                    var16 = new double[]{this.interpolationConcns[var15]};
                    this.calculatedResponses[var15] = var18.function(super.best, var16, -1)[0];
                }
            } else {
                Amersham2 var19 = new Amersham2();
                super.simplex(var19, var2, var3);
                super.removeConstraints();

                for (var15 = 0; var15 < this.nInterp; ++var15) {
                    var16 = new double[]{this.interpolationConcns[var15]};
                    this.calculatedResponses[var15] = var19.function(super.best, var16);
                }
            }

            if (!this.supressPlot) {
                this.plott();
            }

            this.curveCheck(this.methodIndices[this.methodUsed]);
        }
    }

    private void isCompetitive() {
        int var1 = 0;

        for (int var2 = 1; var2 < this.nAnalyteConcns; ++var2) {
            if (this.responses[var2 - 1] < this.responses[var2]) {
                ++var1;
            }

            if (var1 >= this.nAnalyteConcns / 2) {
                if (this.responses[this.nAnalyteConcns - 1] >= this.responses[0]) {
                    throw new IllegalArgumentException("The data appears incompatible with a competitive assay");
                }

                System.out.println("The data has been queried as that of a competitive assay but the fitting has not been aborted");
            }
        }

    }

    public void selectEquation() {
        ArrayList var1 = this.chooseEquation(0);
        int var2 = ((Integer) var1.get(0)).intValue();
        int var3 = ((Integer) var1.get(1)).intValue();
        double var4 = ((Double) var1.get(2)).doubleValue();
        double var6 = ((Double) var1.get(3)).doubleValue();
        switch (var2) {
            case 1:
                this.fiveParameterLogisticFit();
                break;
            case 2:
                this.fiveParameterLogisticFit(var4, var6);
                break;
            case 3:
                this.fourParameterLogisticFit();
                break;
            case 4:
                this.fourParameterLogisticFit(var4, var6);
                break;
            case 5:
                this.bestPolynomialFit();
                break;
            case 6:
                this.polynomialFit(var3);
                break;
            case 7:
                this.nonIntegerPolynomialFit(var3);
                break;
            case 8:
                this.sigmoidThresholdFit();
                break;
            case 9:
                this.sipsSigmoidFit();
                break;
            case 10:
                this.shiftedRectangularHyperbolaFit();
                break;
            case 11:
                this.rectangularHyperbolaFit();
                break;
            case 12:
//                this.amershamFit();
                break;
            case 13:
                this.cubicSpline();
                break;
            case 14:
                this.linearInterpolation();
        }

    }

    private ArrayList<Object> chooseEquation(int var1) {
        ArrayList var2 = new ArrayList();
        String var3 = null;
        String[] var4 = null;
        String[] var5 = null;
        switch (var1) {
            case 0:
                var3 = "Choose a fitting equation";
                var4 = new String[15];
                break;
            case 1:
                var3 = "Choose the first equation of the comparison";
                var4 = new String[13];
                break;
            case 2:
                var3 = "Choose the second equation of the comparison";
                var4 = new String[13];
        }

        var4[0] = "1.  Five paramater logistic equation";
        var4[1] = "2.  Five paramater logistic equation (top & bottom fixed)";
        var4[2] = "3.  Four paramater logistic equation";
        var4[3] = "4.  Four paramater logistic equation (top & bottom fixed)";
        var4[4] = "5.  Best fit polynomial";
        var4[5] = "6.  Polynomial of user supplied degree";
        var4[6] = "7.  Non-integer polynomial";
        var4[7] = "8.  Sigmoid threshold function";
        var4[8] = "9.  Sips sigmoid function";
        var4[9] = "10.  Shifted rectangular hyperbola";
        var4[10] = "11.  Rectangular hyperbola";
        var4[11] = "12.  Amersham mass action model";
        String[] var6;
        if (var1 == 0) {
            var4[12] = "13.  Cubic spline";
            var4[13] = "14.  Linear interpolation\n\n";
            var4[14] = "Click on the appropriate button below";
            var6 = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14"};
            var5 = var6;
        } else {
            var4[12] = "\nClick on the appropriate button below";
            var6 = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
            var5 = var6;
        }

        byte var13 = 0;
        int var7 = 1 + JOptionPane.showOptionDialog((Component) null, var4, var3, 1, 3, (Icon) null, var5, var5[var13]);
        var2.add(new Integer(var7));
        int var8 = 0;
        if (var7 == 6) {
            var8 = Db.readInt("enter polynomial degree");
        }

        if (var7 == 7) {
            var8 = Db.readInt("enter non-integer polynomial number of terms");
        }

        var2.add(new Integer(var8));
        double var9 = 0.0D;
        double var11 = 0.0D;
        if (var7 == 2) {
            var9 = Db.readDouble("Enter five parameter logistic fixed bottom value");
            var11 = Db.readDouble("Enter five parameter logistic fixed top value");
        }

        if (var7 == 4) {
            var9 = Db.readDouble("Enter four parameter logistic fixed bottom value");
            var11 = Db.readDouble("Enter four parameter logistic fixed top value");
        }

        var2.add(new Double(var9));
        var2.add(new Double(var11));
        return var2;
    }

    private double[] log10(double[] var1) {
        int var2 = var1.length;
        double[] var3 = new double[var2];
        this.nPlot = this.nAnalyteConcns;

        for (int var4 = 0; var4 < var2; ++var4) {
            if (var1[var4] == 0.0D) {
                var3[var4] = 0.0D / 0.0;
                --this.nPlot;
            } else {
                var3[var4] = Math.log10(var1[var4]);
            }
        }

        return var3;
    }

    private double[] loge(double[] var1) {
        int var2 = var1.length;
        double[] var3 = new double[var2];
        this.nPlot = 0;
        this.nPlot = this.nAnalyteConcns;

        for (int var4 = 0; var4 < var2; ++var4) {
            if (var1[var4] == 0.0D) {
                var3[var4] = 0.0D / 0.0;
                --this.nPlot;
            } else {
                var3[var4] = Math.log(var1[var4]);
            }
        }

        return var3;
    }

    private double[] antiLog10(double[] var1) {
        int var2 = var1.length;
        double[] var3 = new double[var2];

        for (int var4 = 0; var4 < var2; ++var4) {
            var3[var4] = Math.pow(10.0D, var1[var4]);
        }

        return var3;
    }

    private double[] antiLoge(double[] var1) {
        int var2 = var1.length;
        double[] var3 = new double[var2];

        for (int var4 = 0; var4 < var2; ++var4) {
            var3[var4] = Math.exp(var1[var4]);
        }

        return var3;
    }

    public void setData() {
        this.setDataOne();
    }

    private void setDataOne() {
        if (!this.analyteEntered) {
            throw new IllegalArgumentException("No analyte concentrations have been entered");
        } else if (!this.responsesEntered) {
            throw new IllegalArgumentException("No standard curve responses have been entered");
        } else if (this.nAnalyteConcns != this.nResponses) {
            throw new IllegalArgumentException("The number of analyte concentrations entered, " + this.nAnalyteConcns + ", must equal the number of standard curve responses entered, " + this.nResponses);
        } else {
            if (super.yErrorsEntered) {
                if (this.nAnalyteConcns != this.nWeights) {
                    throw new IllegalArgumentException("The number of analyte concentrations entered, " + this.nAnalyteConcns + ", must equal the number of standard curve weights entered, " + this.nWeights);
                }
            } else {
                super.weightOpt = false;
                super.weightFlag = 0;
                this.nWeights = this.nResponses;
                super.yErrors = new double[this.nWeights];

                for (int var1 = 0; var1 < this.nWeights; ++var1) {
                    super.yErrors[var1] = 1.0D;
                }
            }

            super.nData = this.nAnalyteConcns;
            super.nData0 = this.nAnalyteConcns;
            super.nXarrays = 1;
            super.nYarrays = 1;
            ArrayMaths var16 = new ArrayMaths(this.analyteConcns);
            var16 = var16.sort();
            int[] var2 = var16.originalIndices();
            double[] var3 = new double[this.nAnalyteConcns];
            var3 = Conv.copy(this.analyteConcns);

            int var4;
            for (var4 = 0; var4 < this.nAnalyteConcns; ++var4) {
                this.analyteConcns[var4] = var3[var2[var4]];
            }

            var3 = Conv.copy(this.responses);

            for (var4 = 0; var4 < this.nAnalyteConcns; ++var4) {
                this.responses[var4] = var3[var2[var4]];
            }

            if (super.yErrorsEntered) {
                var3 = Conv.copy(super.yErrors);

                for (var4 = 0; var4 < this.nAnalyteConcns; ++var4) {
                    super.yErrors[var4] = var3[var2[var4]];
                }
            }

            if (super.xErrorsEntered) {
                var3 = Conv.copy(super.xErrors[0]);

                for (var4 = 0; var4 < this.nAnalyteConcns; ++var4) {
                    super.xErrors[0][var4] = var3[var2[var4]];
                }
            }

            if (this.analyteConcnFlag == 1) {
                var3 = Conv.copy(this.log10AnalyteConcns);

                for (var4 = 0; var4 < this.nAnalyteConcns; ++var4) {
                    this.log10AnalyteConcns[var4] = var3[var2[var4]];
                }
            }

            if (this.analyteConcnFlag == 2) {
                var3 = Conv.copy(this.logeAnalyteConcns);

                for (var4 = 0; var4 < this.nAnalyteConcns; ++var4) {
                    this.logeAnalyteConcns[var4] = var3[var2[var4]];
                }
            }

            var4 = this.nAnalyteConcns;

            int var5;
            int var6;
            for (var5 = 0; var5 < this.nAnalyteConcns - 1; ++var5) {
                var6 = 1;
                int var7 = 0;
                new ArrayList();

                for (int var9 = var5 + 1; var9 < this.nAnalyteConcns; ++var9) {
                    if (this.analyteConcns[var5] == this.analyteConcns[var9]) {
                        ++var6;
                        var7 = var5;
                    }
                }

                if (var6 > 1) {
                    double var18 = 0.0D;
                    double var11 = 0.0D;
                    double var13 = 0.0D;

                    int var15;
                    for (var15 = var7; var15 < var7 + var6; ++var15) {
                        var18 += this.responses[var15];
                        if (super.yErrorsEntered) {
                            var11 += super.yErrors[var5] * super.yErrors[var5];
                        }

                        if (super.xErrorsEntered) {
                            var13 += super.xErrors[0][var5] * super.xErrors[0][var5];
                        }
                    }

                    this.responses[var7] = var18 / (double) var6;
                    if (super.yErrorsEntered) {
                        super.yErrors[var7] = Math.sqrt(var11) / (double) var6;
                    }

                    if (super.xErrorsEntered) {
                        super.xErrors[0][var7] = Math.sqrt(var13) / (double) var6;
                    }

                    for (var15 = var7 + 1; var15 < this.nAnalyteConcns - var6 + 1; ++var15) {
                        this.analyteConcns[var15] = this.analyteConcns[var15 + var6 - 1];
                        this.responses[var15] = this.responses[var15 + var6 - 1];
                        if (super.yErrorsEntered) {
                            super.yErrors[var15] = super.yErrors[var15 + var6 - 1];
                        }

                        if (super.xErrorsEntered) {
                            super.xErrors[0][var15] = super.xErrors[0][var15 + var6 - 1];
                        }

                        if (this.analyteConcnFlag == 1) {
                            this.log10AnalyteConcns[var15] = this.log10AnalyteConcns[var15 + var6 - 1];
                        }

                        if (this.analyteConcnFlag == 2) {
                            this.logeAnalyteConcns[var15] = this.logeAnalyteConcns[var15 + var6 - 1];
                        }
                    }

                    this.nAnalyteConcns = this.nAnalyteConcns - var6 + 1;
                }
            }

            if (this.nAnalyteConcns < var4) {
                double[] var17 = this.analyteConcns;
                this.analyteConcns = new double[this.nAnalyteConcns];

                for (var6 = 0; var6 < this.nAnalyteConcns; ++var6) {
                    this.analyteConcns[var6] = var17[var6];
                }

                var17 = this.responses;
                this.responses = new double[this.nAnalyteConcns];

                for (var6 = 0; var6 < this.nAnalyteConcns; ++var6) {
                    this.responses[var6] = var17[var6];
                }

                var17 = super.yErrors;
                super.yErrors = new double[this.nAnalyteConcns];

                for (var6 = 0; var6 < this.nAnalyteConcns; ++var6) {
                    this.weight[var6] = var17[var6];
                }

                if (super.xErrorsEntered) {
                    var17 = super.xErrors[0];
                    super.xErrors = new double[1][this.nAnalyteConcns];

                    for (var6 = 0; var6 < this.nAnalyteConcns; ++var6) {
                        super.xErrors[0][var6] = var17[var6];
                    }
                }

                if (this.analyteConcnFlag == 1) {
                    var17 = this.log10AnalyteConcns;
                    this.log10AnalyteConcns = new double[this.nAnalyteConcns];

                    for (var6 = 0; var6 < this.nAnalyteConcns; ++var6) {
                        this.log10AnalyteConcns[var6] = var17[var6];
                    }
                }

                if (this.analyteConcnFlag == 2) {
                    var17 = this.logeAnalyteConcns;
                    this.logeAnalyteConcns = new double[this.nAnalyteConcns];

                    for (var6 = 0; var6 < this.nAnalyteConcns; ++var6) {
                        this.logeAnalyteConcns[var6] = var17[var6];
                    }
                }
            }

            this.nPlot = this.nAnalyteConcns;
            switch (this.analyteConcnFlag) {
                case 0:
                    this.log10AnalyteConcns = this.log10(this.analyteConcns);
                    this.logeAnalyteConcns = this.loge(this.analyteConcns);
                    break;
                case 1:
                    this.logeAnalyteConcns = this.loge(this.analyteConcns);
                    break;
                case 2:
                    this.log10AnalyteConcns = this.log10(this.analyteConcns);
            }

            var5 = 0;
            this.responsesPlot = true;

            for (var6 = 0; var6 < this.nAnalyteConcns; ++var6) {
                if (this.responses[var6] <= 0.0D) {
                    ++var5;
                }
            }

            if (var5 == 1) {
                if (this.responses[0] <= 0.0D) {
                    --this.nPlot;
                    this.responsesPlot = true;
                } else {
                    this.responsesPlot = false;
                }
            } else if (var5 > 1) {
                this.responsesPlot = false;
            }

            if (this.responsesPlot) {
                switch (this.responsesFlag) {
                    case 0:
                        this.log10Responses = this.log10(this.responses);
                        this.logeResponses = this.loge(this.analyteConcns);
                        break;
                    case 1:
                        this.logeResponses = this.loge(this.responses);
                        break;
                    case 2:
                        this.log10Responses = this.log10(this.responses);
                }
            }

            CubicSpline.supress();
            double[][] var19 = new double[1][this.nResponses];
            var19[0] = this.analyteConcns;
            if (super.xErrorsEntered) {
                super.setDefaultValues(var19, this.responses, super.xErrors, super.yErrors);
            } else {
                super.setDefaultValues(var19, this.responses, super.yErrors);
            }

            this.responsesMax = Fmath.maximum(this.responses);
            this.responsesMin = Fmath.minimum(this.responses);
            this.responsesRange = this.responsesMax - this.responsesMin;
            this.setInterpolationData();
            if (this.nAnalyteConcns <= 3) {
                this.anscombeC = this.critValuesFive[0];
            } else if (this.nAnalyteConcns >= 120) {
                this.anscombeC = this.critValuesFive[19];
            } else {
                if (this.confidenceLevel == 0.95D) {
                    this.critValues = new CubicSpline(this.critSize, this.critValuesFive);
                } else {
                    this.critValues = new CubicSpline(this.critSize, this.critValuesOne);
                }

                this.anscombeC = this.critValues.interpolate((double) this.nAnalyteConcns);
            }

            this.setDataOneDone = true;
        }
    }

    public void setNintyNineLevel() {
        this.confidenceLevel = 0.99D;
    }

    public void setNintyFiveLevel() {
        this.confidenceLevel = 0.95D;
    }

    private void setInterpolationData() {
        this.interpolationConcns = new double[this.nInterp];
        this.calculatedResponses = new double[this.nInterp];
        double var1 = (this.analyteConcns[this.nAnalyteConcns - 1] - this.analyteConcns[0]) / (double) (this.nInterp - 1);
        this.interpolationConcns[0] = this.analyteConcns[0];

        for (int var3 = 1; var3 < this.nInterp - 1; ++var3) {
            this.interpolationConcns[var3] = this.interpolationConcns[var3 - 1] + var1;
        }

        this.interpolationConcns[this.nInterp - 1] = this.analyteConcns[this.nAnalyteConcns - 1];
    }

    public void resetPlotOption(int var1) {
        if (var1 >= 0 && var1 <= 4) {
            this.plotOptions = var1;
        } else {
            throw new IllegalArgumentException("The plot option, " + var1 + ", must be greater than or equal to 0 and less than 5");
        }
    }

    private void supressPlot() {
        this.supressPlot = true;
    }

    private void unsupressPlot() {
        this.supressPlot = false;
    }

    private int plott() {
        byte var1 = 1;
        double[][] var2 = new double[4][];
        PlotGraph var3 = null;
        int var4;
        int var5;
        int var6;
        switch (this.plotOptions) {
            case 0:
                var2[0] = this.analyteConcns;
                var2[1] = this.responses;
                var2[2] = this.interpolationConcns;
                var2[3] = this.calculatedResponses;
                var3 = new PlotGraph(var2);
                var3.setXaxisLegend("Analyte concentration (a)");
                var3.setYaxisLegend("Assay response (r) ");
                break;
            case 1:
                var4 = this.nAnalyteConcns;
                var5 = this.nInterp;
                if (var4 != this.nPlot) {
                    var4 = this.nPlot;
                    var5 = this.nInterp - 1;
                }

                var2[0] = new double[var4];
                var2[1] = new double[var4];
                var2[2] = new double[var5];
                var2[3] = new double[var5];
                if (var4 == this.nAnalyteConcns) {
                    var2[0] = this.log10AnalyteConcns;
                    var2[1] = this.responses;
                    var2[3] = this.calculatedResponses;
                    var2[2] = new double[this.nInterp];

                    for (var6 = 0; var6 < this.nInterp; ++var6) {
                        var2[2][var6] = Math.log10(this.interpolationConcns[var6]);
                    }
                } else {
                    for (var6 = 1; var6 < this.nAnalyteConcns; ++var6) {
                        var2[0][var6 - 1] = this.log10AnalyteConcns[var6];
                        var2[1][var6 - 1] = this.responses[var6];
                    }

                    for (var6 = 1; var6 < this.nInterp; ++var6) {
                        var2[2][var6 - 1] = Math.log10(this.interpolationConcns[var6]);
                        var2[3][var6 - 1] = this.calculatedResponses[var6];
                    }
                }

                var3 = new PlotGraph(var2);
                var3.setXaxisLegend("Log10[ Analyte concentration (a) ]");
                var3.setYaxisLegend("Assay response (r) ");
                break;
            case 2:
                var4 = this.nAnalyteConcns;
                var5 = this.nInterp;
                if (var4 != this.nPlot) {
                    var4 = this.nPlot;
                    var5 = this.nInterp - 1;
                }

                var2[0] = new double[var4];
                var2[1] = new double[var4];
                var2[2] = new double[var5];
                var2[3] = new double[var5];
                if (var4 == this.nAnalyteConcns) {
                    var2[0] = this.logeAnalyteConcns;
                    var2[1] = this.responses;
                    var2[3] = this.calculatedResponses;
                    var2[2] = new double[this.nInterp];

                    for (var6 = 0; var6 < this.nInterp; ++var6) {
                        var2[2][var6] = Math.log(this.interpolationConcns[var6]);
                    }
                } else {
                    for (var6 = 1; var6 < this.nAnalyteConcns; ++var6) {
                        var2[0][var6 - 1] = this.logeAnalyteConcns[var6];
                        var2[1][var6 - 1] = this.responses[var6];
                    }

                    for (var6 = 1; var6 < this.nInterp; ++var6) {
                        var2[2][var6 - 1] = Math.log(this.interpolationConcns[var6]);
                        var2[3][var6 - 1] = this.calculatedResponses[var6];
                    }
                }

                var3 = new PlotGraph(var2);
                var3.setXaxisLegend("Loge[ Analyte concentration (a) ]");
                var3.setYaxisLegend("Assay response (r) ");
                break;
            case 3:
                if (!this.responsesPlot) {
                    System.out.println("A log(concentration) v. log(responses) plot is not possible; zero or negative responses");
                    return -1;
                }

                var4 = this.nAnalyteConcns;
                var5 = this.nInterp;
                if (var4 != this.nPlot) {
                    var4 = this.nPlot;
                    var5 = this.nInterp - 1;
                }

                var2[0] = new double[var4];
                var2[1] = new double[var4];
                var2[2] = new double[var5];
                var2[3] = new double[var5];
                if (var4 == this.nAnalyteConcns) {
                    var2[0] = this.log10AnalyteConcns;
                    var2[1] = this.log10Responses;
                    var2[2] = new double[this.nInterp];
                    var2[3] = new double[this.nInterp];

                    for (var6 = 0; var6 < this.nInterp; ++var6) {
                        var2[2][var6] = Math.log10(this.interpolationConcns[var6]);
                        var2[3][var6] = Math.log10(this.calculatedResponses[var6]);
                    }
                } else {
                    for (var6 = 1; var6 < this.nAnalyteConcns; ++var6) {
                        var2[0][var6 - 1] = this.log10AnalyteConcns[var6];
                        var2[1][var6 - 1] = this.log10Responses[var6];
                    }

                    for (var6 = 1; var6 < this.nInterp; ++var6) {
                        var2[2][var6 - 1] = Math.log10(this.interpolationConcns[var6]);
                        var2[3][var6 - 1] = Math.log10(this.calculatedResponses[var6]);
                    }
                }

                var3 = new PlotGraph(var2);
                var3.setXaxisLegend("Log10[ Analyte concentration (a) ]");
                var3.setYaxisLegend("Log10[ Assay response (r) ]");
                break;
            case 4:
                if (!this.responsesPlot) {
                    System.out.println("A log(concentration) v. log(responses) plot is not possible; zero or negative responses");
                    return -1;
                }

                var4 = this.nAnalyteConcns;
                var5 = this.nInterp;
                if (var4 != this.nPlot) {
                    var4 = this.nPlot;
                    var5 = this.nInterp - 1;
                }

                var2[0] = new double[var4];
                var2[1] = new double[var4];
                var2[2] = new double[var5];
                var2[3] = new double[var5];
                if (var4 == this.nAnalyteConcns) {
                    var2[0] = this.logeAnalyteConcns;
                    var2[1] = this.logeResponses;
                    var2[2] = new double[this.nInterp];
                    var2[3] = new double[this.nInterp];

                    for (var6 = 0; var6 < this.nInterp; ++var6) {
                        var2[2][var6] = Math.log(this.interpolationConcns[var6]);
                        var2[3][var6] = Math.log(this.calculatedResponses[var6]);
                    }
                } else {
                    for (var6 = 1; var6 < this.nAnalyteConcns; ++var6) {
                        var2[0][var6 - 1] = this.logeAnalyteConcns[var6];
                        var2[1][var6 - 1] = this.logeResponses[var6];
                    }

                    for (var6 = 1; var6 < this.nInterp; ++var6) {
                        var2[2][var6 - 1] = Math.log(this.interpolationConcns[var6]);
                        var2[3][var6 - 1] = Math.log(this.calculatedResponses[var6]);
                    }
                }

                var3 = new PlotGraph(var2);
                var3.setXaxisLegend("Loge[ Analyte concentration (a) ]");
                var3.setYaxisLegend("Loge[ Assay response (r) ]");
        }

        int[] var7 = new int[]{1, 0};
        var3.setPoint(var7);
        int[] var8 = new int[]{0, 3};
        var3.setLine(var8);
        var3.setGraphTitle(this.titleZero);
        var3.setGraphTitle2(this.titleOne);
        var3.plot();
        return var1;
    }

    private void curveCheck(int var1) {
        this.interpStartIndex = 0;
        this.interpEndIndex = this.nInterp - 1;
        this.outlierFlag = false;
        this.outliers = new String[this.nAnalyteConcns];
        this.residualsMean = Stat.mean(super.residual);
        this.residualsSD = Stat.standardDeviation(super.residual);
        int var2 = 0;
        double var3 = Math.abs(this.residual[0]);

        int var5;
        for (var5 = 0; var5 < this.nAnalyteConcns; ++var5) {
            if (Math.abs(this.residual[var5]) > var3) {
                var3 = Math.abs(this.residual[var5]);
                var2 = var5;
            }
        }

        for (var5 = 0; var5 < this.nAnalyteConcns; ++var5) {
            this.outliers[var5] = "   ";
            if (Math.abs(this.residual[var5] - this.residualsMean) > this.anscombeC * this.residualsSD) {
                this.outliers[var5] = "possible outlier";
                if (var5 == var2) {
                    this.outliers[var5] = this.outliers[var5] + " (***)";
                }

                this.outlierFlag = true;
            }
        }

        var5 = 1;
        this.nTurningPoints = 0;
        boolean var6 = false;
        ArrayList var7 = new ArrayList();
        double var8 = this.calculatedResponses[1] - this.calculatedResponses[0];
        if (var8 < 0.0D) {
            var5 = -1;
        }

        if (var8 == 0.0D) {
            var6 = true;
        }

        int var10;
        for (var10 = 2; var10 < this.nInterp; ++var10) {
            if (var5 == 1) {
                if (this.calculatedResponses[var10] <= this.calculatedResponses[var10 - 1]) {
                    var6 = true;
                }
            } else if (this.calculatedResponses[var10] >= this.calculatedResponses[var10 - 1]) {
                var6 = true;
            }

            if (var6) {
                ++this.nTurningPoints;
                var7.add(new Integer(var10));
                var5 = -var5;
                this.ambigCheck = true;
                var6 = false;
            }
        }

        if (this.nTurningPoints == 1) {
            this.turnIndices = new int[1];
            this.turnIndices[0] = ((Integer) var7.get(0)).intValue();
            if (this.turnIndices[0] <= this.nInterp / 2) {
                this.interpStartIndex = this.turnIndices[0];
            } else {
                this.interpEndIndex = this.turnIndices[0];
            }

            System.out.println(this.methodNames[var1]);
            System.out.println("The fitted or interpolated standard curve is not monotonic");
            System.out.println("The useable analyte concentration range is " + this.interpolationConcns[this.interpStartIndex] + " to " + this.interpolationConcns[this.interpEndIndex]);
        }

        int var15;
        if (this.nTurningPoints > 1) {
            this.turnIndices = new int[this.nTurningPoints];

            for (var10 = 0; var10 < this.nTurningPoints; ++var10) {
                this.turnIndices[var10] = ((Integer) var7.get(var10)).intValue();
            }

            int[] var28 = new int[this.nTurningPoints + 1];
            int[] var11 = new int[this.nTurningPoints + 1];
            int[] var12 = new int[this.nTurningPoints + 1];
            var12[0] = this.turnIndices[0];
            var28[0] = 0;
            var11[0] = this.turnIndices[0];

            int var13;
            for (var13 = 1; var13 < this.nTurningPoints; ++var13) {
                var12[var13] = this.turnIndices[var13] - this.turnIndices[var13 - 1];
                var28[var13] = this.turnIndices[var13 - 1];
                var11[var13] = this.turnIndices[var13];
            }

            var12[this.nTurningPoints] = this.nInterp - 1 - this.turnIndices[this.nTurningPoints - 1];
            var28[this.nTurningPoints] = this.turnIndices[this.nTurningPoints - 1];
            var11[this.nTurningPoints] = this.nInterp - 1;
            var13 = 0;

            for (int var14 = 0; var14 <= this.nTurningPoints; ++var14) {
                for (var15 = 0; var15 <= this.nTurningPoints; ++var15) {
                    if (var14 != var15 && var12[var14] >= var12[var15]) {
                        ++var13;
                    }
                }

                if (var13 == this.nTurningPoints) {
                    this.interpStartIndex = var28[var14];
                    this.interpEndIndex = var11[var14];
                } else {
                    var13 = 0;
                }

                if (var13 != 0) {
                    break;
                }
            }

            System.out.println(this.methodNames[var1]);
            System.out.println("The fitted or interpolated standard curve is not monotonic");
            System.out.println("The useable analyte concentration range is " + this.interpolationConcns[this.interpStartIndex] + " to " + this.interpolationConcns[this.interpEndIndex]);
        }

        this.interpAnalyteStart = this.interpolationConcns[this.interpStartIndex];
        this.interpAnalyteEnd = this.interpolationConcns[this.interpEndIndex];
        this.interpResponseStart = this.calculatedResponses[this.interpStartIndex];
        this.interpResponseEnd = this.calculatedResponses[this.interpEndIndex];
        this.nWorking = this.interpEndIndex - this.interpStartIndex + 1;
        double[] var29 = new double[this.nWorking];
        double[] var27 = new double[this.nWorking];

        for (int var30 = 0; var30 < this.nWorking; ++var30) {
            var29[var30] = this.interpolationConcns[this.interpStartIndex + var30];
            var27[var30] = this.calculatedResponses[this.interpStartIndex + var30];
        }

        if (this.methodUsed == 14) {
            this.linterp = new LinearInterpolation(var27, var29);
        } else {
            this.interp = new CubicSpline(var27, var29);
        }

        this.curveDirection = true;
        if (this.interpResponseStart > this.interpResponseEnd) {
            this.curveDirection = false;
        }

        this.workingResponseMin = Fmath.minimum(this.calculatedResponses);
        if (this.curveDirection) {
            if (this.workingResponseMin < this.interpResponseStart) {
                this.workingResponseMin = this.interpResponseStart;
            }
        } else if (this.workingResponseMin < this.interpResponseEnd) {
            this.workingResponseMin = this.interpResponseEnd;
        }

        this.workingResponseMax = Fmath.maximum(this.calculatedResponses);
        if (this.curveDirection) {
            if (this.workingResponseMax > this.interpResponseEnd) {
                this.workingResponseMax = this.interpResponseEnd;
            }
        } else if (this.workingResponseMax > this.interpResponseStart) {
            this.workingResponseMax = this.interpResponseStart;
        }

        double[] var31 = new double[this.nWorking];
        double var32 = Math.sqrt(super.sumOfSquaresError / (double) (this.nAnalyteConcns - super.nParam));
        if (this.sampleErrorFlag) {
            double[] var20;
            int var21;
            if (!super.yErrorsEntered) {
                for (var15 = 0; var15 < this.nWorking; ++var15) {
                    var31[var15] = var32;
                }
            } else {
                boolean var33 = true;
                int var16 = 0;
                int var17 = this.nAnalyteConcns - 1;
                boolean var18 = false;
                int var19;
                if (this.ambigCheck) {
                    for (var19 = 0; var19 < this.nAnalyteConcns; ++var19) {
                        if (this.interpAnalyteStart >= this.analyteConcns[var19]) {
                            var16 = var19;
                            var18 = true;
                        }

                        if (var18) {
                            break;
                        }
                    }

                    var18 = false;

                    for (var19 = this.nAnalyteConcns - 1; var19 >= 0; --var19) {
                        if (this.interpAnalyteEnd <= this.analyteConcns[var19]) {
                            var17 = var19;
                            var18 = true;
                        }

                        if (var18) {
                            break;
                        }
                    }
                }

                var19 = var17 - var16 + 1;
                var20 = new double[var19];

                for (var21 = 0; var21 < var19; ++var21) {
                    var20[var21] = super.yErrors[var21 + var16];
                }

                var33 = isMonotonic(var20);
                int var25;
                if (var33) {
                    double var38 = Math.abs(var20[1] - var20[0]);
                    double var23 = var38;

                    for (var25 = 2; var25 < var19; ++var25) {
                        var38 = Math.abs(var20[var25] - var20[var25 - 1]);
                        if (var38 > var23) {
                            var23 = var38;
                        }
                    }

                    if (var23 > 0.6D * Math.abs(Fmath.maximum(var20) - Fmath.minimum(var20))) {
                        var33 = false;
                    }
                }

                double[] var40 = new double[this.nAnalyteConcns];
                double var22 = var32 / this.weightsMean;

                for (int var24 = 0; var24 < this.nAnalyteConcns; ++var24) {
                    var40[var24] = super.yErrors[var24] * var22;
                }

                if (super.yErrorsEntered && !var33) {
                    LinearInterpolation var45 = new LinearInterpolation(this.analyteConcns, var40);

                    for (var25 = 0; var25 < this.nWorking; ++var25) {
                        var31[var25] = Math.abs(var45.interpolate(var29[var25]));
                    }
                } else {
                    CubicSpline var44 = new CubicSpline(this.analyteConcns, var40);

                    for (var25 = 0; var25 < this.nWorking; ++var25) {
                        var31[var25] = Math.abs(var44.interpolate(var29[var25]));
                    }
                }
            }

            this.errorp = new CubicSpline(var27, var31);
            this.propagatedErrors = new String[this.nAnalyteConcns];
            double var34 = 0.0D;
            ArrayList var35 = new ArrayList();

            int var36;
            for (var36 = 0; var36 < this.nAnalyteConcns; ++var36) {
                boolean var37 = false;
                if (this.curveDirection) {
                    if (this.responses[var36] < this.interpResponseStart || this.responses[var36] > this.interpResponseEnd) {
                        var37 = true;
                    }
                } else if (this.responses[var36] > this.interpResponseStart || this.responses[var36] < this.interpResponseEnd) {
                    var37 = true;
                }

                if (var37) {
                    this.propagatedErrors[var36] = "**";
                } else {
                    var35.add(new Double(this.responses[var36]));
                    this.getSampleConcn(this.responses[var36]);
                    var34 = this.getSampleConcnError();
                    var35.add(new Double(var34));
                    var34 = Fmath.truncate(var34, super.prec);
                    this.propagatedErrors[var36] = (new Double(var34)).toString();
                }
            }

            var36 = var35.size() / 2;
            double[] var39 = new double[var36];
            var20 = new double[var36];

            for (var21 = 0; var21 < var36; ++var21) {
                var39[var21] = ((Double) var35.get(2 * var21)).doubleValue();
                var20[var21] = ((Double) var35.get(2 * var21 + 1)).doubleValue();
            }

            CubicSpline var42 = new CubicSpline(var39, var20);
            double[] var41 = new double[1001];
            double[] var43 = new double[1001];
            var41[0] = var39[0];
            var41[1000] = var39[var36 - 1];
            double var46 = (var39[var36 - 1] - var39[0]) / 1000.0D;

            int var26;
            for (var26 = 1; var26 < 1000; ++var26) {
                var41[var26] = var41[var26 - 1] + var46;
            }

            for (var26 = 0; var26 < 1001; ++var26) {
                var43[var26] = var42.interpolate(var41[var26]);
            }

            Stat var47 = new Stat(var43);
            this.minimumAerror = var47.minimum();
            this.maximumAerror = var47.maximum();
            this.meanAerror = var47.mean();
            this.sdAerror = var47.standardDeviation();
        }

    }

    public double getSampleConcn(double var1) {
        this.sampleResponse = var1;
        double var3 = 0.0D / 0.0;
        boolean var5 = false;
        if (this.curveDirection) {
            if (var1 < this.interpResponseStart || var1 > this.interpResponseEnd) {
                var5 = true;
            }
        } else if (var1 > this.interpResponseStart || var1 < this.interpResponseEnd) {
            var5 = true;
        }

        if (var5) {
            if (this.ambigCheck) {
                System.out.println("The sample response, " + var1 + ", is outside the useable part of the standard curve:");
            } else {
                System.out.println("The sample response, " + var1 + ", is outside the limits of the standard curve:");
            }

            System.out.println(this.interpResponseStart + " to " + this.interpResponseEnd);
            System.out.println("NaN returned");
            return var3;
        } else {
            if (this.methodUsed == 14) {
                var3 = this.linterp.interpolate(var1);
            } else {
                var3 = this.interp.interpolate(var1);
            }

            this.sampleConcn = var3;
            this.sampleError = 0.0D / 0.0;
            if (this.sampleErrorFlag) {
                double var6 = this.errorp.interpolate(var1);
                double var8 = var1 - var6;
                if (var8 < this.workingResponseMin) {
                    var8 = this.workingResponseMin;
                }

                double var10 = (var1 - var8) / var6;
                double var12 = this.interp.interpolate(var8);
                double var14 = var1 + var6;
                if (var14 > this.workingResponseMax) {
                    var14 = this.workingResponseMax;
                }

                var10 += (var14 - var1) / var6;
                double var16 = this.interp.interpolate(var14);
                this.sampleError = Math.abs(var16 - var12) / var10;
            }

            return var3;
        }
    }

    public double getSampleConcnError() {
        if (this.methodUsed == 0) {
            System.out.println("ImmunoAssay method: getSampleConcnError()");
            System.out.println("A sample concentration error is not meaningful in the case of a cubic spline interpolation");
            System.out.println("NaN returned");
        }

        if (this.methodUsed == 1) {
            System.out.println("ImmunoAssay method: getSampleConcnError()");
            System.out.println("A sample concentration error is not meaningful in the case of a cubic interpolation");
            System.out.println("NaN returned");
        }

        if (this.methodUsed == 14) {
            System.out.println("ImmunoAssay method: getSampleConcnError()");
            System.out.println("A sample concentration error is not meaningful in the case of a linear interpolation");
            System.out.println("NaN returned");
        }

        return this.sampleError;
    }

    public void resetNfittedCurve(int var1) {
        this.nInterp = var1;
    }

    public double[] getFittedStandardCurveConcns() {
        return Conv.copy(this.interpolationConcns);
    }

    public double[] getFittedStandardCurveResponses() {
        return Conv.copy(this.calculatedResponses);
    }

    public double[] getWorkingConcentrationRange() {
        double[] var1 = new double[]{this.interpAnalyteStart, this.interpAnalyteEnd};
        return var1;
    }

    public double[] getWorkingResponseRange() {
        double[] var1 = new double[]{this.interpResponseStart, this.interpResponseEnd};
        return var1;
    }

    public double[] getModelParameterValues() {
        return Conv.copy(super.best);
    }

    public double[] getModelParameterErrors() {
        return Conv.copy(super.bestSd);
    }

    public int getNumberOfTurningPoints() {
        return this.nTurningPoints;
    }

    public double getSumOfSquares() {
        return super.sumOfSquaresError;
    }

    public double getUnWeightedSumOfSquares() {
        return super.sumOfSquaresError;
    }

    public double getWeightedSumOfSquares() {
        return super.chiSquare;
    }

    public double[] getTurningPointConcns() {
        double[] var1 = null;
        if (this.nTurningPoints > 0) {
            var1 = new double[this.nTurningPoints];

            for (int var2 = 0; var2 < this.nTurningPoints; ++var2) {
                var1[var2] = this.interpolationConcns[this.turnIndices[var2]];
            }
        }

        return var1;
    }

    public double[] getTurningPointResponses() {
        double[] var1 = null;
        if (this.nTurningPoints > 0) {
            var1 = new double[this.nTurningPoints];

            for (int var2 = 0; var2 < this.nTurningPoints; ++var2) {
                var1[var2] = this.calculatedResponses[this.turnIndices[var2]];
            }
        }

        return var1;
    }

    public void print() {
        this.print(this.filename);
    }

    public void print(String var1) {
        boolean var2 = true;
        switch (this.methodUsed) {
            case 0:
                System.out.println("There is no text file associated with the cubic spline interpolation method");
                var2 = false;
                break;
            case 1:
                System.out.println("There is no text file associated with the cubic interpolation method");
                var2 = false;
                break;
            case 14:
                System.out.println("There is no text file associated with the linear interpolation method");
                var2 = false;
        }

        if (var2) {
            int var3 = var1.indexOf(".");
            if (var3 == -1) {
                var1 = var1 + ".txt";
            }

            this.filename = var1;
            FileOutput var4 = new FileOutput(var1);
            var4.println(this.titleZero);
            if (this.dataRead) {
                var4.println("Data input file name:   " + this.dataFilename);
            }

            var4.dateAndTimeln(var1);
            this.commonPrint(var4);
            var4.println();
            var4.println("End of file");
            var4.close();
        }

    }

    public void commonPrint(FileOutput var1) {
        var1.println();
        var1.println(this.titleOne);
        var1.println("r = assay response;  a = analyte concentration");
        boolean var2 = false;
        String[] var3 = null;
        Object var4 = null;
        int var5;
        switch (this.methodUsed) {
            case 0:
            case 1:
            case 14:
                break;
            case 2:
                var3 = new String[this.polyDegree + 1];

                for (var5 = 0; var5 <= this.polyDegree; ++var5) {
                    var3[var5] = "c[" + var5 + "]";
                }

                var2 = false;
                break;
            case 3:
                var3 = new String[this.bestPolyDegree + 1];

                for (var5 = 0; var5 <= this.bestPolyDegree; ++var5) {
                    var3[var5] = "c[" + var5 + "]";
                }

                var2 = false;
                break;
            case 4:
                var1.println("n = " + (this.polyNterms - 1));
                var3 = new String[2 * this.polyNterms - 1];

                for (var5 = 0; var5 < 2 * this.polyNterms - 1; ++var5) {
                    var3[var5] = "c[" + var5 + "]";
                }

                var2 = true;
                break;
            case 5:
                var3 = new String[]{"alpha", "theta", "A"};
                var2 = true;
                break;
            case 6:
                var3 = new String[]{"theta", "n", "A"};
                var2 = true;
                break;
            case 7:
                var3 = new String[]{"top", "bottom", "C50", "Hill Slope"};
                var2 = true;
                break;
            case 8:
                var3 = new String[]{"top", "bottom", "C50", "HillSlope", "Asymm"};
                var2 = true;
                break;
            case 9:
                var3 = new String[]{"theta", "alpha", "A"};
                var2 = true;
                break;
            case 10:
                var3 = new String[]{"theta", "A"};
                var2 = true;
                break;
            case 11:
                var3 = new String[]{"K", "L", "P", "N", "S"};
                var2 = true;
                break;
            case 12:
                var1.println("top =    " + this.top);
                var1.println("bottom = " + this.bottom);
                var3 = new String[]{"C50", "Hill Slope"};
                var2 = true;
                break;
            case 13:
                var1.println("top =    " + this.top);
                var1.println("bottom = " + this.bottom);
                var3 = new String[]{"C50", "HillSlope", "Asymm"};
                var2 = true;
                break;
            default:
                throw new IllegalArgumentException("methodUsed " + this.methodUsed + " not recognised");
        }

        if (var2) {
            var1.println();
            var1.println("Non-linear regression (Nelder and Mead simplex procedure)");
            if (!super.nlrStatus) {
                var1.println("Convergence criterion was not satisfied");
                var1.println("The following results are, or are derived from, the current estimates on exiting the regression method");
                var1.println();
            }

            var1.println("Estimated parameters");
            var1.println("The statistics are obtained assuming that the model behaves as a linear model about the minimum.");
            var1.println("The Hessian matrix is calculated as the numerically derived second derivatives of chi square with respect to all pairs of parameters.");
            if (super.zeroCheck) {
                var1.println("The best estimate/s equal to zero were replaced by the step size in the numerical differentiation!!!");
            }

            var1.println("Consequentlty treat the statistics with great caution.");
            if (!super.posVarFlag) {
                var1.println("Covariance matrix contains at least one negative diagonal element");
                var1.println(" - all variances are dubious");
                var1.println(" - may not be at a minimum or the model may be so non-linear that the linear approximation in calculating the statisics is invalid");
            }

            if (!super.invertFlag) {
                var1.println("Hessian matrix is singular");
                var1.println(" - variances cannot be calculated");
                var1.println(" - may not be at a minimum  or the model may be so non-linear that the linear approximation in calculating the statisics is invalid");
            }
        } else {
            var1.println("Linear regression");
        }

        var1.println();
        var1.printtab(" ", super.field);
        if (super.invertFlag) {
            var1.printtab("Best", super.field);
            var1.printtab("Estimate of", super.field);
            var1.printtab("Coefficient", super.field);
            var1.printtab("t-value", super.field);
            var1.println("p-value");
        } else {
            var1.println("Best");
        }

        if (super.invertFlag) {
            var1.printtab(" ", super.field);
            var1.printtab("estimate", super.field);
            var1.printtab("the error", super.field);
            var1.printtab("of", super.field);
            var1.printtab("t", super.field);
            var1.println("P > |t|");
        } else {
            var1.printtab(" ", super.field);
            var1.println("estimate");
        }

        if (super.invertFlag) {
            var1.printtab(" ", super.field);
            var1.printtab(" ", super.field);
            var1.printtab(" ", super.field);
            var1.println("variation (%)");
        } else {
            var1.println("   ");
        }

        for (var5 = 0; var5 < super.nParam; ++var5) {
            if (super.invertFlag) {
                var1.printtab(var3[var5], super.field);
                var1.printtab(Fmath.truncate(super.best[var5], super.prec), super.field);
                var1.printtab(Fmath.truncate(super.bestSd[var5], super.prec), super.field);
                var1.printtab(Fmath.truncate(Math.abs(super.bestSd[var5] * 100.0D / super.best[var5]), super.prec), super.field);
                var1.printtab(Fmath.truncate(super.tValues[var5], super.prec), super.field);
                var1.println(Fmath.truncate(super.pValues[var5], super.prec));
            } else {
                var1.printtab(var3[var5], super.field);
                var1.println(Fmath.truncate(super.best[var5], super.prec));
            }
        }

        var1.println();
        if (var2) {
            var1.printtab(" ", super.field);
            var1.printtab("Best", super.field);
            var1.printtab("Pre-minimum", super.field);
            var1.printtab("Post-minimum", super.field);
            var1.printtab("Initial", super.field);
            var1.println("Fractional");
            var1.printtab(" ", super.field);
            var1.printtab("estimate", super.field);
            var1.printtab("gradient", super.field);
            var1.printtab("gradient", super.field);
            var1.printtab("estimate", super.field);
            var1.println("step");

            for (var5 = 0; var5 < super.nParam; ++var5) {
                var1.printtab(var3[var5], super.field);
                var1.printtab(Fmath.truncate(super.best[var5], super.prec), super.field);
                var1.printtab(Fmath.truncate(super.grad[var5][0], super.prec), super.field);
                var1.printtab(Fmath.truncate(super.grad[var5][1], super.prec), super.field);
                var1.printtab(Fmath.truncate(super.startH[var5], super.prec), super.field);
                var1.println(Fmath.truncate(super.stepH[var5], super.prec));
            }

            var1.println();
        }

        var5 = 0;
        var1.printtab("analyte", super.field);
        var1.printtab("observed", super.field);
        var1.printtab("calculated", super.field);
        var1.printtab("weight", super.field);
        var1.printtab("unweighted", super.field);
        var1.printtab("weighted", super.field);
        var1.printtab("estimated analyte", super.field);
        var1.println("outliers");
        var1.printtab("concn ", super.field);
        var1.printtab("response", super.field);
        var1.printtab("response", super.field);
        var1.printtab("     ", super.field);
        var1.printtab("residual", super.field);
        var1.printtab("residual", super.field);
        var1.printtab("concn error *", super.field);
        var1.println("   ");

        int var6;
        for (var6 = 0; var6 < this.nAnalyteConcns; ++var6) {
            var1.printtab(Fmath.truncate(super.xData[0][var5], super.prec), super.field);
            var1.printtab(Fmath.truncate(super.yData[var5], super.prec), super.field);
            var1.printtab(Fmath.truncate(super.yCalc[var5], super.prec), super.field);
            var1.printtab(Fmath.truncate(super.weight[var5], super.prec), super.field);
            var1.printtab(Fmath.truncate(super.residual[var5], super.prec), super.field);
            var1.printtab(Fmath.truncate(super.residualW[var5], super.prec), super.field);
            var1.printtab(this.propagatedErrors[var5], super.field);
            var1.println(this.outliers[var6]);
            ++var5;
        }

        var1.println();
        var1.println("*  The estimated error in the estimated concentration on entering this response via getSampleConcn(response)");
        var1.println("** Outside the working range");
        var1.println("Mean of the unweighted residuals =               " + Fmath.truncate(this.residualsMean, super.prec));
        var1.println("Standard deviation of the unweighted residuals = " + Fmath.truncate(this.residualsSD, super.prec));
        var1.println("Outlier critical value at the " + this.confidenceLevel * 100.0D + " confidence level = " + Fmath.truncate(this.anscombeC * this.residualsSD + this.residualsMean, super.prec));
        if (this.outlierFlag) {
            var1.println("*** the most extreme possible outlier");
        }

        var1.println();
        var1.println("Minimum estimated interpolated concentration error:                   " + Fmath.truncate(this.minimumAerror, super.prec));
        var1.println("Maximum estimated interpolated concentration error:                   " + Fmath.truncate(this.maximumAerror, super.prec));
        var1.println("Mean estimated interpolated concentration error:                      " + Fmath.truncate(this.meanAerror, super.prec));
        var1.println("Standard deviation of the estimated interpolated concentration error: " + Fmath.truncate(this.sdAerror, super.prec));
        var1.println();
        var1.printtab("Degrees of freedom");
        var1.println(super.degreesOfFreedom);
        var1.printtab("Number of data points");
        var1.println(super.nData);
        var1.printtab("Number of estimated paramaters");
        var1.println(super.nParam);
        var1.printtab("Sum of squares of the unweighted residuals");
        var1.println(Fmath.truncate(super.sumOfSquaresError, super.prec));
        if (super.weightOpt) {
            var1.printtab("Chi Square");
            var1.println(Fmath.truncate(super.chiSquare, super.prec));
            var1.printtab("Reduced Chi Square");
            var1.println(Fmath.truncate(super.reducedChiSquare, super.prec));
        }

        int var7;
        if (var2) {
            var1.println("Correlation: analyte concentration and responses");
            var1.printtab(super.weightWord[super.weightFlag] + "Linear Correlation Coefficient (R)");
            var1.println(Fmath.truncate(super.xyR, super.prec));
            if (Math.abs(super.xyR) <= 1.0D) {
                var1.printtab(super.weightWord[super.weightFlag] + "Linear Correlation Coefficient Probability");
                var1.println(Fmath.truncate(Stat.linearCorrCoeffProb(super.xyR, super.nData - 2), super.prec));
            }

            var1.println(" ");
            var1.println("Correlation: observed responses and calculated responses");
            var1.printtab(super.weightWord[super.weightFlag] + "Linear Correlation Coefficient");
            var1.println(Fmath.truncate(super.yyR, super.prec));
            if (Math.abs(super.yyR) <= 1.0D) {
                var1.printtab(super.weightWord[super.weightFlag] + "Linear Correlation Coefficient Probability");
                var1.println(Fmath.truncate(Stat.linearCorrCoeffProb(super.yyR, super.nData - 2), super.prec));
            }

            var1.println();
            var1.printtab("Durbin-Watson d statistic");
            var1.println(Fmath.truncate(super.getDurbinWatsonD(), super.prec));
            var1.println();
            if (super.posVarFlag && super.invertFlag && super.chiSquare != 0.0D) {
                var1.println("Parameter - parameter correlation coefficients");
                var1.printtab(" ", super.field);

                for (var6 = 0; var6 < super.nParam; ++var6) {
                    var1.printtab(var3[var6], super.field);
                }

                var1.println();
                var1.println();

                for (var6 = 0; var6 < super.nParam; ++var6) {
                    var1.printtab(var3[var6], super.field);

                    for (var7 = 0; var7 < super.nParam; ++var7) {
                        var1.printtab(Fmath.truncate(super.corrCoeff[var7][var6], super.prec), super.field);
                    }

                    var1.println();
                }
            }

            var1.println();
            var1.println("Coefficient of determination, R =                   " + Fmath.truncate(super.multR, super.prec));
            var1.println("Adjusted Coefficient of determination, R' =         " + Fmath.truncate(super.adjustedR, super.prec));
            var1.println("Coefficient of determination, F-ratio =             " + Fmath.truncate(super.multipleF, super.prec));
            var1.println("Coefficient of determination, F-ratio probability = " + Fmath.truncate(super.multipleFprob, super.prec));
            var1.println("Total (weighted) sum of squares  =                  " + Fmath.truncate(super.sumOfSquaresTotal, super.prec));
            var1.println("Regression (weighted) sum of squares  =             " + Fmath.truncate(super.sumOfSquaresRegrn, super.prec));
            var1.println("Error (weighted) sum of squares  =                  " + Fmath.truncate(super.chiSquare, super.prec));
            var1.println();
            var1.printtab("Number of iterations taken");
            var1.println(super.nIter);
            var1.printtab("Maximum number of iterations allowed");
            var1.println(super.nMax);
            var1.printtab("Number of restarts taken");
            var1.println(super.kRestart);
            var1.printtab("Maximum number of restarts allowed");
            var1.println(super.konvge);
            var1.printtab("Standard deviation of the simplex at the minimum");
            var1.println(Fmath.truncate(super.simplexSd, super.prec));
            var1.printtab("Convergence tolerance");
            var1.println(super.fTol);
            switch (this.minTest) {
                case 0:
                    var1.println("simplex sd < the tolerance times the mean of the absolute values of the y values");
                    break;
                case 1:
                    var1.println("simplex sd < the tolerance");
                    break;
                case 2:
                    var1.println("simplex sd < the tolerance times the square root(sum of squares/degrees of freedom");
            }

            var1.println("Step used in numerical differentiation to obtain Hessian matrix");
            var1.println("d(parameter) = parameter*" + super.delta);
        } else {
            var1.println(" ");
            var1.println("Correlation: analyte concentrations and responses");
            var1.printtab(super.weightWord[super.weightFlag] + "Linear Correlation Coefficient (R)");
            var1.println(Fmath.truncate(super.xyR, super.prec));
            if (Math.abs(super.xyR) <= 1.0D) {
                var1.printtab(super.weightWord[super.weightFlag] + "Linear Correlation Coefficient Probability");
                var1.println(Fmath.truncate(Stat.linearCorrCoeffProb(super.xyR, super.nData - 2), super.prec));
            }

            var1.println(" ");
            var1.println("Correlation: observed responses and calculated responses");
            var1.printtab(super.weightWord[super.weightFlag] + "Linear Correlation Coefficient");
            var1.println(Fmath.truncate(super.yyR, super.prec));
            if (Math.abs(super.yyR) <= 1.0D) {
                var1.printtab(super.weightWord[super.weightFlag] + "Linear Correlation Coefficient Probability");
                var1.println(Fmath.truncate(Stat.linearCorrCoeffProb(super.yyR, super.nData - 2), super.prec));
            }

            var1.println();
            var1.println();
            if (super.chiSquare != 0.0D) {
                var1.println("Correlation coefficients");
                var1.printtab(" ", super.field);

                for (var6 = 0; var6 < super.nParam; ++var6) {
                    var1.printtab(var3[var6], super.field);
                }

                var1.println();

                for (var6 = 0; var6 < super.nParam; ++var6) {
                    var1.printtab(var3[var6], super.field);

                    for (var7 = 0; var7 < super.nParam; ++var7) {
                        var1.printtab(Fmath.truncate(super.corrCoeff[var7][var6], super.prec), super.field);
                    }

                    var1.println();
                }
            }

            var1.println();
            var1.printtab("Durbin-Watson d statistic");
            var1.println(Fmath.truncate(super.getDurbinWatsonD(), super.prec));
            var1.println();
            if (super.bestPolyFlag) {
                var1.println("Method bestPolynomial search history");
                var1.println("F-probability significance level: " + super.fProbSignificance + " (" + super.fProbSignificance * 100.0D + " %)");
                var1.println("Degree of best fit polynomial " + super.bestPolynomialDegree);
                var1.println(" ");
                var1.print("Polynomial degree", 2 * super.field);
                var1.print("chi square", 2 * super.field);
                var1.print("F-ratio", super.field);
                var1.print("F-probability", super.field + 2);
                var1.println("F-value at the");
                var1.print("comparison", 2 * super.field);
                var1.print("comparison", 2 * super.field);
                var1.print("   ", super.field);
                var1.print("   ", super.field + 2);
                var1.println("significance level");
                var6 = ((Integer) super.bestPolyArray.get(1)).intValue();
                int[] var15 = (int[]) ((int[]) super.bestPolyArray.get(2));
                int[] var8 = (int[]) ((int[]) super.bestPolyArray.get(3));
                double[] var9 = (double[]) ((double[]) super.bestPolyArray.get(4));
                double[] var10 = (double[]) ((double[]) super.bestPolyArray.get(5));
                double[] var11 = (double[]) ((double[]) super.bestPolyArray.get(6));
                double[] var12 = (double[]) ((double[]) super.bestPolyArray.get(7));
                double[] var13 = (double[]) ((double[]) super.bestPolyArray.get(8));

                for (int var14 = 0; var14 < var6; ++var14) {
                    var1.print(var15[var14], super.field);
                    var1.print(var8[var14], super.field);
                    var1.print(Fmath.truncate(var9[var14], super.prec), super.field);
                    var1.print(Fmath.truncate(var10[var14], super.prec), super.field);
                    var1.print(Fmath.truncate(var11[var14], super.prec), super.field);
                    var1.print(Fmath.truncate(var12[var14], super.prec), super.field + 2);
                    var1.println(Fmath.truncate(var13[var14], super.prec));
                }
            }

            var1.println();
            var1.println("Coefficient of determination,   =                   " + Fmath.truncate(super.multR, super.prec));
            var1.println("Adjusted Coefficient of determination,    =         " + Fmath.truncate(super.adjustedR, super.prec));
            var1.println("Coefficient of determination, F-ratio =             " + Fmath.truncate(super.multipleF, super.prec));
            var1.println("Coefficient of determination, F-ratio probability = " + Fmath.truncate(super.multipleFprob, super.prec));
            var1.println("Total (weighted) sum of squares  =                  " + Fmath.truncate(super.sumOfSquaresTotal, super.prec));
            var1.println("Regression (weighted) sum of squares  =             " + Fmath.truncate(super.sumOfSquaresRegrn, super.prec));
            var1.println("Error (weighted) sum of squares  =                  " + Fmath.truncate(super.chiSquare, super.prec));
        }

    }

    public void compare() {
        this.selectCompare(this.significance, this.compFilename);
    }

    public void compare(double var1) {
        this.selectCompare(var1, this.compFilename);
    }

    public void compare(String var1) {
        this.selectCompare(this.significance, var1);
    }

    public void compare(double var1, String var3) {
        this.selectCompare(var1, var3);
    }

    private void selectCompare(double var1, String var3) {
        this.compWindow = true;
        ArrayList var4 = this.chooseEquation(1);
        int var5 = ((Integer) var4.get(0)).intValue();
        int var6 = 0;
        int var7 = 0;
        double var8 = 0.0D;
        double var10 = 0.0D;
        if (var5 == 6) {
            var6 = ((Integer) var4.get(1)).intValue();
            this.degSet = true;
        }

        if (var5 == 7) {
            var7 = ((Integer) var4.get(1)).intValue();
            this.nTermsSet = true;
        }

        if (var5 == 2) {
            var8 = ((Double) var4.get(2)).doubleValue();
            var10 = ((Double) var4.get(3)).doubleValue();
            this.fiveBotTopSet = true;
        }

        if (var5 == 4) {
            var8 = ((Double) var4.get(2)).doubleValue();
            var10 = ((Double) var4.get(3)).doubleValue();
            this.fourBotTopSet = true;
        }

        var4 = this.chooseEquation(2);
        int var12 = ((Integer) var4.get(0)).intValue();
        int var13 = 0;
        int var14 = 0;
        double var15 = 0.0D;
        double var17 = 0.0D;
        if (var12 == 6) {
            var13 = ((Integer) var4.get(1)).intValue();
            this.degSet = true;
        }

        if (var12 == 7) {
            var14 = ((Integer) var4.get(1)).intValue();
            this.nTermsSet = true;
        }

        if (var12 == 2) {
            var15 = ((Double) var4.get(2)).doubleValue();
            var17 = ((Double) var4.get(3)).doubleValue();
            this.fiveBotTopSet = true;
        }

        if (var12 == 4) {
            var15 = ((Double) var4.get(2)).doubleValue();
            var17 = ((Double) var4.get(3)).doubleValue();
            this.fourBotTopSet = true;
        }

        this.compare(var5, var6, var8, var10, var7, var12, var13, var15, var17, var14, var1, var3);
    }

    public void compare(int var1, int var2) {
        this.compare(var1, 0, 0.0D, 0.0D, 0, var2, 0, 0.0D, 0.0D, 0, this.significance, this.compFilename);
    }

    public void compare(int var1, int var2, double var3) {
        this.compare(var1, 0, 0.0D, 0.0D, 0, var2, 0, 0.0D, 0.0D, 0, var3, this.compFilename);
    }

    public void compare(int var1, int var2, int var3, int var4, double var5) {
        this.degSet = true;
        this.compare(var1, var2, 0.0D, 0.0D, 0, var3, var4, 0.0D, 0.0D, 0, var5, this.compFilename);
    }

    public void compare(int var1, int var2, String var3) {
        this.compare(var1, 0, 0.0D, 0.0D, 0, var2, 0, 0.0D, 0.0D, 0, this.significance, var3);
    }

    public void compare(int var1, int var2, int var3, int var4, String var5) {
        this.degSet = true;
        this.compare(var1, var2, 0.0D, 0.0D, 0, var3, var4, 0.0D, 0.0D, 0, this.significance, var5);
    }

    public void compare(int var1, int var2, int var3, int var4) {
        this.degSet = true;
        this.compare(var1, var2, 0.0D, 0.0D, 0, var3, var4, 0.0D, 0.0D, 0, this.significance, this.compFilename);
    }

    public void compare(int var1, int var2, double var3, double var5, int var7, int var8, int var9, double var10, double var12, int var14, double var15, String var17) {
        if (!this.degSet && (var2 > 0 || var9 > 0)) {
            this.degSet = true;
        }

        double[] var18 = new double[2];
        int[] var19 = new int[2];
        String[] var20 = new String[2];
        int[] var21 = new int[]{var1, var8};
        if (!this.compWindow) {
            if (var1 == 2) {
                var3 = Db.readDouble("Enter five parameter logistic bottom value");
                var5 = Db.readDouble("Enter five parameter logistic top value");
            }

            if (var1 == 4) {
                var3 = Db.readDouble("Enter four parameter logistic bottom value");
                var5 = Db.readDouble("Enter four parameter logistic top value");
            }

            if (var1 == 6 && !this.degSet) {
                var2 = Db.readInt("Enter the polynomial degree");
            }

            if (var1 == 7) {
                var7 = Db.readInt("Enter the non-integer polynomial number of terms");
            }
        }

        ArrayList var22 = this.fittingForCompare(var1, var2, var3, var5, var7, false);
        var18[0] = ((Double) var22.get(0)).doubleValue();
        var19[0] = ((Integer) var22.get(1)).intValue();
        var20[0] = (String) var22.get(2);
        if (!this.compWindow) {
            if (var1 == 2) {
                var10 = Db.readDouble("Enter five parameter logistic bottom value");
                var12 = Db.readDouble("Enter five parameter logistic top value");
            }

            if (var1 == 4) {
                var10 = Db.readDouble("Enter four parameter logistic bottom value");
                var12 = Db.readDouble("Enter four parameter logistic top value");
            }

            if (var1 == 6 && !this.degSet) {
                var9 = Db.readInt("Enter the polynomial degree");
            }

            if (var1 == 7) {
                var14 = Db.readInt("Enter the non-integer polynomial number of terms");
            }
        }

        ArrayList var23 = this.fittingForCompare(var8, var9, var10, var12, var14, true);
        var18[1] = ((Double) var23.get(0)).doubleValue();
        var19[1] = ((Integer) var23.get(1)).intValue();
        var20[1] = (String) var23.get(2);
        ArrayList var24 = comparisonTest(var21, var18[0], var19[0], this.nAnalyteConcns, var18[1], var19[1], this.nAnalyteConcns, var15);
        int var25 = ((Integer) var24.get(0)).intValue();
        int var26 = ((Integer) var24.get(1)).intValue();
        int var27 = ((Integer) var24.get(2)).intValue();
        double var28 = ((Double) var24.get(3)).doubleValue();
        double var30 = ((Double) var24.get(4)).doubleValue();
        double var32 = ((Double) var24.get(5)).doubleValue();
        int var34 = ((Integer) var24.get(6)).intValue();
        int var35 = ((Integer) var24.get(7)).intValue();
        int var36;
        if (var26 != var1) {
            var36 = var19[0];
            var19[0] = var19[1];
            var19[1] = var36;
            double var37 = var18[0];
            var18[0] = var18[1];
            var18[1] = var37;
            String var39 = var20[0];
            var20[0] = var20[1];
            var20[1] = var39;
        }

        var36 = var17.indexOf(".");
        if (var36 == -1) {
            var17 = var17 + ".txt";
        }

        FileOutput var42 = new FileOutput(var17);
        var42.println(this.titleZero);
        var42.println("Comparison of two fitting procedures");
        if (this.dataRead) {
            var42.println("Data input file name " + this.dataFilename);
        }

        var42.dateAndTimeln(var17);
        var42.println();
        var42.println("Equations compared:");
        var42.println("   Equation One: " + var20[0]);
        var42.println("   Equation Two: " + var20[1]);
        var42.println();
        var42.print("                    ", super.field);
        var42.print("Eqation", super.field);
        var42.println("Eqation");
        var42.print("                    ", super.field);
        var42.print("One", super.field);
        var42.println("Two");
        var42.print("Sum of squares      ", super.field);
        var42.print(Fmath.truncate(var18[0], super.prec), super.field);
        var42.println(Fmath.truncate(var18[1], super.prec));
        var42.print("Degrees of freedom  ", super.field);
        var42.print(this.nAnalyteConcns - var19[0], super.field);
        var42.println(this.nAnalyteConcns - var19[1]);
        var42.println();
        switch (var25) {
            case 0:
                var42.printtab("Extra sum of squares F-ratio =                 ");
                var42.println(Fmath.truncate(var28, super.prec));
                var42.printtab("F-ratio probabilty =                           ");
                var42.println(Fmath.truncate(var30, super.prec));
                var42.printtab("F value at the " + var15 + " significance level = ");
                var42.println(Fmath.truncate(var32, super.prec));
                var42.println();
                if (var30 <= var15) {
                    var42.println("In terms of a best fit Equation Two is the preferred fit.");
                    if (Math.abs(var19[1] - var19[0]) == 1) {
                        var42.println("The additional parameter has, given a " + var15 + " significance level, significantly improved the fit.");
                    } else {
                        var42.println("The additional parameters have, given a " + var15 + " significance level, significantly improved the fit.");
                    }
                } else {
                    var42.println("In terms of a best fit Equation One is the preferred fit");
                    if (Math.abs(var19[1] - var19[0]) == 1) {
                        var42.println("The additional parameter has not, given a " + var15 + " significance level, significantly improved the fit.");
                    } else {
                        var42.println("The additional parameters have not, given a " + var15 + " significance level, significantly improved the fit.");
                    }
                }
                break;
            case 1:
                var42.println("The fittings to the two equations cannot be distinguished using an F-test analysis");
                break;
            case 2:
                var42.printtab("Variance F-ratio =                            ");
                var42.println(Fmath.truncate(var28, super.prec));
                var42.printtab("F-ratio probabilty =                          ");
                var42.println(Fmath.truncate(var30, super.prec));
                var42.printtab("F value at the " + var15 + " significance level = ");
                var42.println(Fmath.truncate(var32, super.prec));
                var42.println();
                if (var30 <= var15) {
                    var42.println("In terms of a best fit Equation Two is the preferred fit");
                    var42.println("as indicated by the F-ratio analysis and a given significance level of " + var15);
                } else {
                    var42.println("The fittings to the two equations cannot be distinguished using an F-test analysis");
                }
        }

        var42.println("However, the choice of the model to be used as a standard curve should include, along with this comparison,");
        var42.println("observation of the displayed graphs and of the detailed analyses listed below");
        var42.println();
        var42.println();
        var42.println("Details of the two compared fitting exercises");
        var42.println();
        FileInput var38 = new FileInput("ImmunoAssayTemp.txt");
        int var43 = var38.numberOfLines();
        String var40 = null;

        for (int var41 = 0; var41 < var43; ++var41) {
            var40 = var38.readLine();
            var42.println(var40);
        }

        var38.close();
        var42.println();
        var42.println("End of file");
        var42.close();
        this.deleteFile("ImmunoAssayTemp.txt");
    }

    private ArrayList<Object> fittingForCompare(int var1, int var2, double var3, double var5, int var7, boolean var8) {
        ArrayList var9 = new ArrayList();
        String var10 = null;
        switch (var1) {
            case 1:
                this.fiveParameterLogisticFit();
                var10 = this.methodNames[1];
                break;
            case 2:
                this.fiveParameterLogisticFit(var3, var5);
                var10 = this.methodNames[2];
                break;
            case 3:
                this.fourParameterLogisticFit();
                var10 = this.methodNames[3];
                break;
            case 4:
                this.fourParameterLogisticFit(var3, var5);
                var10 = this.methodNames[4];
                break;
            case 5:
                this.bestPolynomialFit();
                var10 = this.methodNames[5] + ": degree = " + this.bestPolyDegree;
                break;
            case 6:
                this.polynomialFit(var2);
                var10 = this.methodNames[6] + var2;
                break;
            case 7:
                this.nonIntegerPolynomialFit(var7);
                var10 = this.methodNames[7];
                break;
            case 8:
                this.sigmoidThresholdFit();
                var10 = this.methodNames[8];
                break;
            case 9:
                this.sipsSigmoidFit();
                var10 = this.methodNames[9];
                break;
            case 10:
                this.shiftedRectangularHyperbolaFit();
                var10 = this.methodNames[10];
                break;
            case 11:
                this.rectangularHyperbolaFit();
                var10 = this.methodNames[11];
                break;
            case 12:
//                this.amershamFit();
                var10 = this.methodNames[12];
                break;
            default:
                throw new IllegalArgumentException("Method number " + var1 + " not recognised");
        }

        FileOutput var11 = null;
        String var12 = "ImmunoAssayTemp.txt";
        if (var8) {
            var11 = new FileOutput(var12, 'a');
        } else {
            var11 = new FileOutput(var12);
        }

        this.commonPrint(var11);
        var11.println();
        var11.println();
        var11.close();
        var9.add(new Double(super.chiSquare));
        var9.add(new Integer(super.nParam));
        var9.add(var10);
        return var9;
    }

    public static ArrayList<Object> comparisonTest(int[] var0, double var1, int var3, int var4, double var5, int var7, int var8, double var9) {
        ArrayList var11 = new ArrayList();
        double var12 = 0.0D / 0.0;
        double var14 = 0.0D / 0.0;
        double var16 = 0.0D / 0.0;
        double[] var18 = new double[]{var1, var5};
        int[] var19 = new int[]{var3, var7};
        int[] var20 = new int[]{var4, var8};
        int[] var21 = new int[]{var4 - var3, var8 - var7};
        double[] var22 = new double[2];
        double var23 = 0.0D / 0.0;
        boolean var25 = false;
        boolean var26 = false;
        byte var27 = -1;
        double var28;
        int var30;
        if (var3 != var7 && var4 == var8) {
            if (var19[0] > var19[1]) {
                var28 = var18[0];
                var18[0] = var18[1];
                var18[1] = var28;
                var30 = var19[0];
                var19[0] = var19[1];
                var19[1] = var30;
                var30 = var20[0];
                var20[0] = var20[1];
                var20[1] = var30;
                var30 = var21[0];
                var21[0] = var21[1];
                var21[1] = var30;
                var30 = var0[0];
                var0[0] = var0[1];
                var0[1] = var30;
            }

            if (var18[0] > var18[1]) {
                var23 = var18[0] - var18[1];
                int var31 = var21[0] - var21[1];
                var12 = var23 * (double) var21[1] / ((double) var31 * var18[1]);
                var14 = Stat.fCompCDF(var12, var31, var21[1]);
                var16 = Stat.fTestValueGivenFprob(var9, var31, var21[1]);
                var27 = 0;
            }
        }

        if (var27 == -1) {
            if (var18[0] == var18[1] && var21[0] == var21[1]) {
                var27 = 1;
            } else {
                var22[0] = var18[0] / (double) var21[0];
                var22[1] = var18[1] / (double) var21[1];
                if (var22[0] <= var22[1]) {
                    var28 = var22[0];
                    var22[0] = var22[1];
                    var22[1] = var28;
                    var28 = var18[0];
                    var18[0] = var18[1];
                    var18[1] = var28;
                    var30 = var19[0];
                    var19[0] = var19[1];
                    var19[1] = var30;
                    var30 = var20[0];
                    var20[0] = var20[1];
                    var20[1] = var30;
                    var30 = var21[0];
                    var21[0] = var21[1];
                    var21[1] = var30;
                    var30 = var0[0];
                    var0[0] = var0[1];
                    var0[1] = var30;
                }

                var12 = var22[0] / var22[1];
                var14 = Stat.fCompCDF(var12, var21[0], var21[1]);
                var16 = Stat.fTestValueGivenFprob(var9, var21[0], var21[1]);
                var27 = 2;
            }
        }

        var11.add(new Integer(var27));
        var11.add(new Integer(var0[0]));
        var11.add(new Integer(var0[1]));
        var11.add(new Double(var12));
        var11.add(new Double(var14));
        var11.add(new Double(var16));
        var11.add(new Integer(var21[0]));
        var11.add(new Integer(var21[1]));
        var11.add(new Integer(var20[0]));
        var11.add(new Integer(var20[1]));
        return var11;
    }

    public void deleteFile(String var1) {
        boolean var2 = true;
        File var3 = new File(var1);
        if (!var3.exists()) {
            System.err.println("Method deleteFile: no file or directory of the name " + var1 + " found");
            var2 = false;
        }

        if (var2 && !var3.canWrite()) {
            System.err.println("Method deleteFile: " + var1 + " is write protected and cannot be deleted");
            var2 = false;
        }

        if (var2 && var3.isDirectory()) {
            String[] var4 = var3.list();
            if (var4.length > 0) {
                System.err.println("Method deleteFile: " + var1 + " is a directory which is not empty; no action was taken");
                var2 = false;
            }
        }

        if (var2) {
            var2 = var3.delete();
            if (!var2) {
                System.err.println("Method deleteFile: deletion of the temporary file " + var1 + " failed");
            }
        }

    }

    public static boolean isMonotonic(double[] var0) {
        boolean var1 = true;
        int var2 = var0.length;
        double var3 = var0[1] - var0[0];
        double var5 = var3;
        if (var3 == 0.0D) {
            var1 = false;
        } else {
            for (int var7 = 2; var7 < var2; ++var7) {
                var3 = var0[var7] - var0[var7 - 1];
                if (var3 == 0.0D) {
                    var1 = false;
                } else if (Fmath.sign(var3) != Fmath.sign(var5)) {
                    var1 = false;
                }

                if (!var1) {
                    break;
                }
            }
        }

        return var1;
    }

    public static double surfaceNumberConcn(double var0, double var2) {
        return ImmunoChemistry.surfaceNumberConcn(var0, var2);
    }

    public static double surfaceNumberConcn(double var0) {
        return ImmunoChemistry.surfaceNumberConcn(var0);
    }

    public static double surfaceMolarConcn(double var0, double var2) {
        return ImmunoChemistry.surfaceMolarConcn(var0, var2);
    }

    public static double surfaceMolarConcn(double var0) {
        return ImmunoChemistry.surfaceMolarConcn(var0);
    }

    public static double equivalentVolumeConcn(double var0, double var2, double var4, double var6) {
        return ImmunoChemistry.equivalentVolumeConcn(var0, var2, var4, var6);
    }

    public static double equivalentVolumeConcn(double var0, double var2, double var4) {
        return ImmunoChemistry.equivalentVolumeConcn(var0, var2, var4);
    }

    public static double convertSurfaceToVolumeConcn(double var0, double var2, double var4) {
        return ImmunoChemistry.convertSurfaceToVolumeConcn(var0, var2, var4);
    }

    public static double molecularRadius(double var0, double var2) {
        return ImmunoChemistry.molecularRadius(var0, var2);
    }

    public static double molecularRadius(double var0) {
        return ImmunoChemistry.molecularRadius(var0, 7.4E-4D);
    }

    public static double effectiveRadius(double var0, double var2, double var4) {
        return ImmunoChemistry.effectiveRadius(var0, var2, var4);
    }

    public static double getMolWeightIgG1() {
        return ImmunoChemistry.getMolWeightIgG1();
    }

    public static double getMolWeightIgG2() {
        return ImmunoChemistry.getMolWeightIgG2();
    }

    public static double getMolWeightIgG3() {
        return ImmunoChemistry.getMolWeightIgG3();
    }

    public static double getMolWeightIgG4() {
        return ImmunoChemistry.getMolWeightIgG4();
    }

    public static double getMolWeightIgM() {
        return ImmunoChemistry.getMolWeightIgM();
    }

    public static double getMolWeightIgA1() {
        return ImmunoChemistry.getMolWeightIgA1();
    }

    public static double getMolWeightIgA2() {
        return ImmunoChemistry.getMolWeightIgA2();
    }

    public static double getMolWeightIgD() {
        return ImmunoChemistry.getMolWeightIgD();
    }

    public static double getMolWeightIgE() {
        return ImmunoChemistry.getMolWeightIgE();
    }
}
