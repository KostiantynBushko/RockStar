package com.onquantum.rockstar.guitars;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;

import com.onquantum.rockstar.*;
import com.onquantum.rockstar.glprimitive.GLDTexture;
import com.onquantum.rockstar.glprimitive.GLShape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by saiber on 01.03.14.
 */
public class GuitarRenderer implements GLSurfaceView.Renderer{
    private Context context;
    private int fretCount = 0;

    /**********************************************************************************************/
    int width, height;
    float ordinate = 1;
    float abscissa = 1;
    float ratio = 0;

    GLDTexture strig6;
    GLDTexture strig5;
    GLDTexture strig4;

    List<GLShape>guitarFretboard = Collections.synchronizedList(new ArrayList<GLShape>());
    List<GLShape>Board = Collections.synchronizedList(new ArrayList<GLShape>());
    List<GLShape>stringTexture = Collections.synchronizedList(new ArrayList<GLShape>());
    List<GLShape>shadowTexture = Collections.synchronizedList(new ArrayList<GLShape>());

    List<GLShape>shadowTextureStringOne = Collections.synchronizedList(new ArrayList<GLShape>());
    List<GLShape>shadowTextureStringTwo = Collections.synchronizedList(new ArrayList<GLShape>());
    List<GLShape>shadowTextureStringThree = Collections.synchronizedList(new ArrayList<GLShape>());
    List<GLShape>shadowTextureStringFor = Collections.synchronizedList(new ArrayList<GLShape>());
    List<GLShape>shadowTextureStringFive = Collections.synchronizedList(new ArrayList<GLShape>());
    List<GLShape>shadowTextureStringSix = Collections.synchronizedList(new ArrayList<GLShape>());

    List<GLShape>drawObjectList;

    /**********************************************************************************************/
    public GuitarRenderer(Context context, int fretCount) {
        this.context = context;
        this.fretCount = fretCount;
    }
    public float getAbscissa() {
        return abscissa;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {
        gl.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        ratio = (float)width / (float)height;
        ordinate = 15;
        float h = this.height / ordinate;
        abscissa = fretCount;

        if(this.height == 0)
            this.height = 1;
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_NICEST);
        gl.glEnable(GL10.GL_ALPHA_TEST);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glViewport(0, 0, this.width, this.height);
        gl.glLoadIdentity();
        gl.glOrthof(0.0f, abscissa, 0.0f, ordinate, -1.0f, 1.0f);

        /******************************************************************************************/
        /* Create game object */
        /******************************************************************************************/
        // Clear drawable object collection
        if(drawObjectList != null)
            drawObjectList.clear();

        // Create nek of guitar
        Board.clear();
        float fret = 1;
        for(int i = fretCount-1; i>=0; i--) {
            GLDTexture board = new GLDTexture(i,0,1,ordinate);
            if(((fret/2) % 1) > 0) { // Check if decimal value > 0
                board.loadGLTexture(gl, context, BitmapFactory.decodeResource(context.getResources(),R.drawable.b1));
            }else {
                board.loadGLTexture(gl, context, BitmapFactory.decodeResource(context.getResources(), R.drawable.b0));
            }
            fret++;
            board.layer = 0;
            Board.add(board);
        }
        drawObjectList = Collections.synchronizedList(new ArrayList<GLShape>(Board));

        guitarFretboard.clear();
        float w = 0.17f;
        for(int i = 0; i < fretCount+1; i++) {
            GLDTexture fretTexture = new GLDTexture(i - w / 2, 0, w,ordinate);
            fretTexture.loadGLTexture(gl,context,BitmapFactory.decodeResource(context.getResources(), R.drawable.lad));
            fretTexture.layer = 1;
            guitarFretboard.add(fretTexture);
        }
        drawObjectList.addAll(guitarFretboard);

        // Init shadow strings texture
        float constant = 2.5f;
        shadowTexture.clear();
        shadowTextureStringOne.clear();
        shadowTextureStringTwo.clear();
        shadowTextureStringThree.clear();
        shadowTextureStringFor.clear();
        shadowTextureStringFive.clear();
        shadowTextureStringSix.clear();

        float shadowStep = 1;
        float shadowHeight = 0.15f;
        float shadowAlpha = 0.18f;
        for (int i = 0; i < 6; i++) {
            for(int j = 0; j<fretCount+1; j++) {
                GLDTexture shadow_1 = new GLDTexture(j+w/2,shadowStep-0.4f,1.0f - w,shadowHeight);
                shadow_1.loadGLTexture(gl,context,BitmapFactory.decodeResource(context.getResources(), R.drawable.shadow_p1));
                shadow_1.layer = 2;
                //shadowTexture.add(shadow_1);
                shadow_1.setColor(1.0f, 1.0f, 1.0f,shadowAlpha);

                GLDTexture shadow_2= new GLDTexture(j-w/2,shadowStep-0.4f,w,shadowHeight);
                shadow_2.loadGLTexture(gl,context,BitmapFactory.decodeResource(context.getResources(), R.drawable.shadow_p2));
                shadow_2.layer = 2;
                //shadowTexture.add(shadow_2);
                shadow_2.setColor(1.0f, 1.0f, 1.0f,shadowAlpha);
                switch (i) {
                    case 0:{
                        shadowTextureStringOne.add(shadow_1);
                        shadowTextureStringOne.add(shadow_2);
                        break;
                    }
                    case 1:{
                        shadowTextureStringTwo.add(shadow_1);
                        shadowTextureStringTwo.add(shadow_2);
                        break;
                    }
                    case 2:{
                        shadowTextureStringThree.add(shadow_1);
                        shadowTextureStringThree.add(shadow_2);
                        break;
                    }
                    case 3:{
                        shadowTextureStringFor.add(shadow_1);
                        shadowTextureStringFor.add(shadow_2);
                        break;
                    }
                    case 4:{
                        shadowTextureStringFive.add(shadow_1);
                        shadowTextureStringFive.add(shadow_2);
                        break;
                    }
                    case 5:{
                        shadowTextureStringSix.add(shadow_1);
                        shadowTextureStringSix.add(shadow_2);
                        break;
                    }
                    default: break;
                }
            }
            shadowHeight += 0.05f;
            shadowStep += constant;
        }
        drawObjectList.addAll(shadowTexture);
        drawObjectList.addAll(shadowTextureStringOne);
        drawObjectList.addAll(shadowTextureStringTwo);
        drawObjectList.addAll(shadowTextureStringThree);
        drawObjectList.addAll(shadowTextureStringFor);
        drawObjectList.addAll(shadowTextureStringFive);
        drawObjectList.addAll(shadowTextureStringSix);

        //Guitar string
        //String 1,3
        stringTexture.clear();
        float inc = 1;
        GLDTexture string_1 = new GLDTexture(0,inc, abscissa, 0.12f);
        string_1.loadGLTexture(gl, context, BitmapFactory.decodeResource(context.getResources(), R.drawable.string_3));
        string_1.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        string_1.layer = 3;
        stringTexture.add(string_1);
        inc+=constant;
        GLDTexture string_2 = new GLDTexture(0,inc, abscissa, 0.12f);
        string_2.loadGLTexture(gl, context, BitmapFactory.decodeResource(context.getResources(), R.drawable.string_3));
        string_2.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        string_2.layer = 3;
        stringTexture.add(string_2);
        inc+=constant;
        GLDTexture string_3 = new GLDTexture(0,inc, abscissa, 0.14f);
        string_3.loadGLTexture(gl, context, BitmapFactory.decodeResource(context.getResources(), R.drawable.string_3));
        string_3.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        string_3.layer = 3;
        stringTexture.add(string_3);
        inc+=constant;

        //String 4,6
        strig4 = new GLDTexture(0,inc, abscissa, 0.25f);
        strig4.loadGLTexture(gl,context, BitmapFactory.decodeResource(context.getResources(), R.drawable.string_5));
        strig4.layer = 3;
        stringTexture.add(strig4);
        inc+=constant;
        strig5 = new GLDTexture(0,inc, abscissa, 0.3f);
        strig5.loadGLTexture(gl,context, BitmapFactory.decodeResource(context.getResources(), R.drawable.string_5));
        strig5.layer = 3;
        stringTexture.add(strig5);
        inc+=constant;
        strig6 = new GLDTexture(0,inc, abscissa, 0.35f);
        strig6.loadGLTexture(gl,context, BitmapFactory.decodeResource(context.getResources(), R.drawable.string_6));
        strig6.layer = 3;
        stringTexture.add(strig6);
        drawObjectList.addAll(stringTexture);

    }
    /**********************************************************************************************/
    /* Draw frame */
    /**********************************************************************************************/
    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        int i = 0;
        synchronized (drawObjectList) {
            for(GLShape object : drawObjectList) {
                object.draw(gl);
                i++;
            }
        }
    }
    /**********************************************************************************************/
    /* Touch event handler */
    /**********************************************************************************************/
    private int[] touchMask = new int[10];
    private float stringDownUp = 0.2f;
    private float shadowUp = 0.2f;
    private float scale = 0.02f;
    // Touch Down
    public void onTouchDown(int x, int y) {
        if (y >= 6)
            return;
        if (touchMask[y] != 1) {
            touchMask[y] = 1;
            ((GLDTexture)stringTexture.get(y)).setTranslate(((GLDTexture)stringTexture.get(y)).x,
                    ((GLDTexture)stringTexture.get(y)).y - stringDownUp,
                    ((GLDTexture)stringTexture.get(y)).width,
                    ((GLDTexture)stringTexture.get(y)).height - scale);
            /*List<GLShape>shadow = null;
            switch (y) {
                case 0:
                    shadow = Collections.synchronizedList(new ArrayList<GLShape>(shadowTextureStringOne));
                    break;
                case 1:
                    shadow = Collections.synchronizedList(new ArrayList<GLShape>(shadowTextureStringTwo));
                    break;
                case 2:
                    shadow = Collections.synchronizedList(new ArrayList<GLShape>(shadowTextureStringThree));
                    break;
                case 3:
                    shadow = Collections.synchronizedList(new ArrayList<GLShape>(shadowTextureStringFor));
                    break;
                case 4:
                    shadow = Collections.synchronizedList(new ArrayList<GLShape>(shadowTextureStringFive));
                    break;
                case 5:
                    shadow = Collections.synchronizedList(new ArrayList<GLShape>(shadowTextureStringSix));
                    break;
                default:break;
            }
            for(int i = 0; i<shadow.size(); i++) {
                ((GLDTexture)shadow.get(i)).setTranslate(((GLDTexture)shadow.get(i)).x,
                        ((GLDTexture)shadow.get(i)).y + shadowUp,
                        ((GLDTexture)shadow.get(i)).width,
                        ((GLDTexture)shadow.get(i)).height - scale);
            }*/
        }
    }
    // Touch Up
    public void onTouchUp(int x, int y) {
        if (y >= 6)
            return;
        if (touchMask[y] == 1){
            touchMask[y] = 0;
            ((GLDTexture)stringTexture.get(y)).setTranslate(((GLDTexture)stringTexture.get(y)).x,
                    ((GLDTexture)stringTexture.get(y)).y + stringDownUp,
                    ((GLDTexture)stringTexture.get(y)).width,
                    ((GLDTexture)stringTexture.get(y)).height + scale);
            /*List<GLShape>shadow = null;
            switch (y) {
                case 0:
                    shadow = Collections.synchronizedList(new ArrayList<GLShape>(shadowTextureStringOne));
                    break;
                case 1:
                    shadow = Collections.synchronizedList(new ArrayList<GLShape>(shadowTextureStringTwo));
                    break;
                case 2:
                    shadow = Collections.synchronizedList(new ArrayList<GLShape>(shadowTextureStringThree));
                    break;
                case 3:
                    shadow = Collections.synchronizedList(new ArrayList<GLShape>(shadowTextureStringFor));
                    break;
                case 4:
                    shadow = Collections.synchronizedList(new ArrayList<GLShape>(shadowTextureStringFive));
                    break;
                case 5:
                    shadow = Collections.synchronizedList(new ArrayList<GLShape>(shadowTextureStringSix));
                    break;
                default:break;
            }
            for(int i = 0; i<shadow.size(); i++) {
                ((GLDTexture)shadow.get(i)).setTranslate(((GLDTexture)shadow.get(i)).x,
                        ((GLDTexture)shadow.get(i)).y - shadowUp,
                        ((GLDTexture)shadow.get(i)).width,
                        ((GLDTexture)shadow.get(i)).height + scale);
            }*/
        }
    }
    // Touch Move
    public void onTouchMove(int x, int y) {
        for(int i = 0; i<6; i++ ) {
            if (touchMask[i] == 1) {
                touchMask[i] = 0;
                ((GLDTexture)stringTexture.get(i)).setTranslate(((GLDTexture)stringTexture.get(1)).x,
                        ((GLDTexture)stringTexture.get(i)).y + stringDownUp,
                        ((GLDTexture)stringTexture.get(i)).width,
                        ((GLDTexture)stringTexture.get(i)).height + scale);
                /*List<GLShape>shadow = null;
                switch (y) {
                    case 0:
                        shadow = Collections.synchronizedList(new ArrayList<GLShape>(shadowTextureStringOne));
                        break;
                    case 1:
                        shadow = Collections.synchronizedList(new ArrayList<GLShape>(shadowTextureStringTwo));
                        break;
                    case 2:
                        shadow = Collections.synchronizedList(new ArrayList<GLShape>(shadowTextureStringThree));
                        break;
                    case 3:
                        shadow = Collections.synchronizedList(new ArrayList<GLShape>(shadowTextureStringFor));
                        break;
                    case 4:
                        shadow = Collections.synchronizedList(new ArrayList<GLShape>(shadowTextureStringFive));
                        break;
                    case 5:
                        shadow = Collections.synchronizedList(new ArrayList<GLShape>(shadowTextureStringSix));
                        break;
                    default:break;
                }
                for(int j = 0; j<shadow.size(); j++) {
                    ((GLDTexture)shadow.get(j)).setTranslate(((GLDTexture)shadow.get(j)).x,
                            ((GLDTexture)shadow.get(j)).y - shadowUp,
                            ((GLDTexture)shadow.get(j)).width,
                            ((GLDTexture)shadow.get(j)).height + scale);
                }*/
            }
        }
    }
}
