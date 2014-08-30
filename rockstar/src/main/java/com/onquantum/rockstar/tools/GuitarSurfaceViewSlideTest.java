package com.onquantum.rockstar.tools;

import android.content.Context;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.onquantum.rockstar.Settings;
import com.onquantum.rockstar.common.GuitarString;
import com.onquantum.rockstar.common.GuitarStringTest;
import com.onquantum.rockstar.common.Pentatonic;
import com.onquantum.rockstar.guitars.GuitarInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 8/28/14.
 */
public class GuitarSurfaceViewSlideTest extends GuitarSurfaceAbstract {
    private Context context;
    private GuitarSurfaceRenderer guitarSurfaceRenderer;

    private final int frets;
    private int width, height;
    private int titleBarH = 0;

    private boolean isSoundLoaded = false;

    private int[][] touchMask = new int[24][7];
    List<GuitarStringTest> simpleTouchList = new ArrayList<GuitarStringTest>(10);
    List<ExtSoundPool>listSoundPools = new ArrayList<ExtSoundPool>(6);


    public GuitarSurfaceViewSlideTest(final Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
        getHolder().addCallback(guitarSurfaceRenderer = new GuitarSurfaceRenderer(context));
        this.context = context;
        frets = new Settings(context).getFretNumbers();

        for (int i = 0; i<6; i++) {
            ExtSoundPool extSoundPool = new ExtSoundPool(10,AudioManager.STREAM_MUSIC,0, context,i);
            listSoundPools.add(extSoundPool);
            simpleTouchList.add(new GuitarStringTest(0,0,0,context));
        }
        isSoundLoaded = true;
        guitarSurfaceRenderer.enableRendering(true);
        if (onSoundLoadedCompleteListener != null) {
            onSoundLoadedCompleteListener.onSoundLoadedComplete();
        }
    }


    @Override
    public void onSizeChanged(int width, int height, int oldw, int oldh) {
        this.width = width;
        this.height = height;
        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        titleBarH = size.y - this.height;
        super.onSizeChanged(width,height,oldw,oldh);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if(!isSoundLoaded)
            return true;

        int actionMask = event.getActionMasked();
        int pointIndex = event.getActionIndex();
        final int pointID = event.getPointerId(pointIndex);

        int x,y;
        y = (int)((height - (event.getY(pointIndex))) / (height / 6));
        x = (int)(width - event.getX(pointIndex)) / (width / frets);
        Log.i("info"," touch x = " + x + " y = " + y);

        switch(actionMask) {
            case MotionEvent.ACTION_POINTER_DOWN:{
                if(touchMask[x][y] == 1)
                    break;
                guitarSurfaceRenderer.onTouchDown((int)x,(int)y);
                simpleTouchList.get(pointID).set((int)x,(int)y,listSoundPools.get(y));
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                if(touchMask[x][y] == 1)
                    break;
                guitarSurfaceRenderer.onTouchDown((int)x,(int)y);
                simpleTouchList.get(pointID).set((int)x,(int)y,listSoundPools.get(y));
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:  {
                touchMask[x][y] = 0;
                guitarSurfaceRenderer.onTouchUp((int)x,(int)y);
                simpleTouchList.get(pointID).stop();
                break;
            }
            case MotionEvent.ACTION_UP: {
                touchMask[x][y] = 0;
                guitarSurfaceRenderer.onTouchUp((int)x,(int)y);
                simpleTouchList.get(pointID).stop();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                guitarSurfaceRenderer.onTouchMove(x,y);
                int _x = simpleTouchList.get(pointID).x;
                if(_x != x) {
                    simpleTouchList.get(pointID).move(x,y);
                }
            }
            default:break;
        }

        return true;
    }

    @Override
    public void LoadPentatonicFile(String fileName) {
        List<Pentatonic> pentatonics = new ArrayList<Pentatonic>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open("pentatonic/" + fileName)));
            String str = reader.readLine();
            while (str != null) {
                Pentatonic pentatonic = new Pentatonic();
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
            if(guitarSurfaceRenderer.LoadPentatonic(pentatonics)) {
                if (context instanceof GuitarInterface) {
                    ((GuitarInterface)context).onPentatonicSuccessLoaded(fileName);
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void ClosePlayPentatonic() {
        guitarSurfaceRenderer.ClosePlayPentatonic();
    }

    @Override
    public void Stop() {

        Log.i("info"," SurfaceView STOP");
        for(int i = 0; i<listSoundPools.size(); i++) {
            listSoundPools.get(i).release();
        }
    }
}
