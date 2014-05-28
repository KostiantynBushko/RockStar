package com.onquantum.rockstar.glprimitive;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by saiber on 09.02.14.
 */
public class GLDSquare extends GLShape {
    private float Red = 0.0f;
    private float Green = 1.0f;
    private float Blue = 0.0f;
    private float Alpha = 0.01f;

    private FloatBuffer vertexBuffer;
    private float vertices[] = new float[12];
    /*private float vertices[] = {
             1.0f,  1.0f,  0.0f,        // V1 - bottom left
             1.0f,  2.0f,  0.0f,        // V2 - top left
             2.0f,  1.0f,  0.0f,        // V3 - bottom right
             2.0f,  2.0f,  0.0f         // V4 - top right
    };*/

    public GLDSquare(float bottomLeftX, float bottomLeftY, float width, float height) {
        // V1 - bottom left
        vertices[0] = bottomLeftX;
        vertices[1] = bottomLeftY;
        vertices[2] = 0;
        // V2 - top left
        vertices[3] = bottomLeftX;
        vertices[4] = bottomLeftY + height;
        vertices[5] = 0;
        // V3 - bottom right
        vertices[6] = bottomLeftX + width;
        vertices[7] = bottomLeftY;
        vertices[8] = 0;
        // V4 - top right
        vertices[9] = bottomLeftX + width;
        vertices[10] = bottomLeftY + height;
        vertices[11] = 0;


        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = vertexByteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
    }

    @Override
    public void draw(GL10 gl) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        //gl.glClearColor(Red, Green, Blue, Alpha);
        gl.glColor4f(Red, Green, Blue, Alpha);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    @Override
    public void setColor(float r, float g, float b, float a) {
        Red = r;
        Green = g;
        Blue = b;
        Alpha = a;
    }

    @Override
    public void setAlpha(float alpha) {

    }
}
