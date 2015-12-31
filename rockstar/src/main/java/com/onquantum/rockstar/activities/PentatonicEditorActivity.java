package com.onquantum.rockstar.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.onquantum.rockstar.R;
import com.onquantum.rockstar.RockStarApplication;
import com.onquantum.rockstar.Settings;
import com.onquantum.rockstar.dialogs.SaveFileDialog;
import com.onquantum.rockstar.dialogs.SetBpmDialog;
import com.onquantum.rockstar.file_system.FileSystem;
import com.onquantum.rockstar.pentatonic_editor.BarSelectView;
import com.onquantum.rockstar.pentatonic_editor.NotePanelSurfaceView;
import com.onquantum.rockstar.pentatonic_editor.PentatonicEditorSurfaceView;
import com.onquantum.rockstar.sequencer.QSoundPool;
import com.onquantum.rockstar.sequencer.QTabsPlayer;
import com.onquantum.rockstar.svprimitive.DrawEngineInterface;
import com.onquantum.rockstar.tabulature.SimpleTab;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 8/10/15.
 */
public class PentatonicEditorActivity extends Activity{

    private static final int PICK_TABS_FILE_REQUEST = 1;

    NotePanelSurfaceView notePanelSurfaceView;
    PentatonicEditorSurfaceView pentatonicEditorSurfaceView;
    BarSelectView barSelectView;

    private ImageButton buttonNoteWhole;
    private ImageButton buttonNoteHalf;
    private ImageButton buttonNoteQuartet;
    private ImageButton buttonNoteEight;
    private ImageButton buttonNoteSixteenth;

    private ImageButton playTabsButton;
    QTabsPlayer player;

    private String tabsFileName = null;

    private int BPM = 240;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.pentatonic_editor);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/BaroqueScript.ttf");
        ((TextView)this.findViewById(R.id.textView0)).setTypeface(typeface);
        BPM = new Settings(this).getBPM();

        // Note button
        (buttonNoteWhole = (ImageButton)findViewById(R.id.buttonNoteWhole)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableNoteButton();
                v.setSelected(true);
                pentatonicEditorSurfaceView.SetQuartetNote(1);
            }
        });
        (buttonNoteHalf = (ImageButton)findViewById(R.id.buttonNoteHalf)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableNoteButton();
                v.setSelected(true);
                pentatonicEditorSurfaceView.SetQuartetNote(2);
            }
        });
        (buttonNoteQuartet = (ImageButton)findViewById(R.id.buttonNoteQuartet)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableNoteButton();
                v.setSelected(true);
                pentatonicEditorSurfaceView.SetQuartetNote(4);
            }
        });
        (buttonNoteEight = (ImageButton)findViewById(R.id.buttonNoteEight)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableNoteButton();
                v.setSelected(true);
                pentatonicEditorSurfaceView.SetQuartetNote(8);
            }
        });
        (buttonNoteSixteenth = (ImageButton)findViewById(R.id.buttonNoteSixteenth)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableNoteButton();
                v.setSelected(true);
                pentatonicEditorSurfaceView.SetQuartetNote(16);
            }
        });
        buttonNoteWhole.setSelected(true);

        // Player button
        (playTabsButton = (ImageButton)findViewById(R.id.playTabs)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                if(v.isSelected()) {
                    if(player != null) {
                        player.Stop();
                        player = null;
                    }
                    if(pentatonicEditorSurfaceView.GetSimpleTabList().size() == 0) {
                        v.setSelected(!v.isSelected());
                        return;
                    }
                    player = new QTabsPlayer(getApplicationContext(), pentatonicEditorSurfaceView.GetSimpleTabList());
                    player.SetOnPlayInterface(new QTabsPlayer.TabPlayInterface() {
                        @Override
                        public void Stop() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    playTabsButton.setSelected(false);
                                    pentatonicEditorSurfaceView.Stop();
                                }
                            });
                        }
                        @Override
                        public void Start() {
                            pentatonicEditorSurfaceView.Play();
                        }

                        @Override
                        public void CurrentPlayTab(SimpleTab simpleTab) {
                            pentatonicEditorSurfaceView.SetSelectedBar(simpleTab.getGuitarBar());
                            barSelectView.SetCurrentBar(simpleTab.getGuitarBar());
                        }
                    });
                    player.Play();
                } else {
                    if(player != null)
                        player.Stop();
                    player = null;
                    pentatonicEditorSurfaceView.Stop();
                }
            }
        });
        ((ImageButton)findViewById(R.id.fastForward)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN : {
                        pentatonicEditorSurfaceView.FastForward(true);
                        break;
                    }
                    case MotionEvent.ACTION_UP:{
                        pentatonicEditorSurfaceView.FastForward(false);
                        break;
                    }
                }
                return true;
            }
        });

        ((ImageButton)findViewById(R.id.fastRewind)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN : {
                        pentatonicEditorSurfaceView.FastRewind(true);
                        break;
                    }
                    case MotionEvent.ACTION_UP:{
                        pentatonicEditorSurfaceView.FastRewind(false);
                        break;
                    }
                }
                return true;
            }
        });


        // Edit button
        ((ImageButton)findViewById(R.id.buttonUndo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pentatonicEditorSurfaceView.Undo();
            }
        });
        ((ImageButton)findViewById(R.id.clearAll)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pentatonicEditorSurfaceView.ClearAll();
            }
        });

        // File operation
        ((ImageButton)findViewById(R.id.saveFile)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveFileDialog();
            }
        });
        ((ImageButton)findViewById(R.id.openFile)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(PentatonicEditorActivity.this, LoadTabActivity.class),PICK_TABS_FILE_REQUEST);
            }
        });

        // Settings
        ((ImageButton)findViewById(R.id.setupMetronom)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetBPMDialog();
            }
        });

        ((ImageButton)findViewById(R.id.backButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player != null) {
                    player.Stop();
                    pentatonicEditorSurfaceView.Stop();
                    player = null;
                }
                finish();
            }
        });
    }

    private boolean showOnStart = false;
    @Override
    public void onStart() {
        super.onStart();

        Log.i("info","Editor on start");
        notePanelSurfaceView = (NotePanelSurfaceView)findViewById(R.id.noteSurfaceView);
        pentatonicEditorSurfaceView = (PentatonicEditorSurfaceView)findViewById(R.id.chordBookSurfaceView);
        // Load tabs from cache
        pentatonicEditorSurfaceView.SetOnDrawEngineInterface(new DrawEngineInterface() {
            @Override
            public void SurfaceSuccessCreated() {
                Log.i("info"," ---- SURFACE SUCCESS CREATED ----");
                String path = FileSystem.GetCachePath() + "/cache_tabs";
                List<SimpleTab>simpleTabList = SimpleTab.LoadTabsFromXmlFile(path);
                if(simpleTabList != null && simpleTabList.size() > 0)
                    pentatonicEditorSurfaceView.LoadTabs(simpleTabList);
            }
        });


        barSelectView = (BarSelectView)findViewById(R.id.barSelect);
        barSelectView.SetOnBarSelectListener(new BarSelectView.OnBarSelectListener() {
            @Override
            public void onBarSelect(int barSelected) {
                Log.i("info"," BAR SELECTTED : " + barSelected);
                pentatonicEditorSurfaceView.SetSelectedBar(barSelected);
                String[] notes = new String[6];
                for (int i = 0; i < 6; i++) {
                    String note = QSoundPool.getInstance().GetNoteForGuitarString(barSelected,i);
                    notes[i] = note;
                }
                notePanelSurfaceView.DrawNote(notes);
            }
        });


        if(!showOnStart) {
            showOnStart = true;
            SetBPMDialog();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        Log.i("info"," PAUSE");
        super.onPause();
        if(setBpmDialog != null) {
            Log.i("info"," DISMISS DIALOG");
            setBpmDialog.dismiss();
        }
        // Cache current work
        if(pentatonicEditorSurfaceView.GetSimpleTabList().size() > 0) {
            String path = FileSystem.GetCachePath() + "/cache_tabs";
            SimpleTab.SaveTabsToXmlFile(path, pentatonicEditorSurfaceView.GetSimpleTabList());
        }
    }
    private void disableNoteButton() {
        buttonNoteWhole.setSelected(false);
        buttonNoteHalf.setSelected(false);
        buttonNoteQuartet.setSelected(false);
        buttonNoteEight.setSelected(false);
        buttonNoteSixteenth.setSelected(false);
    }

    private void StartBuildEditorView() {
        notePanelSurfaceView = (NotePanelSurfaceView)findViewById(R.id.noteSurfaceView);
        pentatonicEditorSurfaceView = (PentatonicEditorSurfaceView)findViewById(R.id.chordBookSurfaceView);
        barSelectView = (BarSelectView)findViewById(R.id.barSelect);
        barSelectView.SetOnBarSelectListener(new BarSelectView.OnBarSelectListener() {
            @Override
            public void onBarSelect(int barSelected) {
                Log.i("info"," BAR SELECTTED : " + barSelected);
                pentatonicEditorSurfaceView.SetSelectedBar(barSelected);
            }
        });
    }
    // Dialog
    private void SaveFileDialog() {
        SaveFileDialog saveFileDialog = new SaveFileDialog();
        Bundle arguments = new Bundle();
        if(tabsFileName != null)
            arguments.putString("file_name",tabsFileName);
        arguments.putString("title","Save tabs");
        saveFileDialog.setArguments(arguments);
        saveFileDialog.SetOnSaveFileListener(new SaveFileDialog.OnSaveFileListener() {
            @Override
            public void OnSaveFile(String fileName) {
                if(fileName == null || fileName.isEmpty()) {
                    Toast.makeText(PentatonicEditorActivity.this,"Please provide file name",Toast.LENGTH_SHORT).show();
                } else {
                    // Save file
                    tabsFileName = fileName;
                    String path = FileSystem.GetTabsFilesPath();
                    if(path != null) {
                        path = path + "/" + fileName;
                        if(!SimpleTab.SaveTabsToXmlFile(path,pentatonicEditorSurfaceView.GetSimpleTabList())) {
                            Toast.makeText(PentatonicEditorActivity.this, "File system error",Toast.LENGTH_SHORT);
                        }
                    } else {
                        Toast.makeText(PentatonicEditorActivity.this, "File system error",Toast.LENGTH_SHORT);
                    }
                }
            }
        });
        saveFileDialog.show(getFragmentManager(), "SAVE_TABS");
    }

    // Set BPM
    private boolean bpmDialogIsShow = false;
    SetBpmDialog setBpmDialog = null;
    private void SetBPMDialog() {
        setBpmDialog = new SetBpmDialog();
        setBpmDialog.SetBpmListener(new SetBpmDialog.SetBpmInterface() {
            @Override
            public void OnSetBpm(int bpm) {
                Log.i("info"," SELECT BPM = " + bpm);
                pentatonicEditorSurfaceView.SetBPM(bpm);
                bpmDialogIsShow = false;
                setBpmDialog = null;
            }
            @Override
            public void OnCancelDialog() {
                Log.i("info"," ------ CANCEL DIALOG");
                bpmDialogIsShow = false;
                setBpmDialog = null;
            }
        });
        setBpmDialog.show(getFragmentManager(),"SET_BPM");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == PICK_TABS_FILE_REQUEST) {
            if(resultCode == RESULT_OK) {
                tabsFileName = intent.getStringExtra("fileName");
                if(tabsFileName != null && !tabsFileName.isEmpty()) {
                    pentatonicEditorSurfaceView.LoadTabs(SimpleTab.LoadTabsFromXmlFile(FileSystem.GetTabsFilesPath() + "/" + tabsFileName));
                }
            }
        }
    }
}