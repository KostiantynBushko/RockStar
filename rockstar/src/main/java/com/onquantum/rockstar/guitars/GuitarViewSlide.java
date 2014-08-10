package com.onquantum.rockstar.guitars;

import android.content.Context;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
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
 * Created by Admin on 8/5/14.
 */
public class GuitarViewSlide extends GLSurfaceView {

    private Context context;
    private SoundPool soundPool;
    private GuitarRenderer glRenderer;
    private int titleBarH = 0;
    private int[][] touchMask = new int[13][6];
    List<GuitarString>simpleTouchList = new ArrayList<GuitarString>(11);

    private final float rateCof = 2.0f / 24;
    int x,y;
    int width,height;

    public GuitarViewSlide(Context context) {
        super(context);
        Log.i("info"," GuitarViewTest SLIDE" );
        Log.i("info"," rateCof = " + Float.toString(rateCof));
        for(int i = 0; i<10; i++) {
            simpleTouchList.add(new GuitarString(-1,-1,-1,context));
        }

        Log.i("info"," simpleTouchList = " + simpleTouchList.size());
        // Loading resources
        soundPool = new SoundPool(60, AudioManager.STREAM_MUSIC,0);
        String prefix;
        if(new Settings(context).getDistortion()) {
            prefix = "distortion";
        }else {
            prefix = "clean";
        }

        for (int i = 0; i < new Settings(context).getFretNumbers(); i++) {
            for (int j = 0; j < 6; j++){
                String file = prefix + "_" + Integer.toString(i) + "_" + Integer.toString(j);
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
                final int pointID = event.getPointerId(pointIndex);

                float fy;
                fy = ((height - (event.getY(pointIndex) - titleBarH)) / (height / 6));

                //Get x,y coordinates in open gl perspective
                x = (int)((width - event.getX(pointIndex)) / ((width / glRenderer.getAbscissa())));
                y = (int)((height - (event.getY(pointIndex) - titleBarH)) / (height / 6));
                //Log.i("info","x = " + Integer.toString(x) + "  y = " + Integer.toString(y));

                if(0.05f > (Math.abs(y - fy))) { return; }

                switch(actionMask) {
                    case MotionEvent.ACTION_POINTER_DOWN:{
                        if(touchMask[x][y] == 1)
                            break;
                        glRenderer.onTouchDown(x,y);
                        simpleTouchList.get(pointID).set(x,y,soundPool);
                        break;
                    }
                    case MotionEvent.ACTION_DOWN: {
                        if(touchMask[x][y] == 1)
                            break;
                        glRenderer.onTouchDown(x,y);
                        simpleTouchList.get(pointID).set(x,y,soundPool);
                        break;
                    }
                    /******************************************************************************/
                    case MotionEvent.ACTION_POINTER_UP:  {
                        touchMask[x][y] = 0;
                        glRenderer.onTouchUp(x,y);
                        simpleTouchList.get(pointID).stop();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        touchMask[x][y] = 0;
                        glRenderer.onTouchUp(x,y);
                        simpleTouchList.get(pointID).stop();
                        //Log.i("info","pID = " + Integer.toString(fretMaskStreamID[x][y]) + "  playId = " + Integer.toString(playId));
                        break;
                    }
                    /******************************************************************************/
                    case MotionEvent.ACTION_MOVE: {
                        glRenderer.onTouchMove(x,y);
                        int _x = simpleTouchList.get(pointID).x;
                        if(_x != x) {
                            simpleTouchList.get(pointID).move(x,y);
                        }
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
}
