package com.dmitrybrant.modelviewer;

import android.opengl.Matrix;
import android.support.annotation.NonNull;



public abstract class Model1 {
    // Center of mass to be populated by subclasses
    protected float centerMassX1;
    protected float centerMassY1;
    protected float centerMassZ1;
    protected float floorOffset1;

    @NonNull
    private String title;

    protected int glProgram1 = -1;
    protected float[] modelMatrix1 = new float[16];
    protected float[] mvMatrix1 = new float[16];
    protected float[] mvpMatrix1 = new float[16];



    protected float maxX;
    protected float maxY;
    protected float maxZ;
    protected float minX;
    protected float minY;
    protected float minZ;

    public Model1() {
        maxX = Float.MIN_VALUE;
        maxY = Float.MIN_VALUE;
        maxZ = Float.MIN_VALUE;
        minX = Float.MAX_VALUE;
        minY = Float.MAX_VALUE;
        minZ = Float.MAX_VALUE;
        title = "";
    }

    public void init(float boundSize) {
        initModelMatrix1(boundSize);
    }

    @NonNull public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    protected void initModelMatrix1(float boundSize) {
        initModelMatrix1(boundSize, 0.0f, 0.0f, 0.0f);
    }

    protected void initModelMatrix1(float boundSize, float rotateX, float rotateY, float rotateZ) {
         Matrix.setIdentityM(modelMatrix1, 0);
        Matrix.rotateM(modelMatrix1, 0, rotateX, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(modelMatrix1, 0, rotateY, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(modelMatrix1, 0, rotateZ, 0.0f, 0.0f, 1.0f);
        scaleModelMatrixToBounds(boundSize);
        Matrix.translateM(modelMatrix1, 0,-centerMassX1,-centerMassY1,-centerMassZ1);
    }

    public float[] getModelMatrix() {
        return modelMatrix1;
    }

    public float getFloorOffset() {
        return floorOffset1;
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
            Matrix.scaleM(modelMatrix1, 0, scale, scale, scale);
        }
    }
}
