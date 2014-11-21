package com.example.ggunnssakac.cube2_returnofthecube;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by ggunn on 21/11/14.
 */
public class ChildRenderer extends AbstractGLRenderer
{

    private final Context context;
    private int width, height;

    private float[] mModelMatrix = new float[16];

    private final FloatBuffer mModelPositions;
    private final FloatBuffer mModelColors;
    private final FloatBuffer mModelTextureCoordinates;

    private int mTextureUniformHandle;
    private int mTextureCoordinateHandle;

    private int mProgramHandle;
    private int mTextureDataHandle;

    final float dec = -0.5f;
    private float acc;
    private int FPS = 60;

    public ChildRenderer(Context context)
    {
        this.context = context;

        final float[] cubePositionData =
                {
                        // In OpenGL counter-clockwise winding is default. This means that when we look at a triangle,
                        // if the points are counter-clockwise we are looking at the "front". If not we are looking at
                        // the back. OpenGL has an optimization where all back-facing triangles are culled, since they
                        // usually represent the backside of an object and aren't visible anyways.

                        // Front face
                        -1.0f, 1.0f, 1.0f,
                        -1.0f, -1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f,
                        -1.0f, -1.0f, 1.0f,
                        1.0f, -1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f,

                        // Right face
                        1.0f, 1.0f, 1.0f,
                        1.0f, -1.0f, 1.0f,
                        1.0f, 1.0f, -1.0f,
                        1.0f, -1.0f, 1.0f,
                        1.0f, -1.0f, -1.0f,
                        1.0f, 1.0f, -1.0f,

                        // Back face
                        1.0f, 1.0f, -1.0f,
                        1.0f, -1.0f, -1.0f,
                        -1.0f, 1.0f, -1.0f,
                        1.0f, -1.0f, -1.0f,
                        -1.0f, -1.0f, -1.0f,
                        -1.0f, 1.0f, -1.0f,

                        // Left face
                        -1.0f, 1.0f, -1.0f,
                        -1.0f, -1.0f, -1.0f,
                        -1.0f, 1.0f, 1.0f,
                        -1.0f, -1.0f, -1.0f,
                        -1.0f, -1.0f, 1.0f,
                        -1.0f, 1.0f, 1.0f,

                        // Top face
                        -1.0f, 1.0f, -1.0f,
                        -1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, -1.0f,
                        -1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, -1.0f,

                        // Bottom face
                        1.0f, -1.0f, -1.0f,
                        1.0f, -1.0f, 1.0f,
                        -1.0f, -1.0f, -1.0f,
                        1.0f, -1.0f, 1.0f,
                        -1.0f, -1.0f, 1.0f,
                        -1.0f, -1.0f, -1.0f,
                };

        // R, G, B, A
        final float[] cubeColorData =
                {
                        // Front face (red)
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,

                        // Right face (green)
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,

                        // Back face (blue)
                        0.0f, 1.0f, 1.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f,

                        // Left face (yellow)
                        1.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 1.0f, 0.0f, 1.0f,

                        // Top face (cyan)
                        0.0f, 1.0f, 1.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f,

                        // Bottom face (magenta)
                        1.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f
                };
        // S, T (or X, Y)
        // Texture coordinate data.
        // Because images have a Y axis pointing downward (values increase as you move down the image) while
        // OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
        // What's more is that the texture coordinates are the same for every face.
        final float[] cubeTextureCoordinateData =
                {
                        // Front face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        // Right face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        // Back face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        // Left face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        // Top face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        // Bottom face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f
                };

        mModelPositions = ByteBuffer.allocateDirect(cubePositionData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mModelColors = ByteBuffer.allocateDirect(cubeColorData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mModelTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();

        mModelPositions.put(cubePositionData).position(0);
        mModelColors.put(cubeColorData).position(0);
        mModelTextureCoordinates.put(cubeTextureCoordinateData).position(0);
    }

    @Override
    public String getVertexShader() {
        return null;
    }

    @Override
    public String getFragmentShader() {
        return null;
    }

    @Override
    public String getVertexShader(int resId) {
        return ResourceReader.getString(context, resId);
    }

    @Override
    public String getFragmentShader(int resId) {
        return ResourceReader.getString(context, resId);
    }

    @Override
    public void setViewMatrix() {
        // Position the eye in front of the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = (float)-1F; //y = -1.75x + 4.25 near = 1.0f, far = 30.0f

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = 0.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

    }

    @Override
    public void setProjectionMatrix(int i, int j) {
        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) i / j;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0F;
        final float top = 1.0F;
        final float near = 1.0f;
        final float far = 30.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);

    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        super.onSurfaceCreated(glUnused, config);
        // Set the background clear color to black.
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Use culling to remove back faces.
        GLES20.glEnable(GLES20.GL_CULL_FACE);

        // Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        this.setViewMatrix();

        final String vertexShader = getVertexShader(R.raw.per_point_vertex_shader);
        final String fragmentShader = getFragmentShader(R.raw.per_point_fragment_shader);

        final int vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        mProgramHandle = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {"a_Position",  "a_Color", "a_TexCoordinate"});

        mTextureDataHandle = ResourceReader.loadTexture(context, R.drawable.cube);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        super.onSurfaceChanged(glUnused, width, height);
        this.setProjectionMatrix(width, height);
    }

    public float VELOCITY_SCALE = 1F;
    public boolean isTouched = false;

    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        if(isTouched)
        {
            angleInDegrees *= VELOCITY_SCALE;
        }

        // Set our per-vertex lighting program.
        GLES20.glUseProgram(mProgramHandle);

        // Set program handles for cube drawing.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0, 0, 2.0f);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 1.0f, 1.0F, 0.0f);
        drawModel();
    }

    public void drawModel()
    {
        // Pass in the position information
        mModelPositions.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, 0, mModelPositions);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Pass in the color information
        mModelColors.position(0);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, mModelColors);
        GLES20.glEnableVertexAttribArray(mColorHandle);

        // Pass in the texture coordinate information
        mModelTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, mModelTextureCoordinates);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        // Pass in the modelview matrix.
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);

    }
}
