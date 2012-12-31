package org.rl337.skynet.optimizers;

import org.rl337.skynet.Hypothesis;
import org.rl337.math.types.Matrix;

public class PerceptronOptimizer extends AbstractBatchOptimizer {

    public PerceptronOptimizer(int batchSize) {
        super(Hypothesis.Perceptron, null, batchSize);
    }

    @Override
    public Matrix runBatch(Matrix theta, Matrix x, Matrix y) {
        Matrix guess = getHypothesis().guess(theta, x);
        for (int j = 0; j < guess.getRows(); j++) {
            double actualRaw = guess.getValue(j, 0);
            double actual = actualRaw >= 0.5 ? 1.0 : 0.0;
            double expected = y.getValue(j, 0);
            
            if (expected == actual) {
                continue;
            }
            
            Matrix m = x.sliceRow(j).transpose();
            if (expected == 0.0) {
                theta = theta.subtract(m);
            } else {
                theta = theta.add(m);
            }
        }
        
        return theta;
    }

}
