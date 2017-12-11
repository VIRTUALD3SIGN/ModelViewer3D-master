package com.dmitrybrant.modelviewer;

import android.opengl.Matrix;
import android.support.annotation.NonNull;



public abstract class Model2 {
    // Center of mass to be populated by subclasses
    protected float centerMassX2;
    protected float centerMassY2;
    protected float centerMassZ2;
    protected float floorOffset2;

    @NonNull
    private String title;

    protected int glProgram1 = -1;
    protected float[] modelMatrix2 = new float[16];
    protected float[] mvMatrix2 = new float[16];
    protected float[] mvpMatrix2 = new float[16];



    protected float maxX;
    protected float maxY;
    protected float maxZ;
    protected float minX;
    protected float minY;
    protected float minZ;

    public Model2() {
        maxX = Float.MIN_VALUE;
        maxY = Float.MIN_VALUE;
        maxZ = Float.MIN_VALUE;
        minX = Float.MAX_VALUE;
        minY = Float.MAX_VALUE;
        minZ = Float.MAX_VALUE;
        title = "";
    }

    public void init(float boundSize) {
        initModelMatrix2(boundSize);
    }

    @NonNull public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    protected void initModelMatrix2(float boundSize) {
        initModelMatrix2(boundSize, 0.0f, 0.0f, 0.0f);
    }

    protected void initModelMatrix2(float boundSize, float rotateX, float rotateY, float rotateZ) {
        Matrix.setIdentityM(modelMatrix2, 0);
        Matrix.rotateM(modelMatrix2, 0, rotateX, 1.0f, 0.0f, 2.0f);
        Matrix.rotateM(modelMatrix2, 0, rotateY, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(modelMatrix2, 0, rotateZ, 0.0f, 2.0f, 1.0f);
        scaleModelMatrixToBounds(boundSize);
        Matrix.translateM(modelMatrix2, 0,-centerMassX2,-centerMassY2,-centerMassZ2);
    }

    public float[] getModelMatrix() {
        return modelMatrix2;
    }

    public float getFloorOffset() {
        return floorOffset2;
    }

    abstract public void draw(float[] viewMatrix, float[] projectionMatrix, @NonNull Light light);

    protected void adjustMaxMin(float x, float y, float z) {
        if (x > maxX) {
            maxX = x;
        }
        if (y > maxY) {
            maxY = y;
        }
        if (z > maxZ) {
            maxZ = z;
        }
        if (x < minX) {
            minX = x;
        }
        if (y < minY) {
            minY = y;
        }
        if (z < minZ) {
            minZ = z;
        }
    }

    protected float getBoundScale(float boundSize) {
        float scaleX = (maxX - minX) / boundSize;
        float scaleY = (maxY - minY) / boundSize;
        float scaleZ = (maxZ - minZ) / boundSize;
        float scale = scaleX;
        if (scaleY > scale) {
            scale = scaleY;
        }
        if (scaleZ > scale) {
            scale = scaleZ;
        }
        return scale;
    }

    private void scaleModelMatrixToBounds(float boundSize) {
        float scale = getBoundScale(boundSize);
        if (scale != 0f) {
            scale = 1f / scale;
            Matrix.scaleM(modelMatrix2, 0, scale, scale, scale);
        }
    }
}
