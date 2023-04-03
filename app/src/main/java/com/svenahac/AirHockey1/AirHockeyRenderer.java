package com.svenahac.AirHockey1;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;




import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import com.svenahac.AirHockey1.objects.Mallet;
import com.svenahac.AirHockey1.objects.Table;
import com.svenahac.AirHockey1.programs.ColorShaderProgram;
import com.svenahac.AirHockey1.programs.TextureShaderProgram;
import com.svenahac.AirHockey1.utils.MatrixHelper;
import com.svenahac.AirHockey1.utils.TextureHelper;
import com.svenahac.firstopenglproject.R;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class AirHockeyRenderer implements Renderer {

    private final Context context;
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private Table table;
    private Mallet mallet;
    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;
    private int[] texture = new int[2];

    public AirHockeyRenderer(Context context) {
        this.context = context;
    }
    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        table = new Table();
        mallet = new Mallet();

        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);

        texture[0] = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
        texture[1] = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface2);
    }
    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);
        // Setting the size of the field depending on orientation
        float z = 0f;
        if (width > height) {
            z = -2.5f;
        } else {
            z = -3f;
        }
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width
                / (float) height, 1f, 10f);

        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, 0f, 0f, z);
        rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);

        final float[] temp = new float[16];
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);

        // No culling of back faces
        glDisable(GL_CULL_FACE);
        // No depth testing
        glDisable(GL_DEPTH_TEST);

        // Draw the table.
        textureProgram.useProgram();
        textureProgram.setUniforms2(projectionMatrix, texture[0], texture[1]);
        textureProgram.setuTextureUnit(0);
        table.bindData(textureProgram);
        table.draw();

        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);

        textureProgram.setuTextureUnit(1);
        table.bindData(textureProgram);
        table.draw();

        // Draw the mallets.
        colorProgram.useProgram();
        colorProgram.setUniforms(projectionMatrix);
        mallet.bindData(colorProgram);
        mallet.draw();
    }


}
