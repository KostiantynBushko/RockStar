package com.onquantum.rockstar.guitars;

import android.content.Context;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.onquantum.rockstar.Settings;
import com.onquantum.rockstar.common.Pentatonic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by saiber on 01.03.14.
 */
public class GuitarView extends GLSurfaceView{

    private Context context;
    private SoundPool soundPool;
    private SoundPool soundPool2;
    private GuitarRenderer glRenderer;
    private int titleBarH = 0;

    private int[][] fretMaskStreamID = new int[20][20];
    private int[][] touchMask = new int[6][20];
    private int[] fingersTouchID = new int[10];
    private int[][] coordinateID = new int[10][10];

    int x,y;
    int width,height;
    //int streamID = -1;

    public interface GuitarViewInterface {
        void onPentatonicSuccessLoaded(String name);
    }

    public GuitarView(Context context) {
        super(context);
        for(int i = 0; i<10; i++){
            for(int j = 0; j<7; j++) { fretMaskStreamID[i][j] = -1; }
        }

        // Loading resources
        soundPool = new SoundPool(new Settings(context).getSoundChannels(), AudioManager.STREAM_MUSIC,0);
        String prefix = new Settings(context).getGuitarStyle();

        for (int i = 0; i < new Settings(context).getFretNumbers(); i++) {
            for (int j = 0; j < 6; j++){
                String file = prefix + "_" + Integer.toString(i) + "_" + Integer.toString(j);
                Log.i("info",file);
                int id = context.getResources().getIdentifier(file,"raw",context.getPackageName());
                soundPool.load(context,id,1);
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
                int pointIndex = event.getActionIndex();
                int pointID = event.getPointerId(pointIndex);

                if(actionMask == MotionEvent.ACTION_DOWN) {
                    Log.i("info", " Down Pointer ID = " + Integer.toString(pointID));
                }else if(actionMask == MotionEvent.ACTION_POINTER_DOWN) {
                    Log.i("info", " Down Pointer ID = " + Integer.toString(pointID));
                }else if (actionMask == MotionEvent.ACTION_UP) {
                    Log.i("info", " Up Pointer ID = " + Integer.toString(pointID));
                }else if(actionMask == MotionEvent.ACTION_POINTER_UP) {
                    Log.i("info", " Up Pointer ID = " + Integer.toString(pointID));
                }

                float fy;
                fy = ((height - (event.getY(pointIndex) - titleBarH)) / (height / 6));

                //Get x,y coordinates in open gl perspective
                x = (int)((width - event.getX(pointIndex)) / ((width / glRenderer.getAbscissa())));
                y = (int)((height - (event.getY(pointIndex) - titleBarH)) / (height / 6));


                int playId = (y + 1) + (6 * x);
                if(0.05f > (Math.abs(y - fy))) { return; }

                switch(actionMask) {

                    case MotionEvent.ACTION_POINTER_DOWN:{
                        if(touchMask[x][y] == 1)
                            break;
                        glRenderer.onTouchDown(x,y);
                        if(fretMaskStreamID[x][y] != -1) {
                            int id = fretMaskStreamID[x][y];
                            soundPool.stop(id);
                        }
                        //streamID = soundPool.play(playId, 1, 1, 1, 0, 1.0f);
                        fretMaskStreamID[x][y] = soundPool.play(playId, 1, 1, 1, 0, 1.0f);
                        Log.i("info","pID = " + Integer.toString(fretMaskStreamID[x][y]) + "  playId = " + Integer.toString(playId));
                        break;
                    }
                    case MotionEvent.ACTION_DOWN: {
                        if(touchMask[x][y] == 1)
                            break;
                        glRenderer.onTouchDown(x,y);
                        if(fretMaskStreamID[x][y] != -1) {
                            int id = fretMaskStreamID[x][y];
                            soundPool.stop(id);
                        }
                        //streamID  = soundPool.play(playId, 1, 1, 1, 0, 1.0f);
                        fretMaskStreamID[x][y]= soundPool.play(playId, 1, 1, 1, 0, 1.0f);
                        Log.i("info","pID = " + Integer.toString(fretMaskStreamID[x][y]) + "  playId = " + Integer.toString(playId));
                        break;
                    }
                    case MotionEvent.ACTION_POINTER_UP:  {
                        touchMask[x][y] = 0;
                        glRenderer.onTouchUp(x,y);
                        new Thread(new Runnable() {
                            int _x = x;
                            int _y = y;
                            int _id = fretMaskStreamID[_x][_y];
                            @Override
                            public void run() {
                                float volume = 0.8f;
                                while (volume > 0.01f){
                                    soundPool.setVolume(_id,volume, volume);
                                    SystemClock.sleep(15);
                                    volume-=0.01f;
                                }
                                soundPool.stop(_id);
                                fretMaskStreamID[_x][_y] = -1;
                            }
                        }).start();
                        Log.i("info","pID = " + Integer.toString(fretMaskStreamID[x][y]) + "  playId = " + Integer.toString(playId));
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        touchMask[x][y] = 0;
                        glRenderer.onTouchUp(x,y);
                        new Thread(new Runnable() {
                            int _x = x;
                            int _y = y;
                            int _id = fretMaskStreamID[_x][_y];
                            @Override
                            public void run() {
                                float volume = 0.8f;
                                while (volume > 0.01f){
                                    soundPool.setVolume(_id,volume,volume);
                                    SystemClock.sleep(15);
                                    volume-=0.01f;
                                }
                                soundPool.stop(_id);
                                fretMaskStreamID[_x][_y] = -1;
                            }
                        }).start();
                        Log.i("info","pID = " + Integer.toString(fretMaskStreamID[x][y]) + "  playId = " + Integer.toString(playId));
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        glRenderer.onTouchMove(x,y);

                    }
                    default:break;
                }
            }
        });
        return true;
    }

    /**********************************************************************************************/
    /* Play pentatonic */
    /**********************************************************************************************/
    public void LoadPentatonicFile(String fileName) {
        List<Pentatonic>pentatonics = new ArrayList<Pentatonic>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open("pentatonic/" + fileName)));
            String str = reader.readLine();
            while (str != null) {
                Pentatonic pentatonic = new Pentatonic();
                Log.i("info", " line = " + str);
                String[] parce = str.split("\\s+");
                if (parce.length == 4){
                    pentatonic.bar = Integer.parseInt(parce[0]);
                    pentatonic.line = Integer.parseInt(parce[1]);
                    pentatonic.delay = Integer.parseInt(parce[2]);
                    pentatonic.playTime = Integer.parseInt(parce[3]);
                    pentatonics.add(pentatonic);
                }
                str = reader.readLine();
            }
            if(glRenderer.LoadPentatonic(pentatonics)) {
                if (context instanceof GuitarViewInterface) {
                    ((GuitarViewInterface)context).onPentatonicSuccessLoaded(fileName);
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void ClosePlayPentatonic() {
        glRenderer.ClosePlayPentatonic();
    }
}
