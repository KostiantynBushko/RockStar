package com.onquantum.rockstar.guitar;

import android.content.Context;
import android.graphics.Point;
import android.media.SoundPool;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.onquantum.rockstar.Settings;
import com.onquantum.rockstar.common.GuitarString;
import com.onquantum.rockstar.common.Pentatonic;
import com.onquantum.rockstar.file_system.FileSystem;
import com.onquantum.rockstar.sequencer.QSoundPool;
import com.onquantum.rockstar.tabulature.SimpleTab;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Admin on 8/20/14.
 */
public class GuitarDefaultView extends GuitarAbstractView {

    private Context context;
    private SoundPool soundPool;
    private GuitarRenderer guitarRenderer;
    private int titleBarH = 0;
    private final int frets;

    private int[][] fretMaskStreamID = new int[25][7];
    private int[][] touchMask = new int[25][7];
    List<GuitarString> simpleTouchList = new ArrayList<GuitarString>(10);
    int width,height;

    private int[][] samplesID;

    private int currentFret = 0;
    private int currentString = 0;
    private int Slide = 0;

    private boolean openString = false;

    public GuitarDefaultView(final Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        getHolder().addCallback(guitarRenderer = new GuitarRenderer(context));

        frets = new Settings(context).getFretNumbers();
        samplesID = new int[new Settings(context).getFretNumbers()][6];

        soundPool = QSoundPool.getInstance().getSoundPool();
        if (soundPool == null) {
            QSoundPool.getInstance().loadSound();
        }
        for(int i = 0; i<10; i++) {
            simpleTouchList.add(new GuitarString(0,0,0,context,soundPool));
        }
        this.context = context;
        openString = new Settings(context).getOpenStringStatus();
    }
    @Override
    public void Start() {
        QSoundPool.getInstance().setOnSoundPoolSuccessLoaded(new QSoundPool.OnSoundPoolSuccessLoaded() {
            @Override
            public void soundPoolSuccessLoaded() {
                isTouchEnable = true;
                if (guitarRenderer != null)
                    guitarRenderer.enableRendering(true);
                if (onSoundLoadedCompleteListener != null) {
                    Log.i("info"," SOUND LOAD COMPLETE ");
                    onSoundLoadedCompleteListener.onSoundLoadedComplete();
                }
            }
        });
        super.Start();
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
        if (!isTouchEnable)
            return false;
        int actionMask = event.getActionMasked();
        int pointIndex = event.getActionIndex();
        final int pointID = event.getPointerId(pointIndex);

        int x,y;
        y = (int)(height - (event.getY(pointIndex))) / (height / 6);
        x = (int)(width - event.getX(pointIndex)) / (width / frets) + 1;
        if (y > 5)
            return false;

        if(openString) {
            if(x > frets - 1){
                Log.i("info"," OPEN STRING TOUCH");
                x = 0;
            } else {
                x+= Slide;
            }
        } else {
            x+= Slide;
        }

        int playId = (y + 1) + (6 * x);

        switch(actionMask) {
            case MotionEvent.ACTION_POINTER_DOWN:{
                if(touchMask[x][y] == 1)
                    break;
                guitarRenderer.onTouchDown(x,y);
                if(fretMaskStreamID[x][y] != 0) {
                    int id = fretMaskStreamID[x][y];
                    soundPool.stop(id);
                }
                fretMaskStreamID[x][y] = soundPool.play(playId, 1, 1, 1, 0, 1.0f);
                simpleTouchList.get(pointID).set(fretMaskStreamID[x][y],x,y);
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                if(touchMask[x][y] == 1)
                    break;
                guitarRenderer.onTouchDown(x,y);
                if(fretMaskStreamID[x][y] != 0) {
                    int id = fretMaskStreamID[x][y];
                    soundPool.stop(id);
                }
                fretMaskStreamID[x][y]= soundPool.play(playId, 1, 1, 1, 0, 1.0f);
                simpleTouchList.get(pointID).set(fretMaskStreamID[x][y],x,y);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:  {
                touchMask[x][y] = 0;
                guitarRenderer.onTouchUp(x,y);
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
                guitarRenderer.onTouchUp(x,y);
                simpleTouchList.get(pointID).stop();
                new Thread(new Runnable() {
                    int _x = simpleTouchList.get(pointID).x;
                    int _y = simpleTouchList.get(pointID).y;
                    int _id = simpleTouchList.get(pointID).streamId;
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
            case MotionEvent.ACTION_MOVE: {
                guitarRenderer.onTouchMove(x,y);
            }
            default:break;
        }
        return true;
    }


    @Override
    public void LoadTabsFile(String fileName) {
        int BPM = new Settings(context).getBPM();
        File file = new File(FileSystem.GetTabsFilesPath() + "/" + fileName);
        if(!file.exists())
            return;
        List<SimpleTab>simpleTabList = new ArrayList<SimpleTab>();
        simpleTabList = SimpleTab.LoadTabsFromXmlFile(file.toString());
        if(simpleTabList == null || simpleTabList.size() == 0)
            return;

        List<Pentatonic> pentatonics = new ArrayList<>();
        for(int i = 0; i < simpleTabList.size(); i++) {
            Pentatonic pentatonic = new Pentatonic();
            SimpleTab simpleTab = simpleTabList.get(i);
            pentatonic.bar = simpleTab.getGuitarBar();
            pentatonic.line = simpleTab.getGuitarString();
            pentatonic.delay = simpleTab.getDurationMS(BPM);
            pentatonic.playTime = simpleTab.getStartQuartetMS(BPM);
            pentatonic.position.set(pentatonic.bar, pentatonic.line);
            pentatonics.add(pentatonic);
        }
        if(guitarRenderer.LoadPentatonic(pentatonics)) {
            if (context instanceof GuitarInterface) {
                ((GuitarInterface)context).onPentatonicSuccessLoaded(fileName);
            }
        }
        /*List<Pentatonic> pentatonics = new ArrayList<Pentatonic>();
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
                    pentatonic.position.set(pentatonic.bar, pentatonic.line);
                    pentatonics.add(pentatonic);
                }
                str = reader.readLine();
            }
            if(guitarRenderer.LoadPentatonic(pentatonics)) {
                if (context instanceof GuitarInterface) {
                    ((GuitarInterface)context).onPentatonicSuccessLoaded(fileName);
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }*/
    }
    @Override
    public void ClosePlayPentatonic() {
        guitarRenderer.ClosePlayPentatonic();
    }

    @Override
    public void Stop() {
        //soundPool.release();
    }


    @Override
    public boolean isInEditMode() {
        return true;
    }


    // Neck view
    @Override
    public void SetFretsNumberVisible(boolean visibility){
        guitarRenderer.setFretNumberVisible(visibility);
    }
    @Override
    public void SetShowTouchesVisible(boolean visibility){
        guitarRenderer.setShowTouchEnable(visibility);
    }
    @Override
    public void SetFretsSliderVisible(boolean visibility){
        guitarRenderer.resetLoaded();
    }

    @Override
    public void slideChange(int slide) {
        Slide += slide;
        guitarRenderer.slide(slide);
    }
}
