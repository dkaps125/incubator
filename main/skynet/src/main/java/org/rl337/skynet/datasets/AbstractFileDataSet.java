package org.rl337.skynet.datasets;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import org.rl337.skynet.DataSet;
import org.rl337.math.types.Matrix;

public abstract class AbstractFileDataSet extends AbstractDataSet implements DataSet {
    DataInputStream mInput;
    boolean mEOF;
    boolean mAddBias;
    protected AbstractFileDataSet(File f, boolean addBias, Random random) throws IOException {
        super(random);
        mInput = getInputStream(f);
        mEOF = false;
        mAddBias = addBias;
    }
    
    protected AbstractFileDataSet(File f) throws IOException {
        this(f, false, null);
    }
    
    protected AbstractFileDataSet(File f, Random rand) throws IOException {
        this(f, false, rand);
    }
    
    private DataInputStream getInputStream(File file) throws IOException {
        String filename = file.getAbsolutePath();
        
        InputStream inputStream = new FileInputStream(file);
        if (filename.endsWith(".zip")) {
            inputStream = new ZipInputStream(inputStream);
        } else if (filename.endsWith(".gz")) {
            inputStream = new GZIPInputStream(inputStream);
        }
        
        DataInputStream result = new DataInputStream(inputStream);
        
        readHeaders(result);
        
        return result;
    }
    
    protected abstract void readHeaders(DataInput is) throws IOException;
    protected abstract Matrix readNextBatch(DataInput is, int size) throws IOException;
    protected abstract Matrix readAll(DataInput is) throws IOException;
    protected abstract Matrix readAllSubsampled(DataInput is, double prob) throws IOException;

    public Matrix getNextBatch(int size) {
        try {
            Matrix data = readNextBatch(mInput, size);
            if (mAddBias) {
                Matrix bias = Matrix.ones(data.getRows(), 1);
                data = bias.appendColumns(data);
            }
            
            return data;
        } catch (IOException e) {
            close();
            return null;
        }
    }

    public Matrix getAll() {
        try {
            Matrix data = readAll(mInput);
            if (mAddBias) {
                Matrix bias = Matrix.ones(data.getRows(), 1);
                data = bias.appendColumns(data);
            }
            
            return data;
        } catch (EOFException e) {
            mEOF = true;
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            close();
        }
    }
    

    public Matrix getSubsample(double samplePercent) {
        try {
            Matrix data = readAllSubsampled(mInput, samplePercent);
            if (mAddBias) {
                Matrix bias = Matrix.ones(data.getRows(), 1);
                data = bias.appendColumns(data);
            }
            
            return data;
        } catch (EOFException e) {
            mEOF = true;
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            close();
        }
    }
    
    public void close() {
        try {
            mEOF = true;
            mInput.close();
        } catch (IOException e) {
        }
    }

    public boolean hasMore() {
        return !mEOF;
    }
}
