package com.onquantum.rockstar.glprimitive;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by saiber on 09.02.14.
 */
public class GLDTexture extends GLShape {
    private float Red = 1.0f;
    private float Green = 1.0f;
    private float Blue = 1.0f;
    private float Alpha = 0.9f;

    private int[] textures = new int[1];
    private FloatBuffer textureBuffer;  // buffer holding the texture coordinates
    private FloatBuffer vertexBuffer;

    public float x,y,width,height;
    private float texture[] = {
            // Mapping coordinates for the vertices
            0.0f, 1.0f,     // top left     (V2)
            0.0f, 0.0f,     // bottom left  (V1)
            1.0f, 1.0f,     // top right    (V4)
            1.0f, 0.0f      // bottom right (V3)
    };
    private float vertices[] = new float[12];

    public void loadGLTexture(GL10 gl, Context context, Bitmap bitmap) {
        // loading texture
        //Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.grif_background);
        // generate one texture pointer
        gl.glGenTextures(1, textures, 0);
        // ...and bind it to our array
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        // create nearest filtered texture
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        // Use Android GLUtils to specify a two-dimensional texture image from our bitmap
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        // Clean up
        bitmap.recycle();
    }

    public GLDTexture(float bottomLeftX, float bottomLeftY, float width, float height, boolean object) {
        this.x = bottomLeftX;
        this.y = bottomLeftY;
        this.width = width;
        this.height = height;

        // V1 - bottom left
        vertices[0] = bottomLeftX - width / 2;
        vertices[1] = bottomLeftY - height / 2;
        vertices[2] = 0;
        // V2 - top left
        vertices[3] = bottomLeftX - width / 2;
        vertices[4] = bottomLeftY + height / 2;
        vertices[5] = 0;
        // V3 - bottom right
        vertices[6] = bottomLeftX + width / 2;
        vertices[7] = bottomLeftY - height / 2;
        vertices[8] = 0;
        // V4 - top right
        vertices[9] = bottomLeftX + width / 2;
        vertices[10] = bottomLeftY + height / 2;
        vertices[11] = 0;

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
        byteBuffer = ByteBuffer.allocateDirect(texture.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        textureBuffer = byteBuffer.asFloatBuffer();
        textureBuffer.put(texture);
        textureBuffer.position(0);
    }

    public GLDTexture(float bottomLeftX, float bottomLeftY, float width, float height) {
        this.x = bottomLeftX;
        this.y = bottomLeftY;
        this.width = width;
        this.height = height;

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

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
        byteBuffer = ByteBuffer.allocateDirect(texture.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        textureBuffer = byteBuffer.asFloatBuffer();
        textureBuffer.put(texture);
        textureBuffer.position(0);
    }

    @Override
    public void draw(GL10 gl) {
        // bind the previously generated texture
        gl.glPushMatrix();
        gl.glColor4f(Red, Green, Blue, Alpha);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        // Point to our buffers
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        // Set the face rotation
        gl.glFrontFace(GL10.GL_CW);
        // Point to our vertex buffer
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
        // Draw the vertices as triangle strip
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
        //Disable the client state before leaving
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glPopMatrix();
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
        Alpha =alpha;
    }
    public void setTranslate(float bottomLeftX, float bottomLeftY,float width, float height) {
        this.x = bottomLeftX;
        this.y = bottomLeftY;
        this.width = width;
        this.height = height;

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
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
    }
}
