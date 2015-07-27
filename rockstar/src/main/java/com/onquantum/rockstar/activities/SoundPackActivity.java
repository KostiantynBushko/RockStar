package com.onquantum.rockstar.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.TypeAdapterFactory;
import com.onquantum.rockstar.QURL.QURL;
import com.onquantum.rockstar.R;
import com.onquantum.rockstar.file_system.FileSystem;
import com.onquantum.rockstar.gsqlite.DBGuitarTable;
import com.onquantum.rockstar.gsqlite.GuitarEntity;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Admin on 7/23/15.
 */
public class SoundPackActivity extends Activity {

    private GuitarEntity guitarEntity;
    private MediaPlayer mediaPlayer;
    private String urlSampleSound = QURL.GET_SAMPLE_SOUND;

    private ImageButton playButton;
    private ImageButton stopButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sound_pack_layout);

        guitarEntity = DBGuitarTable.GetGuitarEntityByID(this, getIntent().getIntExtra(DBGuitarTable.ID, 1));
        urlSampleSound += guitarEntity.sample_sound;


        Typeface typeFaceBaroque = Typeface.createFromAsset(getAssets(), "font/BaroqueScript.ttf");
        TextView textView = (TextView)findViewById(R.id.textView0);
        textView.setText((CharSequence) guitarEntity.name);
        textView.setTypeface(typeFaceBaroque);

        Typeface typeFaceCapture = Typeface.createFromAsset(getAssets(), "font/Capture_it.ttf");
        TextView soundPackName = (TextView)findViewById(R.id.textView14);
        soundPackName.setText(guitarEntity.name);
        soundPackName.setTypeface(typeFaceCapture);


        File iconFile = new File(FileSystem.GetIconPath(), guitarEntity.icon);
        Bitmap bitmap = null;
        if(iconFile.exists()) {
            bitmap = BitmapFactory.decodeFile(iconFile.getAbsolutePath());
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        }

        ((ImageView) findViewById(R.id.imageView2)).setImageBitmap(bitmap);

        TextView description = (TextView)findViewById(R.id.textView16);
        description.setText(guitarEntity.description);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        playButton = (ImageButton)findViewById(R.id.playSampleSound);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //progressBar.setVisibility(View.VISIBLE);
                playButton.setVisibility(View.INVISIBLE);
                stopButton.setVisibility(View.VISIBLE);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        PlaySample();
                    }
                }, 100);
                //PlaySample();
            }
        });

        stopButton = (ImageButton)findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer != null) {
                    if(mediaPlayer.isPlaying())
                        mediaPlayer.stop();
                    mediaPlayer.release();
                }
                mediaPlayer = null;
                //progressBar.setVisibility(View.INVISIBLE);
                stopButton.setVisibility(View.INVISIBLE);
                playButton.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }

    private void PlaySample() {
        if(mediaPlayer != null && mediaPlayer.isPlaying())
            mediaPlayer.stop();

        mediaPlayer = null;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                Log.i("info", " MediaPlayer : Buffering Update = " + percent + " %");
                //progressBar.setVisibility(View.INVISIBLE);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i("info", " MediaPlayer : completion listener");
                //progressBar.setVisibility(View.INVISIBLE);
                stopButton.setVisibility(View.INVISIBLE);
                playButton.setVisibility(View.VISIBLE);
            }
        });
        try {
            mediaPlayer.setDataSource(urlSampleSound);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
