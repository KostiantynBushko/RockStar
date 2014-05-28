package com.onquantum.rockstar.guitars;

import android.content.Context;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.onquantum.rockstar.Settings;

/**
 * Created by saiber on 01.03.14.
 */
public class GuitarView extends GLSurfaceView{
    private static final String LOG = "log";
    private static final String LOG_TUCH = "log_touch";

    private Context context;
    private SoundPool soundPool;
    private SoundPool soundPool2;
    private GuitarRenderer glRenderer;
    private int titleBarH = 0;

    private int[][] fretMask = new int[20][20];
    private int[][] touchMask = new int[20][20];
    private int[] fingersTouchID = new int[10];

    int x,y;
    int width,height;
    int pID = -1;

    public GuitarView(Context context) {
        super(context);
        for(int i = 0; i<10; i++){
            for(int j = 0; j<7; j++) { fretMask[i][j] = -1; }
        }

        // Loading sound resources
        soundPool = new SoundPool(new Settings(context).getSoundChannels(), AudioManager.STREAM_MUSIC,0);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 6; j++){
                String file = "f_" + Integer.toString(i) + "_" + Integer.toString(j);
                int id = context.getResources().getIdentifier(file,"raw",context.getPackageName());
                soundPool.load(context,id,1);
                ///Log.i("info"," load = " + file);
            }
        }

        glRenderer = new GuitarRenderer(context, new Settings(context).getFretNumbers());
        this.setRenderer(glRenderer);
        this.context = context;
    }

    @Override
    public void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        titleBarH = size.y - height;
    }
    /**********************************************************************************************/
    /* Touches event */
    /**********************************************************************************************/
    public boolean onTouchEvent(final MotionEvent event) {
        this.width = glRenderer.width;
        this.height = glRenderer.height;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                int actionMask = event.getActionMasked();
                int pointerCount = event.getPointerCount();
                int pointIndex = event.getActionIndex();
                //Log.i("info"," index = " + Integer.toString(pointerCount));

                float fy;
                fy = ((height - (event.getY(pointIndex) - titleBarH)) / (height / 6));

                //Get x,y coordinates in opengl perspective
                x = (int)((width - event.getX(pointIndex)) / ((width / glRenderer.getAbscissa())));
                y = (int)((height - (event.getY(pointIndex) - titleBarH)) / (height / 6));
                int playId = (y + 1) + (6 * x);
                ///Log.i("info"," playId = " + Integer.toString(playId));

                if(0.15f > (Math.abs(y - fy))) { return; }

                switch(actionMask) {

                    case MotionEvent.ACTION_POINTER_DOWN:{
                        if(touchMask[x][y] == 1)
                            break;
                        glRenderer.onTouchDown(x,y);
                        if(fretMask[x][y] != -1) {
                            int id = fretMask[x][y];
                            soundPool.stop(id);
                        }
                        ///Log.i("info"," X = " + Integer.toString(x));
                        ///Log.i("info"," Y = " + Integer.toString(y));
                        pID = soundPool.play(playId, 1, 1, 1, 0, 1.0f);
                        fretMask[x][y]=pID;
                        break; }

                    case MotionEvent.ACTION_DOWN: {
                        if(touchMask[x][y] == 1)
                            break;
                        glRenderer.onTouchDown(x,y);
                        if(fretMask[x][y] != -1) {
                            int id = fretMask[x][y];
                            soundPool.stop(id);
                        }
                        ///Log.i("info"," X = " + Integer.toString(x));
                        ///Log.i("info"," Y = " + Integer.toString(y));
                        pID = soundPool.play(playId, 1, 1, 1, 0, 1.0f);
                        fretMask[x][y]=pID;
                        break;}

                    case MotionEvent.ACTION_POINTER_UP:  {
                        touchMask[x][y] = 0;
                        glRenderer.onTouchUp(x,y);
                        new Thread(new Runnable() {
                            int _x = x;
                            int _y = y;
                            int _id = fretMask[_x][_y];
                            @Override
                            public void run() {
                                float volume = 0.8f;
                                while (volume > 0.1f){
                                    soundPool.setVolume(_id,volume, volume);
                                    //Log.i(LOG, ":" + Float.toString(volume));
                                    SystemClock.sleep(150);
                                    volume-=0.1f;
                                }
                                soundPool.stop(_id);
                                fretMask[_x][_y] = -1;
                            }
                        }).start();
                        break; }

                    case MotionEvent.ACTION_UP: {
                        fingersTouchID[event.getPointerId(pointIndex)] = 0;
                        touchMask[x][y] = 0;
                        glRenderer.onTouchUp(x,y);
                        new Thread(new Runnable() {
                            int _x = x;
                            int _y = y;
                            int _id = fretMask[_x][_y];
                            @Override
                            public void run() {
                                float volume = 0.8f;
                                while (volume > 0.1f){
                                    soundPool.setVolume(_id,volume,volume);
                                    //Log.i(LOG, ": " + Float.toString(volume));
                                    SystemClock.sleep(150);
                                    volume-=0.1f;
                                }
                                soundPool.stop(_id);
                                fretMask[_x][_y] = -1;
                            }
                        }).start();
                        break; }

                    case MotionEvent.ACTION_MOVE: {
                        //Log.i("info","id = " + Integer.toString(event.getPointerId(pointIndex)));
                        //Log.i("info","x = " + Float.toString(event.getX()) + " y = " + Float.toString(event.getY()));
                        glRenderer.onTouchMove(x,y);
                    }
                    default:break;
                }
            }
        });
        return true;
    }
}
