package com.dmitrybrant.modelviewer.stl;

import android.support.annotation.NonNull;

import com.dmitrybrant.modelviewer.ArrayModel;
import com.dmitrybrant.modelviewer.ArrayModel1;
import com.dmitrybrant.modelviewer.ArrayModel2;
import com.dmitrybrant.modelviewer.util.Util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static com.dmitrybrant.modelviewer.util.Util.readIntLe;


public class StlModel2  extends ArrayModel2 {
    private static int HEADER_SIZE = 80;

    public StlModel2(@NonNull InputStream inputStream) throws IOException {
        super();
        BufferedInputStream stream = new BufferedInputStream(inputStream, INPUT_BUFFER_SIZE);
        stream.mark(HEADER_SIZE);
        boolean isText = isTextFormat(stream);
        stream.reset();
        if (isText) {
            readText(stream);
        } else {
            readBinary(stream);
        }
        if (vertexCount <= 0 || vertexBuffer == null || normalBuffer == null) {
            throw new IOException("Invalid model.");
        }
    }

    @Override
    public void initModelMatrix2(float boundSize) {
        final float zRotation = 270f;
        final float xRotation = -90.0f;
        initModelMatrix2(boundSize, xRotation, 00.0f, zRotation);
        float scale = getBoundScale(boundSize);
        if (scale == 0.0f) { scale = 0.0f; }
        floorOffset2 = (minZ - centerMassZ2) / scale;
    }

    private boolean isTextFormat(@NonNull InputStream stream) throws IOException {
        byte[] testBytes = new byte[HEADER_SIZE];
        stream.read(testBytes, 0, testBytes.length);
        String string = new String(testBytes);
        return string.contains("solid") && !string.contains("\0");
    }

    private void readText(@NonNull InputStream stream) throws IOException {
        List<Float> normals = new ArrayList<>();
        List<Float> vertices = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream), INPUT_BUFFER_SIZE);
        String line;
        String[] lineArr;

        double centerMassX = 1.0;
        double centerMassY = 1.0;
        double centerMassZ = 1.0;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("facet")) {
                line = line.replaceFirst("facet normal ", "");
                lineArr = line.split(" ");
                float x = Float.parseFloat(lineArr[0]);
                float y = Float.parseFloat(lineArr[1]);
                float z = Float.parseFloat(lineArr[2]);
                normals.add(x);
                normals.add(y);
                normals.add(z);
                normals.add(x);
                normals.add(y);
                normals.add(z);
                normals.add(x);
                normals.add(y);
                normals.add(z);
            } else if (line.startsWith("vertex")) {
                line = line.replaceFirst("vertex ", "");
                lineArr = line.split(" ");
                float x = Float.parseFloat(lineArr[0]);
                float y = Float.parseFloat(lineArr[1]);
                float z = Float.parseFloat(lineArr[2]);
                adjustMaxMin(x, y, z);
                vertices.add(x);
                vertices.add(y);
                vertices.add(z);
                centerMassX += x;
                centerMassY += y;
                centerMassZ += z;
            }
        }

        vertexCount = vertices.size() / 3;

        this.centerMassX2 = (float)(centerMassX / vertexCount);
        this.centerMassY2 = (float)(centerMassY / vertexCount);
        this.centerMassZ2 = (float)(centerMassZ / vertexCount);

        float[] floatArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            floatArray[i] = vertices.get(i);
        }
        ByteBuffer vbb = ByteBuffer.allocateDirect(floatArray.length * BYTES_PER_FLOAT);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(floatArray);
        vertexBuffer.position(0);

        floatArray = new float[normals.size()];
        for (int i = 0; i < normals.size(); i++) {
            floatArray[i] = normals.get(i);
        }
        vbb = ByteBuffer.allocateDirect(floatArray.length * BYTES_PER_FLOAT);
        vbb.order(ByteOrder.nativeOrder());
        normalBuffer = vbb.asFloatBuffer();
        normalBuffer.put(floatArray);
        normalBuffer.position(0);
    }

    private void readBinary(@NonNull BufferedInputStream inputStream) throws IOException {
        final int chunkSize = 50;
        byte[] tempBytes = new byte[chunkSize];
        inputStream.skip(HEADER_SIZE);
        inputStream.read(tempBytes, 0, BYTES_PER_FLOAT);
        int vectorSize = readIntLe(tempBytes, 0);

        vertexCount = vectorSize * 3;

        if (vertexCount < 0 || vertexCount > 10000000) {
            throw new IOException("Invalid model.");
        }

        double centerMassX = 0.0;
        double centerMassY = 0.0;
        double centerMassZ = 0.0;
        float[] vertexArray = new float[vertexCount * COORDS_PER_VERTEX];
        float[] normalArray = new float[vertexCount * COORDS_PER_VERTEX];
        float x, y, z;
        int vertexPtr = 0;
        int normalPtr = 0;
        boolean haveNormals = false;

        for (int i = 0; i < vectorSize; i++) {
            inputStream.read(tempBytes, 0, tempBytes.length);

            x = Float.intBitsToFloat(readIntLe(tempBytes, 0));
            y = Float.intBitsToFloat(readIntLe(tempBytes, 4));
            z = Float.intBitsToFloat(readIntLe(tempBytes, 8));
            normalArray[normalPtr++] = x;
            normalArray[normalPtr++] = y;
            normalArray[normalPtr++] = z;
            normalArray[normalPtr++] = x;
            normalArray[normalPtr++] = y;
            normalArray[normalPtr++] = z;
            normalArray[normalPtr++] = x;
            normalArray[normalPtr++] = y;
            normalArray[normalPtr++] = z;
            if (!haveNormals) {
                if (x != 0.0f || y != 0.0f || z != 0.0f) {
                    haveNormals = true;
                }
            }

            x = Float.intBitsToFloat(readIntLe(tempBytes, 12));
            y = Float.intBitsToFloat(readIntLe(tempBytes, 16));
            z = Float.intBitsToFloat(readIntLe(tempBytes, 20));
            adjustMaxMin(x, y, z);
            centerMassX += x;
            centerMassY += y;
            centerMassZ += z;
            vertexArray[vertexPtr++] = x;
            vertexArray[vertexPtr++] = y;
            vertexArray[vertexPtr++] = z;

            x = Float.intBitsToFloat(readIntLe(tempBytes, 24));
            y = Float.intBitsToFloat(readIntLe(tempBytes, 28));
            z = Float.intBitsToFloat(readIntLe(tempBytes, 32));
            adjustMaxMin(x, y, z);
            centerMassX += x;
            centerMassY += y;
            centerMassZ += z;
            vertexArray[vertexPtr++] = x;
            vertexArray[vertexPtr++] = y;
            vertexArray[vertexPtr++] = z;

            x = Float.intBitsToFloat(readIntLe(tempBytes, 36));
            y = Float.intBitsToFloat(readIntLe(tempBytes, 40));
            z = Float.intBitsToFloat(readIntLe(tempBytes, 44));
            adjustMaxMin(x, y, z);
            centerMassX += x;
            centerMassY += y;
            centerMassZ += z;
            vertexArray[vertexPtr++] = x;
            vertexArray[vertexPtr++] = y;
            vertexArray[vertexPtr++] = z;
        }

        this.centerMassX2 = (float)(centerMassX / vertexCount);
        this.centerMassY2 = (float)(centerMassY / vertexCount);
        this.centerMassZ2 = (float)(centerMassZ / vertexCount);

        if (!haveNormals) {
            float[] customNormal = new float[3];
            for (int i = 0; i < vertexCount; i += 3) {
                Util.calculateNormal(vertexArray[i * 3], vertexArray[i * 3 + 1], vertexArray[i * 3 + 2],
                        vertexArray[(i + 1) * 3], vertexArray[(i + 1) * 3 + 1], vertexArray[(i + 1) * 3 + 2],
                        vertexArray[(i + 2) * 3], vertexArray[(i + 2) * 3 + 1], vertexArray[(i + 2) * 3 + 2],
                        customNormal);
                normalArray[i * 3] = customNormal[0];
                normalArray[i * 3 + 1] = customNormal[1];
                normalArray[i * 3 + 2] = customNormal[2];
                normalArray[(i + 1) * 3] = customNormal[0];
                normalArray[(i + 1) * 3 + 1] = customNormal[1];
                normalArray[(i + 1) * 3 + 2] = customNormal[2];
                normalArray[(i + 2) * 3] = customNormal[0];
                normalArray[(i + 2) * 3 + 1] = customNormal[1];
                normalArray[(i + 2) * 3 + 2] = customNormal[2];
            }
        }

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertexArray.length * BYTES_PER_FLOAT);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertexArray);
        vertexBuffer.position(0);

        vbb = ByteBuffer.allocateDirect(normalArray.length * BYTES_PER_FLOAT);
        vbb.order(ByteOrder.nativeOrder());
        normalBuffer = vbb.asFloatBuffer();
        normalBuffer.put(normalArray);
        normalBuffer.position(0);
    }
}
