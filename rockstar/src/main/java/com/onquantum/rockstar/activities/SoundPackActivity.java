package com.onquantum.rockstar.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onquantum.rockstar.QURL.QURL;
import com.onquantum.rockstar.R;
import com.onquantum.rockstar.RockStarApplication;
import com.onquantum.rockstar.common.Constants;
import com.onquantum.rockstar.gsqlite.DBGuitarTable;
import com.onquantum.rockstar.gsqlite.DBPurchaseTable;
import com.onquantum.rockstar.gsqlite.GuitarEntity;
import com.onquantum.rockstar.gsqlite.PurchaseEntity;
import com.onquantum.rockstar.sequencer.QSoundPool;
import com.onquantum.rockstar.services.DownloadSoundPackage;
import com.onquantum.rockstar.util.IabHelper;
import com.onquantum.rockstar.util.IabResult;
import com.onquantum.rockstar.util.Inventory;
import com.onquantum.rockstar.util.Purchase;
import com.onquantum.rockstar.util.SkuDetails;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kostiantyn Bushko on 7/23/15.
 */
public class SoundPackActivity extends Activity implements View.OnClickListener, DownloadSoundPackage.OnProgressUpdate{

    private static final String TAG = SoundPackActivity.class.getSimpleName();

    private GuitarEntity guitarEntity;
    private PurchaseEntity purchaseEntity;


    private MediaPlayer mediaPlayer;
    private String urlSampleSound = QURL.GET_SAMPLE_SOUND;

    private ImageButton playButton;
    private ProgressBar progressBar;
    private Button applySoundPack;
    private Button downloadSoundPack;
    private Button buySoundPack;
    private Button StartPlay;
    private RelativeLayout controlLayout;

    private ServiceConnection serviceConnection;
    private boolean isBanded = false;
    private BroadcastReceiver broadcastReceiver;

    private RelativeLayout progressLayout;
    private ProgressBar downloadSoundProgress;
    private TextView progressPercentage;

    private DownloadSoundPackage downloadSoundPackage;

    private int position = -1;

    private IabHelper iabHelper;
    private boolean iabSuccess = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sound_pack_layout);

        guitarEntity = DBGuitarTable.GetGuitarEntityByID(this, getIntent().getLongExtra(DBGuitarTable.ID, 1));
        purchaseEntity = DBPurchaseTable.GetPurchaseEntityByID(this, guitarEntity.purchase_id);
        position = getIntent().getIntExtra("position",-1);

        urlSampleSound += guitarEntity.sample_sound;


        Typeface typeFaceBaroque = Typeface.createFromAsset(getAssets(), "font/BaroqueScript.ttf");
        TextView textView = (TextView)findViewById(R.id.textView0);
        textView.setText((CharSequence) guitarEntity.name);
        textView.setTypeface(typeFaceBaroque);

        Typeface typeFaceCapture = Typeface.createFromAsset(getAssets(), "font/Capture_it.ttf");
        TextView soundPackName = (TextView)findViewById(R.id.textView14);
        soundPackName.setText(guitarEntity.name);
        soundPackName.setTypeface(typeFaceCapture);


        // Bind to download service test:
        if(!guitarEntity.isSoundPackAvailable() && isDownloadServiceRunning(DownloadSoundPackage.class)) {
            BindToDownloadService();
        }

        /*File iconFile = new File(FileSystem.GetIconPath(), guitarEntity.icon);
        Bitmap bitmap = null;
        if(iconFile.exists()) {
            bitmap = BitmapFactory.decodeFile(iconFile.getAbsolutePath());
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_guitar_white);
        }
        ((ImageView) findViewById(R.id.imageView2)).setImageBitmap(bitmap);*/

        TextView description = (TextView)findViewById(R.id.textView16);
        description.setText(guitarEntity.description);

        // Progress
        progressLayout = (RelativeLayout)findViewById(R.id.progressLayout);
        progressPercentage = (TextView)findViewById(R.id.progressPercentage);
        downloadSoundProgress = (ProgressBar)findViewById(R.id.progressBar2);
        controlLayout = (RelativeLayout)findViewById(R.id.relativeLayout9);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        playButton = (ImageButton)findViewById(R.id.playSampleSound);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!((RockStarApplication)getApplication()).isNetworkConnected()) {
                    Toast.makeText(SoundPackActivity.this, getResources().getString(R.string.no_internet_connection),Toast.LENGTH_SHORT).show();
                    return;
                }
                v.setSelected(!v.isSelected());
                if(v.isSelected()) {
                    v.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            URL url = null;
                            HttpURLConnection httpURLConnection = null;
                            try {
                                url = new URL(QURL.GET_SOUND_PACK_SAMPLE_URL);
                                httpURLConnection = (HttpURLConnection)url.openConnection();
                                httpURLConnection.setReadTimeout(10000);
                                httpURLConnection.setConnectTimeout(15000);
                                httpURLConnection.setRequestMethod("POST");
                                httpURLConnection.setDoInput(true);
                                httpURLConnection.setDoOutput(true);

                                // POST parameter
                                OutputStream outputStream = httpURLConnection.getOutputStream();
                                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                                Uri.Builder builder = new Uri.Builder();
                                builder.appendQueryParameter("sound_pack", guitarEntity.article);
                                String queryParams = builder.build().getEncodedQuery();

                                bufferedWriter.write(queryParams);
                                bufferedWriter.flush();
                                bufferedWriter.close();
                                outputStream.close();

                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                                String line;
                                final StringBuilder stringBuilder = new StringBuilder();
                                while ((line = bufferedReader.readLine()) != null) {
                                    stringBuilder.append(line);
                                }
                                if (!stringBuilder.toString().isEmpty()) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            urlSampleSound = stringBuilder.toString();
                                            progressBar.setVisibility(View.INVISIBLE);
                                            playButton.setVisibility(View.VISIBLE);
                                            playButton.setSelected(true);
                                            PlaySample();
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            playButton.setVisibility(View.VISIBLE);
                                            playButton.setSelected(false);
                                        }
                                    });
                                }
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (ProtocolException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                if(httpURLConnection != null) {
                                    httpURLConnection.disconnect();
                                }
                            }
                        }
                    }).start();
                } else {
                    StopSample();
                }
            }
        });

        applySoundPack = (Button)findViewById(R.id.buttonApplyPack);
        applySoundPack.setOnClickListener(this);
        downloadSoundPack = (Button)findViewById(R.id.downloadSoundPack);
        buySoundPack = (Button)findViewById(R.id.buySoundPack);
        StartPlay = (Button)findViewById(R.id.buttonPlaySoundPack);
        StartPlay.setOnClickListener(this);

        downloadSoundPack.setOnClickListener(this);
        buySoundPack.setOnClickListener(this);

        if(guitarEntity.is_active) {
            if(!guitarEntity.isSoundPackAvailable()) {
                applySoundPack.setVisibility(View.INVISIBLE);
                downloadSoundPack.setVisibility(View.VISIBLE);
            }else {
                applySoundPack.setVisibility(View.GONE);
                downloadSoundPack.setVisibility(View.GONE);
                //controlLayout.setVisibility(View.GONE);
                StartPlay.setVisibility(View.VISIBLE);
            }
        }else {
            if(purchaseEntity.has_purchased || (purchaseEntity.getPrice() == 0)) {
                if(!guitarEntity.isSoundPackAvailable()) {
                    applySoundPack.setVisibility(View.INVISIBLE);
                    downloadSoundPack.setVisibility(View.VISIBLE);
                }else {
                    applySoundPack.setVisibility(View.VISIBLE);
                }
            }else {
                applySoundPack.setVisibility(View.INVISIBLE);
                downloadSoundPack.setVisibility(View.INVISIBLE);
                buySoundPack.setVisibility(View.VISIBLE);
                buySoundPack.setText(purchaseEntity.price + purchaseEntity.currency_code);
            }
        }

        ((ImageButton)findViewById(R.id.backButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(DBGuitarTable.IS_ACTIVE, guitarEntity.is_active);
                intent.putExtra(DBGuitarTable.ID, guitarEntity.id);
                intent.putExtra("position",position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        iabHelper = new IabHelper(this, Constants.LICENSE_KEY);
        iabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                iabSuccess = result.isSuccess();
                if (!result.isSuccess()) {
                    Log.d(TAG,"Problem setting up In-app Billing: " + result);
                }
            }
        });
    }

    @Override
    public void onStart() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(guitarEntity.article.equals(intent.getStringExtra(DBGuitarTable.ARTICLE))) {
                    UnbindFromDownloadService();
                    Log.d(TAG,"BROADCAST MESSAGE : complete download sound package - " + guitarEntity.article);
                    downloadSoundPack.setVisibility(View.INVISIBLE);
                    buySoundPack.setVisibility(View.INVISIBLE);
                    progressLayout.setVisibility(View.INVISIBLE);
                    applySoundPack.setVisibility(View.VISIBLE);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(DownloadSoundPackage.BROADCAST_COMPLETE_DOWNLOAD_SOUND_PACKAGE_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }

        UnbindFromDownloadService();
        if(broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (iabHelper != null)
            iabHelper.dispose();
        iabHelper = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buySoundPack :{
                try {

                    //final String  SKU = "android.test.purchased";
                    final List<String>additionalSkuList = new ArrayList<>();
                    additionalSkuList.add(purchaseEntity.bundle);

                    if (iabHelper != null) {
                        iabHelper.flagEndAsync();
                    } else {
                        Log.d(TAG,"Error Iab Helper not initialised");
                        return;
                    }

                    iabHelper.launchPurchaseFlow(SoundPackActivity.this, purchaseEntity.bundle/*SKU*/, 1001, new IabHelper.OnIabPurchaseFinishedListener() {
                        @Override
                        public void onIabPurchaseFinished(IabResult result, Purchase info) {
                            if (result.isFailure()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SoundPackActivity.this);
                                builder.setTitle(getResources().getString(R.string.error));
                                builder.setIcon(R.drawable.ic_warning_white_48dp);
                                builder.setMessage(result.getMessage());
                                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                                return;

                            } else {
                                iabHelper.queryInventoryAsync(true, additionalSkuList, new IabHelper.QueryInventoryFinishedListener() {
                                    @Override
                                    public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                                        if (result.isFailure()) {
                                            return;
                                        }
                                        for (String productBundle : additionalSkuList) {
                                            SkuDetails skuDetails = inv.getSkuDetails(productBundle);
                                            if(skuDetails != null) {
                                                purchaseEntity.has_purchased = true;
                                                DBPurchaseTable.AddPurchaseEntity(SoundPackActivity.this, purchaseEntity);
                                                buySoundPack.setVisibility(View.GONE);
                                                if(!guitarEntity.isSoundPackAvailable()) {
                                                    applySoundPack.setVisibility(View.INVISIBLE);
                                                    downloadSoundPack.setVisibility(View.VISIBLE);
                                                }else {
                                                    applySoundPack.setVisibility(View.VISIBLE);
                                                }
                                            } else {
                                                Log.d(TAG," SKU DETAIL : NULL");
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    AlertDialog.Builder builder = new AlertDialog.Builder(SoundPackActivity.this);
                    builder.setTitle(getResources().getString(R.string.error));
                    builder.setIcon(R.drawable.ic_warning_white_48dp);
                    builder.setMessage(e.getMessage());
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
                break;
            }
            case R.id.downloadSoundPack: {
                Log.d(TAG,"START To DOWNLOAD PACKAGE name = " + guitarEntity.article);
                StartDownloadPackage();
                break;
            }
            case R.id.buttonApplyPack: {
                //controlLayout.setVisibility(View.GONE);
                applySoundPack.setVisibility(View.INVISIBLE);
                StartPlay.setVisibility(View.VISIBLE);
                DBGuitarTable.SetActiveGuitar(this, (long) guitarEntity.id);
                guitarEntity.is_active = true;
                QSoundPool.getInstance().releaseSoundPool();
                QSoundPool.getInstance().loadSound();
                break;
            }case R.id.buttonPlaySoundPack: {
                startActivity(new Intent(this, GuitarSimulatorActivity.class));
                break;
            }
            default:break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass on the activity result to the helper for handling
        if (!iabHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    /**
     * Download sound package files
     */
    private void StartDownloadPackage() {
        if(!((RockStarApplication)getApplication()).isNetworkConnected()) {
            Toast.makeText(SoundPackActivity.this, getResources().getString(R.string.no_internet_connection),Toast.LENGTH_SHORT).show();
            return;
        }
        downloadSoundPack.setVisibility(View.INVISIBLE);
        applySoundPack.setVisibility(View.INVISIBLE);
        buySoundPack.setVisibility(View.INVISIBLE);
        progressLayout.setVisibility(View.VISIBLE);
        for (int i = 0; i < 25; i++) {
            for (int j = 0; j < 6; j++) {
                Intent intent = new Intent(this, DownloadSoundPackage.class);
                intent.putExtra("sound_pack",guitarEntity.article);
                intent.putExtra("file_name",guitarEntity.article + "_" + i + "_" + j + ".ogg");
                startService(intent);
            }
        }
        BindToDownloadService();
    }

    private boolean isDownloadServiceRunning(Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if(serviceClass.getName().equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private boolean BindToDownloadService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d(TAG,"Bind to the download service");
                isBanded = true;
                downloadSoundPackage = ((DownloadSoundPackage.DownloadPackBinder)binder).getServiceInstance();
                if(!downloadSoundPackage.isSoundPackInProgress(guitarEntity.article)) {
                    return;
                }
                long currentProgress = downloadSoundPackage.GetProgressForSoundPack(guitarEntity.article);
                controlLayout.setVisibility(View.VISIBLE);
                progressLayout.setVisibility(View.VISIBLE);
                downloadSoundPack.setVisibility(View.INVISIBLE);
                downloadSoundProgress.setProgress((int)currentProgress);
                progressPercentage.setText((int)currentProgress + "%");
                downloadSoundPackage.SetOnProgressUpdateListener(SoundPackActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG,"Disconnect from download service");
                UnbindFromDownloadService();
                isBanded = false;
            }
        };

        Intent intent = new Intent(this, DownloadSoundPackage.class);
        bindService(intent, serviceConnection, BIND_ADJUST_WITH_ACTIVITY);
        return false;
    }

    private void UnbindFromDownloadService() {
        if(isBanded /*&& isDownloadServiceRunning(DownloadSoundPackage.class)*/) {
            if(serviceConnection == null)
                return;
            unbindService(serviceConnection);
        }
        if(downloadSoundPackage != null)
            downloadSoundPackage.SetOnProgressUpdateListener(null);
        serviceConnection = null;
    }

    @Override
    public void updateProgress(String soundPackage, final long progress) {
        if (soundPackage.equals(guitarEntity.article)) {
            Log.d(TAG, " UPDATE PROGRESS SOUND PACK = " + soundPackage + " PROGRESS = " + progress);
            downloadSoundProgress.setProgress((int)progress);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressPercentage.setText((int)progress + "%");
                }
            });
        }
    }

    /**
     * Media player, play sample sound.
     */
    private void PlaySample() {
        if(mediaPlayer != null && mediaPlayer.isPlaying())
            mediaPlayer.stop();

        mediaPlayer = null;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                Log.d(TAG, " MediaPlayer : Buffering Update = " + percent + " %");
                //progressBar.setVisibility(View.INVISIBLE);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, " MediaPlayer : completion listener");
                //progressBar.setVisibility(View.INVISIBLE);
                //stopButton.setVisibility(View.INVISIBLE);
                /*mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                playButton.setSelected(!playButton.isSelected());*/
                StopSample();
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

    private void StopSample() {
        //progressBar.setVisibility(View.INVISIBLE);
        //playButton.setVisibility(View.VISIBLE);
        playButton.setSelected(false);
        if(mediaPlayer != null) {
            if(mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = null;
    }

}
