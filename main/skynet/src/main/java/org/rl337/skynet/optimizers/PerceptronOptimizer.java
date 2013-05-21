package org.rl337.skynet.optimizers;

import org.rl337.skynet.Hypothesis;
import org.rl337.math.types.Matrix;

public class PerceptronOptimizer extends AbstractBatchOptimizer {
    private double mAlpha;

    public PerceptronOptimizer(int batchSize, double alpha) {
        super(Hypothesis.Perceptron, null, batchSize);
        mAlpha = alpha;
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
                theta = theta.subtract(m.multiply(mAlpha));
            } else {
                theta = theta.add(m.multiply(1.0 - mAlpha));
            }
        }
        
        return theta;
    }

}
