package com.svenahac.AirHockey1;

import static android.opengl.GLES20.*;


import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import com.svenahac.AirHockey1.utils.LoggerConfig;
import com.svenahac.AirHockey1.utils.ShaderHelper;
import com.svenahac.AirHockey1.utils.TextResourceReader;
import com.svenahac.firstopenglproject.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class AirHockeyRenderer implements Renderer {

    private final Context context;
    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;
    private int uColorLocation,aPositionLocation;
    private static final int POSITION_COMPONENT_COUNT = 2;
    public AirHockeyRenderer(Context context) {
        this.context = context;

        float[] tableVerticesWithTriangles = {
                // Triangle 1
                -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f,
                // Triangle 2
                -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f,
                // Line 1
                -0.5f, 0f, 0.5f, 0f,
                // Mallets
                0f, -0.25f, 0f, 0.25f
        };

        vertexData = ByteBuffer.allocateDirect(
                tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
        String vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
        int program = ShaderHelper.linkProgram(vertexShader,fragmentShader);
        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }
        glUseProgram(program);
        uColorLocation = glGetUniformLocation(program, "u_Color");
        aPositionLocation = glGetAttribLocation(program, "a_Position");

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
        glEnableVertexAttribArray(aPositionLocation);
    }
    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height){
        glViewport(0,0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        glClear(GL_COLOR_BUFFER_BIT);
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 0, 6);

        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_LINES, 6, 2);

        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_POINTS, 8, 1);

        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_POINTS, 9, 1);
    }
}
