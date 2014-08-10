package com.onquantum.rockstar.guitars;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.onquantum.rockstar.Settings;
import com.onquantum.rockstar.common.GuitarString;
import com.onquantum.rockstar.common.Pentatonic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 8/6/14.
 */
public class GuitarViewDefault extends GLSurfaceView implements SensorEventListener{

    private Context context;
    private SoundPool soundPool;
    private GuitarRenderer glRenderer;
    private int titleBarH = 0;

    private int[][] fretMaskStreamID = new int[13][6];
    private int[][] touchMask = new int[13][6];
    List<GuitarString> simpleTouchList = new ArrayList<GuitarString>(11);

    int x,y;
    int width,height;

    private SensorManager sensorManager;

    public GuitarViewDefault(final Context context) {
        super(context);

        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);

        Log.i("info"," GuitarSurfaceView NO SLIDE" );
        for(int i = 0; i<10; i++) {
            simpleTouchList.add(new GuitarString(-1,-1,-1,context));
        }

        Log.i("info"," simpleTouchList = " + simpleTouchList.size());
        soundPool = new SoundPool(new Settings(context).getSoundChannels(), AudioManager.STREAM_MUSIC,0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String prefix;
                if(new Settings(context).getDistortion()) {
                    prefix = "distortion";
                }else {
                    prefix = "clean";
                }
                for (int i = 0; i < new Settings(context).getFretNumbers(); i++) {
                    for (int j = 0; j < 6; j++){
                        String file = prefix + "_" + Integer.toString(i) + "_" + Integer.toString(j);
                        Log.i("info"," sound = " + file);
                        int id = context.getResources().getIdentifier(file,"raw",context.getPackageName());
                        soundPool.load(context,id,1);
                    }
                }
            }
        }).start();

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
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        this.width = glRenderer.width;
        this.height = glRenderer.height;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                int actionMask = event.getActionMasked();
                int pointIndex = event.getActionIndex();
                final int pointID = event.getPointerId(pointIndex);

                float fy;
                fy = ((height - (event.getY(pointIndex) - titleBarH)) / (height / 6));

                x = (int)((width - event.getX(pointIndex)) / ((width / glRenderer.getAbscissa())));
                y = (int)((height - (event.getY(pointIndex) - titleBarH)) / (height / 6));

                int playId = (y + 1) + (6 * x);
                if(0.05f > (Math.abs(y - fy))) { return; }

                switch(actionMask) {

                    case MotionEvent.ACTION_POINTER_DOWN:{
                        if(touchMask[x][y] == 1)
                            break;
                        glRenderer.onTouchDown(x,y);
                        if(fretMaskStreamID[x][y] != 0) {
                            int id = fretMaskStreamID[x][y];
                            soundPool.stop(id);
                        }
                        fretMaskStreamID[x][y] = soundPool.play(playId, 1, 1, 1, 0, 1.0f);
                        simpleTouchList.get(pointID).set(fretMaskStreamID[x][y],x,y,soundPool);
                        break;
                    }
                    case MotionEvent.ACTION_DOWN: {
                        if(touchMask[x][y] == 1)
                            break;
                        glRenderer.onTouchDown(x,y);
                        if(fretMaskStreamID[x][y] != 0) {
                            int id = fretMaskStreamID[x][y];
                            soundPool.stop(id);
                        }
                        fretMaskStreamID[x][y]= soundPool.play(playId, 1, 1, 1, 0, 1.0f);
                        simpleTouchList.get(pointID).set(fretMaskStreamID[x][y],x,y,soundPool);
                        break;
                    }

                    /******************************************************************************/
                    case MotionEvent.ACTION_POINTER_UP:  {
                        touchMask[x][y] = 0;
                        glRenderer.onTouchUp(x,y);
                        simpleTouchList.get(pointID).stop();
                        new Thread(new Runnable() {
                            int _x = simpleTouchList.get(pointID).x; //x;
                            int _y = simpleTouchList.get(pointID).y; //y;
                            int _id = simpleTouchList.get(pointID).streamId; //fretMaskStreamID[_x][_y];
                            @Override
                            public void run() {
                                float volume = 0.8f;
                                while (volume > 0.01f){
                                    soundPool.setVolume(_id,volume, volume);
                                    SystemClock.sleep(15);
                                    volume-=0.01f;
                                }
                                soundPool.stop(_id);
                                fretMaskStreamID[_x][_y] = 0;
                            }
                        }).start();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        touchMask[x][y] = 0;
                        glRenderer.onTouchUp(x,y);
                        simpleTouchList.get(pointID).stop();
                        new Thread(new Runnable() {
                            int _x = simpleTouchList.get(pointID).x; //x;
                            int _y = simpleTouchList.get(pointID).y; //y;
                            int _id = simpleTouchList.get(pointID).streamId; //fretMaskStreamID[_x][_y];
                            @Override
                            public void run() {
                                float volume = 0.8f;
                                while (volume > 0.01f){
                                    soundPool.setVolume(_id,volume,volume);
                                    SystemClock.sleep(15);
                                    volume-=0.01f;
                                }
                                soundPool.stop(_id);
                                fretMaskStreamID[_x][_y] = 0;
                            }
                        }).start();
                        break;
                    }
                    /******************************************************************************/
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
        List<Pentatonic> pentatonics = new ArrayList<Pentatonic>();
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
                if (context instanceof GuitarInterface) {
                    ((GuitarInterface)context).onPentatonicSuccessLoaded(fileName);
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void ClosePlayPentatonic() {
        glRenderer.ClosePlayPentatonic();
    }

    /**********************************************************************************************/
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            Log.i("info","Z = " + event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
