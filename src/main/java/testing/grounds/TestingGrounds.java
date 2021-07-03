package testing.grounds;

import flanagan.io.Db;
import flanagan.physchem.ImmunoAssay;

public class TestingGrounds {

    public static void main(String[] args) {

        // Title
        String title = "ImmunoAssay Example One";

        // Analyte concentrations
        double[] analyteConcentrations = {444, 1333.3, 4000, 12000};
        // assay responses
        double[] assayResponses = {100-63.393, 100-50.865, 100-33.483, 100-26};
//        double[] assayResponses = {0.0, 0.05, 0.1, 0.6, 1.0, 1.5, 2.0, 2.5, 3.0, 3.4, 3.5, 3.7, 3.9, 3.94, 3.95};

        // Create an instance of ImmunoAssay
        DrugAssay assay = new DrugAssay(title);

        // Enter analyte concentrations
        assay.enterAnalyteConcns(analyteConcentrations);

        // Enter assay responses
        assay.enterResponses(assayResponses);

        // Fit assay data to a five parameter logistic function
        assay.fourParameterLogisticFit(0, 100);

        // Print an analysis of the fit to a test file named ImmunoAssayOneOutput.txt
        assay.print("ImmunoAssayOneOutput.txt");

        // Find concentration for a given response i.e. of a sample of unknown concentration
        double sampleResponse = Db.readDouble("Sample assay response");
        double sampleConcn = assay.getSampleConcn(sampleResponse);
        double sampleError = assay.getSampleConcnError();

        // Display the estimated sample concentration and its estimated error
        System.out.println("Sample assay response = " + sampleResponse);
        System.out.println("Estimated sample analyte concentration = " + sampleConcn);
        System.out.println("Estimated concentration error = " + sampleError);
    }
}

