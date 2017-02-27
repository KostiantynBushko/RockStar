package com.onquantum.rockstar.sequencer;

import android.content.Context;
import android.os.SystemClock;

import com.onquantum.rockstar.settings.Settings;
import com.onquantum.rockstar.common.GuitarString;
import com.onquantum.rockstar.tabulature.SimpleTab;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Admin on 12/27/15.
 */
public class QTabsPlayer {

    private List<SimpleTab> tabs = null;
    private long timeLine = 0;
    private long totalTime = 0;

    private Timer timer;
    private TimerTask timerTask;

    private boolean pause = false;
    private Context context;

    private int BPM = 240;

    public interface TabPlayInterface {
        public void Stop();
        public void Start();
        public void CurrentPlayTab(SimpleTab simpleTab);
    }

    public void SetOnPlayInterface(TabPlayInterface tabPlayInterface) {
        this.tabPlayInterface = tabPlayInterface;
    }

    private TabPlayInterface tabPlayInterface;

    public QTabsPlayer(Context context, List<SimpleTab> tabs) {
        this.tabs = tabs;
        this.context = context;
        BPM = new Settings(context).getBPM();
        totalTime = calculateTime();
    }


    private long calculateTime() {
        //Log.i("info"," CALCULATE TIME : tabs size = " + tabs.size());
        if(tabs == null || tabs.size() == 0)
            return 0;
        long lastTime = 0;
        Iterator<SimpleTab>iterator = tabs.iterator();
        SimpleTab simpleTab = iterator.next();
        lastTime = simpleTab.getStartQuartetMS(BPM) + simpleTab.getDurationMS(BPM);

        while (iterator.hasNext()) {
            simpleTab = iterator.next();
            long time = simpleTab.getStartQuartetMS(BPM) + simpleTab.getDurationMS(BPM);
            if(time > lastTime)
                lastTime = time;
        }
        //Log.i("info","QTabPlayer : time = " + lastTime);
        return lastTime;
    }

    List<Timer>timers;
    public void Play() {
        isStop = false;
        timers = new ArrayList<Timer>();

        if(tabPlayInterface != null)
            tabPlayInterface.Start();
        for (int i = 0; i < tabs.size(); i++) {
            Timer timer = new Timer();
            PNote note = new PNote(tabs.get(i));
            timer.schedule(note,tabs.get(i).getStartQuartetMS(BPM));
            timers.add(timer);
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeLine += 1;
                if(timeLine >= totalTime) {
                    timer.cancel();
                    timer = null;
                    if(tabPlayInterface != null)
                        tabPlayInterface.Stop();
                }
            }
        }, 0, 1);
    }

    /*public void Play1() {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        for (int i = 0; i < tabs.size(); i++) {
            new PlayNote(tabs.get(i)).start();
        }
        isStop = false;
        timer = new Timer();
        if(tabPlayInterface != null)
            tabPlayInterface.Start();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeLine += 1;
                if(timeLine >= totalTime) {
                    timer.cancel();
                    timer = null;
                    if(tabPlayInterface != null)
                        tabPlayInterface.Stop();
                }
            }
        }, 0, 1);
    }*/

    public void Stop() {
        //Log.i("info"," Player STOP");
        isStop = true;
        timeLine = totalTime;
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        for (int i = 0; i < timers.size(); i++) {
            if(timers.get(i) != null)
                timers.get(i).cancel();
        }
    }

    private boolean isStop = true;

    private class PNote extends TimerTask{
        SimpleTab simpleTab;
        long timeDuration = 0;
        long startTime = 0;
        public PNote(SimpleTab simpleTab) {
            this.simpleTab = simpleTab;
            timeDuration = this.simpleTab.getDurationMS(BPM);
            startTime = simpleTab.getStartQuartetMS(BPM);
        }

        @Override
        public void run() {
            //Log.i("info","START PLAY start : " + startTime + " end : " + timeDuration);
            GuitarString guitarString = new GuitarString(0,0,0,context, QSoundPool.getInstance().getSoundPool());
            guitarString.playSimpleString(simpleTab.getGuitarBar(), simpleTab.getGuitarString());
            if(tabPlayInterface != null)
                tabPlayInterface.CurrentPlayTab(this.simpleTab);
            SystemClock.sleep(timeDuration);
            //Log.i("info"," STOP PLAY STRING = " + simpleTab.getGuitarString());
            guitarString.stopSimpleString();
        }
    }

    /*private class PlayNote extends Thread {
        SimpleTab simpleTab;
        long timeDuration = 0;
        long startTime = 0;
        public PlayNote(SimpleTab simpleTab) {
            this.simpleTab = simpleTab;
            timeDuration = this.simpleTab.getDurationMS(BPM);
            startTime = simpleTab.getStartQuartetMS(BPM);
            Log.i("info","START PLAY start : " + startTime + " end : " + timeDuration);
        }
        @Override
        public void run() {
            while (timeLine < startTime || isStop == true);
            if(isStop) {
                Log.i("info"," INTERRUPT PLAY STRING : " + simpleTab.getGuitarString());
                return;
            } else {
                GuitarString guitarString = new GuitarString(0,0,0,context, QSoundPool.getInstance().getSoundPool());
                guitarString.playSimpleString(simpleTab.getGuitarBar(), simpleTab.getGuitarString());
                try {
                    sleep(timeDuration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    Log.i("info"," STOP PLAY STRING = " + simpleTab.getGuitarString());
                    guitarString.stopSimpleString();
                }
            }
        }
    }*/

    public void SetBPM(int bpm) {
        this.BPM = bpm;
    }
}
