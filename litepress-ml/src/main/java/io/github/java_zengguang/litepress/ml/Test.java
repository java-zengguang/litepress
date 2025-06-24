package io.github.java_zengguang.litepress.ml;

import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.transforms.Pad;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Test {


    public void createModel() throws IOException {

        // Define the network configuration
        NeuralNetConfiguration.ListBuilder listBuilder = new NeuralNetConfiguration.Builder()
                .seed(123) // Set random seed for reproducibility
                .updater(new Adam(0.001)) // Use Adam optimizer with learning rate 0.001
                .list();

        int numInputs = 784; // Example: MNIST image input size (28x28=784)
        int hiddenLayerSize = 512;
        int numOutputs = 10; // Example: MNIST has 10 classes

// Add layers to the network
        listBuilder.layer(new DenseLayer.Builder().nIn(numInputs).nOut(hiddenLayerSize)
                .activation(Activation.RELU)
                .build());

        listBuilder.layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                .activation(Activation.SOFTMAX)
                .nIn(hiddenLayerSize).nOut(numOutputs)
                .build());

        MultiLayerNetwork model = new MultiLayerNetwork(listBuilder.build());
        model.init();


        //准备数据

        // Define batch size and number of epochs
        int batchSize = 64;
        int nEpochs = 10;

// Create data iterators for training and testing sets
        DataSetIterator mnistTrain = new MnistDataSetIterator(batchSize, true, 12345);
        DataSetIterator mnistTest = new MnistDataSetIterator(batchSize, false, 12345);



        //训练
        for (int i = 0; i < nEpochs; i++) {
            Logger.info("Completed epoch begin " + i);
            model.fit(mnistTrain);
            Logger.info("Completed epoch " + i);
        }

        //测试
        Evaluation eval = model.evaluate(mnistTest);
        Logger.info(eval.stats());



// Save the model to a file
        File locationToSave = new File("/home/zengguang/model.zip");
        boolean saveUpdater = true; // Whether to save the updater (Adam in this case)

        try {
            ModelSerializer.writeModel(model, locationToSave, saveUpdater);
            Logger.info("Model saved successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static  void main(String args[]) throws IOException {
        Test test=new Test();
        test.createModel();
    }

    //TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
    // 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
    public static class Main {
        public static void main(String[] args) {
            int nRows = 3;
            int nColumns = 5;
            INDArray zeros= Nd4j.zeros(10,10);
            INDArray ones= Nd4j.ones(10,10);
            INDArray combined = Nd4j.concat(0,zeros,ones);

            INDArray padded = Nd4j.pad(ones, new int[]{1,1}, Pad.Mode.CONSTANT, 9 );
            INDArray hstack = Nd4j.hstack(ones,zeros);
            INDArray vstack = Nd4j.vstack(ones,zeros);
            INDArray allTens = Nd4j.valueArrayOf(3, 5, 10.0);



            double[] vectorDouble = new double[]{1,2,3};
            INDArray rowVector = Nd4j.create(vectorDouble);


            INDArray columnVector = Nd4j.create(vectorDouble, 3,1);  //Manually specify: 3 rows, 1 column


            double[][] matrixDouble = new double[][]{
                    {1.0, 2.0, 3.0},
                    {4.0, 5.0, 6.0}};
            INDArray matrix = Nd4j.create(matrixDouble);
            Logger.info("\nINDArray defined from double[][]:");
            Logger.info(matrix);

            int[] shape = new int[]{nRows, nColumns};
            long[] shape_long = new long []{nRows, nColumns};
            INDArray uniformRandom = Nd4j.rand(shape_long);
            Logger.info("\n\n\nUniform random array:");
            Logger.info(uniformRandom);
            Logger.info("Full precision of random value at position (0,0): " + uniformRandom.getDouble(0,0));

            INDArray gaussianMeanZeroUnitVariance = Nd4j.randn(shape);
            Logger.info("\nN(0,1) random array:");
            Logger.info(gaussianMeanZeroUnitVariance);


            //We can make things repeatable using RNG seed:
            long rngSeed = 12345;
            INDArray uniformRandom2 = Nd4j.randn(rngSeed, shape_long);
            INDArray uniformRandom3 = Nd4j.randn(rngSeed, shape_long);
            Logger.info("\nUniform random arrays with same fixed seed:");
            Logger.info(uniformRandom2);
            Logger.info("");
            Logger.info(uniformRandom3);


            //Of course, we aren't restricted to 2d. 3d or higher is easy:
            INDArray threeDimArray = Nd4j.ones(3,4,5);      //3x4x5 INDArray
            INDArray fourDimArray = Nd4j.ones(3,4,5,6);     //3x4x5x6 INDArray
            INDArray fiveDimArray = Nd4j.ones(3,4,5,6,7);   //3x4x5x6x7 INDArray
            Logger.info("\n\n\nCreating INDArrays with more dimensions:");
            Logger.info("3d array shape:         " + Arrays.toString(threeDimArray.shape()));
            Logger.info("4d array shape:         " + Arrays.toString(fourDimArray.shape()));
            Logger.info("5d array shape:         " + Arrays.toString(fiveDimArray.shape()));


            //We can create INDArrays by combining other INDArrays, too:
            INDArray rowVector1 = Nd4j.create(new double[]{1,2,3});
            INDArray rowVector2 = Nd4j.create(new double[]{4,5,6});

            INDArray vStack = Nd4j.vstack(rowVector1, rowVector2);      //Vertical stack:   [1,3]+[1,3] to [2,3]
            INDArray hStack = Nd4j.hstack(rowVector1, rowVector2);      //Horizontal stack: [1,3]+[1,3] to [1,6]
            Logger.info("\n\n\nCreating INDArrays from other INDArrays, using hstack and vstack:");
            Logger.info("vStack:\n" + vStack);
            Logger.info("hStack:\n" + hStack);



            //There's some other miscellaneous methods, too:
            INDArray identityMatrix = Nd4j.eye(3);
            Logger.info("\n\n\nNd4j.eye(3):\n" + identityMatrix);
            INDArray linspace = Nd4j.linspace(1,10,10);                 //Values 1 to 10, in 10 steps
            Logger.info("Nd4j.linspace(1,10,10):\n" + linspace);
            INDArray diagMatrix = Nd4j.diag(rowVector2);                //Create square matrix, with rowVector2 along the diagonal
            Logger.info("Nd4j.diag(rowVector2):\n" + diagMatrix);

            INDArray originalArray = Nd4j.linspace(1,15,15).reshape('c',3,5);
            Logger.info("Original Array:");
            Logger.info(originalArray);

            //We can use getRow and getColumn operations to get a row or column respectively:
            INDArray firstRow = originalArray.getRow(0);
            INDArray lastColumn = originalArray.getColumn(4);
            Logger.info("");
            Logger.info("First row:\n" + firstRow);
            Logger.info("Last column:\n" + lastColumn);
            //Careful of the printing here: lastColumn looks like a row vector when printed, but it's really a column vector
            Logger.info("Shapes:         " + Arrays.toString(firstRow.shape()) + "\t" + Arrays.toString(lastColumn.shape()));


            //A key concept in ND4J is the idea of views: one INDArray may point to the same locations in memory as other arrays
            //For example, getRow and getColumn are both views of originalArray
            //Consequently, changes to one results in changes to the other:
            firstRow.addi(1.0);             //In-place addition operation: changes the values of both firstRow AND originalArray:
            Logger.info("\n\n");
            Logger.info("firstRow, after addi operation:");
            Logger.info(firstRow);
            Logger.info("originalArray, after firstRow.addi(1.0) operation: (note it is modified, as firstRow is a view of originalArray)");
            Logger.info(originalArray);


            //Let's recreate our our original array for the next section...
            originalArray = Nd4j.linspace(1,15,15).reshape('c',3,5);


            //We can select arbitrary subsets, using INDArray indexing:
            //All rows, first 3 columns (note that internal here is columns 0 inclusive to 3 exclusive)
            INDArray first3Columns = originalArray.get(NDArrayIndex.all(), NDArrayIndex.interval(0,3));
            Logger.info("first 3 columns:\n" + first3Columns);
            //Again, this is also a view:
            first3Columns.addi(100);
            Logger.info("originalArray, after first3Columns.addi(100)");
            Logger.info(originalArray);

            //We can similarly set arbitrary subsets.
            //Let's set the 3rd column (index 2) to zeros:
            INDArray zerosColumn = Nd4j.zeros(3,1);
            originalArray.put(new INDArrayIndex[]{NDArrayIndex.all(), NDArrayIndex.point(2)}, zerosColumn);     //All rows, column index 2
            Logger.info("\n\n\nOriginal array, after put operation:\n" + originalArray);



            //Let's recreate our our original array for the next section...
            originalArray = Nd4j.linspace(1,15,15).reshape('c',3,5);


            //Sometimes, we don't want this in-place behaviour. In this case: just add a .dup() operation at the end
            //the .dup() operation - aka 'duplicate' - creates a new and separate array
            INDArray firstRowDup = originalArray.getRow(0).dup();   //We now have a copy of the first row. i.e., firstRowDup is NOT a view of originalArray
            firstRowDup.addi(100);
            Logger.info("\n\n\n");
            Logger.info("firstRowDup, after .addi(100):\n" + firstRowDup);
            Logger.info("originalArray, after firstRowDup.addi(100): (note it is unmodified)\n" + originalArray);


             originalArray = Nd4j.linspace(1,15,15).reshape('c',3,5);       //As per example 3
            INDArray copyAdd = originalArray.add(1.0);
            Logger.info("Same object returned by add:    " + (originalArray == copyAdd));
            Logger.info("Original array after originalArray.add(1.0):\n" + originalArray);
            Logger.info("copyAdd array:\n" + copyAdd);

            //Let's do the same thing with the in-place add operation:
            INDArray inPlaceAdd = originalArray.addi(1.0);
            Logger.info("");
            Logger.info("Same object returned by addi:    " + (originalArray == inPlaceAdd));    //addi returns the exact same Java object
            Logger.info("Original array after originalArray.addi(1.0):\n" + originalArray);
            Logger.info("inPlaceAdd array:\n" + copyAdd);

            //Let's recreate our our original array for the next section, and create another one:
            originalArray = Nd4j.linspace(1,15,15).reshape('c',3,5);
            INDArray random = Nd4j.rand(3,5);               //See example 2; we have a 3x5 with uniform random (0 to 1) values



            //We can perform element-wise operations. Note that the array shapes must match here
            // add vs. addi works in exactly the same way as for scalars
            INDArray added = originalArray.add(random);
            Logger.info("\n\n\nRandom values:\n" + random);
            Logger.info("Original plus random values:\n" + added);



            //Matrix multiplication is easy:
            INDArray first = Nd4j.rand(3,4);
            INDArray second = Nd4j.rand(4,5);
            INDArray mmul = first.mmul(second);
            Logger.info("\n\n\nShape of mmul array:      " + Arrays.toString(mmul.shape()));     //3x5 output as expected



        }
    }
}
