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
    private int aPositionLocation, uSizeLocation, aColorLocation;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    public AirHockeyRenderer(Context context) {
        this.context = context;

        float[] tableVerticesWithTriangles = {
                //Order: X,Y,R,G,B
                // Triangle Fan
                0f,0f,1f,1f,1f,
                -0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.5f, 0.7f, 0.7f, 0.7f,
                -0.5f, 0.5f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
                // Line 1
                -0.5f, 0f, 1f, 0f, 0f, 0.5f, 0f, 1f, 0f, 0f,
                // Mallet Blue
                0f, -0.25f, 0f,0f,1f,
                // Mallet Red
                0f, 0.25f, 1f,0f,0f,
                // Puck
                0f, 0f, 1f,0f,0f


        };

        vertexData = ByteBuffer.allocateDirect(
                tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0f, 0f, 0f, 1f);
        String vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
        int program = ShaderHelper.linkProgram(vertexShader,fragmentShader);
        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }
        glUseProgram(program);
        aColorLocation = glGetAttribLocation(program, "a_Color");
        aPositionLocation = glGetAttribLocation(program, "a_Position");
        uSizeLocation = glGetUniformLocation(program, "u_Size");

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aPositionLocation);

        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aColorLocation);
    }
    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height){
        glViewport(0,0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        glClear(GL_COLOR_BUFFER_BIT);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);


        glDrawArrays(GL_LINES, 6, 2);

        glUniform1f(uSizeLocation, 50f);
        glDrawArrays(GL_POINTS, 8, 1);

        glDrawArrays(GL_POINTS, 9, 1);

        glUniform1f(uSizeLocation, 10f);
        glDrawArrays(GL_POINTS, 10, 1);


    }
}
