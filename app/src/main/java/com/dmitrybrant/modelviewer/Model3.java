package com.dmitrybrant.modelviewer;

import android.opengl.Matrix;
import android.support.annotation.NonNull;



public abstract class Model3 {
    // Center of mass to be populated by subclasses
    protected float centerMassX3;
    protected float centerMassY3;
    protected float centerMassZ3;
    protected float floorOffset3;

    @NonNull
    private String title;

    protected int glProgram1 = -1;
    protected float[] modelMatrix3 = new float[16];
    protected float[] mvMatrix3 = new float[16];
    protected float[] mvpMatrix3 = new float[16];



    protected float maxX;
    protected float maxY;
    protected float maxZ;
    protected float minX;
    protected float minY;
    protected float minZ;

    public Model3() {
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
        Matrix.setIdentityM(modelMatrix3, 0);
        Matrix.rotateM(modelMatrix3, 0, rotateX, 1.0f, 1.5f, 2.0f);
        Matrix.rotateM(modelMatrix3, 0, rotateY, 1.5f, 2.0f, 1.0f);
        Matrix.rotateM(modelMatrix3, 0, rotateZ, 2.0f, 1.0f, 1.5f);
        scaleModelMatrixToBounds(boundSize);
        Matrix.translateM(modelMatrix3, 0,-centerMassX3,-centerMassY3,-centerMassZ3);
    }

    public float[] getModelMatrix() {
        return modelMatrix3;
    }

    public float getFloorOffset() {
        return floorOffset3;
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
            Matrix.scaleM(modelMatrix3, 0, scale, scale, scale);
        }
    }
}
