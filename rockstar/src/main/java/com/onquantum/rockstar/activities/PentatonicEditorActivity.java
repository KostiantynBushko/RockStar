package com.onquantum.rockstar.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onquantum.rockstar.R;
import com.onquantum.rockstar.RockStarApplication;
import com.onquantum.rockstar.Settings;
import com.onquantum.rockstar.common.Constants;
import com.onquantum.rockstar.common.SpeechBubble;
import com.onquantum.rockstar.dialogs.SaveFileDialog;
import com.onquantum.rockstar.dialogs.SetBpmDialog;
import com.onquantum.rockstar.file_system.FileSystem;
import com.onquantum.rockstar.gsqlite.DBGuitarTable;
import com.onquantum.rockstar.gsqlite.GuitarEntity;
import com.onquantum.rockstar.pentatonic_editor.BarSelectView;
import com.onquantum.rockstar.pentatonic_editor.NotePanelSurfaceView;
import com.onquantum.rockstar.pentatonic_editor.PentatonicEditorInterface;
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
    QTabsPlayer player = null;
    QTabsPlayer playSimpleTab = null;

    private String tabsFileName = null;

    private int BPM = 240;

    private RelativeLayout rootLayout = null;
    private SpeechBubble speechBubble = null;
    private RelativeLayout controllPanel = null;

    //private List<SimpleTab>TabBuffer = null;
    GuitarEntity guitarEntity = null;

    private ProgressDialog loadSoundPackProgress = null;
    private ProgressDialog changeBPMProgress = null;

    private Typeface typeFaceCapture;

    public interface OpenTabsInterface {
        public void SuccessSaved();
    }
    public OpenTabsInterface openTabsInterface = null;


    private long START_TIME = 0;
    private long END_TIME = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.i("info","Editor onCreate");

        this.START_TIME = System.currentTimeMillis();

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.tabulature_editor);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/BaroqueScript.ttf");
        typeFaceCapture = Typeface.createFromAsset(getAssets(), "font/Capture_it.ttf");

        ((TextView)this.findViewById(R.id.textView0)).setTypeface(typeface);

        BPM = new Settings(this).getBPM();

        guitarEntity = DBGuitarTable.GetCurrentActive(this);

        // Obtain root layout
        rootLayout = (RelativeLayout)this.findViewById(R.id.rootLayout);
        controllPanel = (RelativeLayout)this.findViewById(R.id.tabEditorControlPanel);

        // Note button
        (buttonNoteWhole = (ImageButton)findViewById(R.id.buttonNoteWhole)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveContextMenu();
                disableNoteButton();
                v.setSelected(true);
                pentatonicEditorSurfaceView.SetQuartetNote(1);
            }
        });
        (buttonNoteHalf = (ImageButton)findViewById(R.id.buttonNoteHalf)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveContextMenu();
                disableNoteButton();
                v.setSelected(true);
                pentatonicEditorSurfaceView.SetQuartetNote(2);
            }
        });
        (buttonNoteQuartet = (ImageButton)findViewById(R.id.buttonNoteQuartet)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveContextMenu();
                disableNoteButton();
                v.setSelected(true);
                pentatonicEditorSurfaceView.SetQuartetNote(4);
            }
        });
        (buttonNoteEight = (ImageButton)findViewById(R.id.buttonNoteEight)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveContextMenu();
                disableNoteButton();
                v.setSelected(true);
                pentatonicEditorSurfaceView.SetQuartetNote(8);
            }
        });
        (buttonNoteSixteenth = (ImageButton)findViewById(R.id.buttonNoteSixteenth)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveContextMenu();
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
                PlayButtonAction(v);
            }
        });
        ((ImageButton)findViewById(R.id.fastForward)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                RemoveContextMenu();
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
                RemoveContextMenu();
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
                RemoveContextMenu();
                pentatonicEditorSurfaceView.Undo();
            }
        });
        ((ImageButton)findViewById(R.id.clearAll)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveContextMenu();
                AlertDialog.Builder builder = new AlertDialog.Builder(PentatonicEditorActivity.this);
                builder.setTitle("Clear");
                builder.setMessage("Do you want to clear all tabs");
                builder.setIcon(R.drawable.ic_content_cut_white_48dp);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pentatonicEditorSurfaceView.ClearAll();
                        tabsFileName = null;
                    }
                });
                builder.create().show();
            }
        });

        // File operation
        ((ImageButton)findViewById(R.id.saveFile)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveContextMenu();
                SaveFileDialog();
            }
        });
        ((ImageButton)findViewById(R.id.openFile)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveContextMenu();
                openTabsInterface = null;
                if(tabsFileName != null && pentatonicEditorSurfaceView.needSave) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PentatonicEditorActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Save current change to '" + tabsFileName + "'");
                    builder.setIcon(R.drawable.ic_warning_white_48dp);
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            SaveFileDialog();
                            openTabsInterface = new OpenTabsInterface() {
                                @Override
                                public void SuccessSaved() {
                                    startActivityForResult(new Intent(PentatonicEditorActivity.this, LoadTabActivity.class),PICK_TABS_FILE_REQUEST);
                                }
                            };
                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivityForResult(new Intent(PentatonicEditorActivity.this, LoadTabActivity.class),PICK_TABS_FILE_REQUEST);
                        }
                    });
                    builder.create().show();
                } else {
                    startActivityForResult(new Intent(PentatonicEditorActivity.this, LoadTabActivity.class),PICK_TABS_FILE_REQUEST);
                }
            }
        });

        // Settings
        ((ImageButton)findViewById(R.id.setupMetronom)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveContextMenu();
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


    @Override
    public void onStart() {
        super.onStart();
        SetSoundPoolListener();

        // Obtain file name from intent if exist
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && getIntent().getExtras().containsKey("fileName")) {
                this.tabsFileName = getIntent().getStringExtra("fileName");
            }
        }

        notePanelSurfaceView = (NotePanelSurfaceView)findViewById(R.id.noteSurfaceView);
        pentatonicEditorSurfaceView = (PentatonicEditorSurfaceView)findViewById(R.id.chordBookSurfaceView);

        if(pentatonicEditorSurfaceView.isSuccessLoaded()) {
            List<SimpleTab>simpleTabList = null;
            String path = FileSystem.GetCachePath() + "/cache_tabs";
            simpleTabList = SimpleTab.LoadTabsFromXmlFile(path);
            if(simpleTabList != null && simpleTabList.size() > 0) {
                pentatonicEditorSurfaceView.LoadTabs(simpleTabList);
            }
        } else {
            pentatonicEditorSurfaceView.SetOnDrawEngineInterface(new DrawEngineInterface() {
                @Override
                public void SurfaceSuccessCreated() {
                    //Log.i("info"," Tab editor Surface CREATED");
                    List<SimpleTab>simpleTabList = null;
                    String path = FileSystem.GetCachePath() + "/cache_tabs";
                    simpleTabList = SimpleTab.LoadTabsFromXmlFile(path);
                    if(simpleTabList != null && simpleTabList.size() > 0) {
                        pentatonicEditorSurfaceView.LoadTabs(simpleTabList);
                    }
                }
            });
        }

        pentatonicEditorSurfaceView.SetPentatonicEditorInterface(new PentatonicEditorInterface() {
            @Override
            public void OnPentatonicEditorClickListener(MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE){
                    RemoveContextMenu();
                }
            }
            @Override
            public void OnBPMChange() {
                if (changeBPMProgress != null) {
                    changeBPMProgress.dismiss();
                    changeBPMProgress = null;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fillSoundPackInfoPanel(guitarEntity.name, Integer.toString(BPM) + " bpm");
                        }
                    });
                }
            }
            @Override
            public void OnSelectTab(final PentatonicEditorSurfaceView.Tab tab) {
                SimpleTab selectedTab = null;
                try {
                    selectedTab = (SimpleTab) tab.simpleTab.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                    return;
                }
                if(speechBubble != null) {
                    speechBubble.removeAllViews();
                    speechBubble = null;
                }

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                speechBubble = new SpeechBubble(PentatonicEditorActivity.this);
                speechBubble.setAnchor(new Point((int)(tab.shape.getX() + pentatonicEditorSurfaceView.getX()), (int)(tab.shape.getY() + pentatonicEditorSurfaceView.getY())));
                rootLayout.addView(speechBubble, params);

                View child = getLayoutInflater().inflate(R.layout.tab_context_menu, null);
                child.findViewById(R.id.imageButton3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pentatonicEditorSurfaceView.RemoveTab(tab);
                        RemoveContextMenu();
                    }
                });

                final SimpleTab finalSelectedTab = selectedTab;
                child.findViewById(R.id.imageButton2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        v.setSelected(!v.isSelected());
                        if (v.isSelected()) {
                            if(!CheckSoundPool()) {
                                v.setSelected(!v.isSelected());
                                return;
                            }
                            List<SimpleTab>simpleTab = new ArrayList<SimpleTab>(1);
                            finalSelectedTab.setStartQuartet(0);
                            simpleTab.add(finalSelectedTab);
                            playSimpleTab = new QTabsPlayer(getApplicationContext(), simpleTab);

                            playSimpleTab.SetOnPlayInterface(new QTabsPlayer.TabPlayInterface() {
                                @Override
                                public void Stop() {
                                    //Log.i("info"," Play Simple tab : Stop");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            v.setSelected(false);
                                        }
                                    });
                                }
                                @Override
                                public void Start() {
                                    //Log.i("info"," Play Simple tab : Start");
                                }
                                @Override
                                public void CurrentPlayTab(SimpleTab simpleTab) {
                                    //Log.i("info"," Play Simple tab : CurrentPlayTab");
                                }
                            });
                            playSimpleTab.Play();
                        } else {
                            if (playSimpleTab != null)
                                playSimpleTab.Stop();
                            playSimpleTab = null;
                        }
                    }
                });
                speechBubble.addView(child);
            }
            @Override
            public void OnAddTab(PentatonicEditorSurfaceView.Tab tab) {
                RemoveContextMenu();
            }
        });

        barSelectView = (BarSelectView)findViewById(R.id.barSelect);
        barSelectView.SetOnBarSelectListener(new BarSelectView.OnBarSelectListener() {
            @Override
            public void onBarSelect(int barSelected) {
                pentatonicEditorSurfaceView.SetSelectedBar(barSelected);
                String[] notes = new String[6];
                for (int i = 0; i < 6; i++) {
                    String note = QSoundPool.getInstance().GetNoteForGuitarString(barSelected,i);
                    notes[i] = note;
                }
                notePanelSurfaceView.DrawNote(notes);
            }
        });
        if(!RockStarApplication.IsBPMPresent()) {
            RockStarApplication.SetBPMPresent();
            SetBPMDialog();
        }
    }


    private void RemoveContextMenu() {
        if (speechBubble != null) {
            speechBubble.removeAllViews();
            speechBubble = null;
            if (playSimpleTab != null) {
                playSimpleTab.Stop();
                playSimpleTab = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.END_TIME = System.currentTimeMillis() - this.START_TIME;
        Log.i("info"," ---- PentatinicEditorActivity.onResume : time elapsed = " + Long.toString(this.END_TIME));
    }

    @Override
    public void onPause() {
        super.onPause();
        StopPlayer();

        if(setBpmDialog != null) {
            setBpmDialog.dismiss();
        }

        QSoundPool.getInstance().setOnSoundPoolSuccessLoaded(null);

        // Cache current work
        if(pentatonicEditorSurfaceView.GetSimpleTabList().size() > 0) {
            String path = FileSystem.GetCachePath() + "/cache_tabs";
            SimpleTab.SaveTabsToXmlFile(path, pentatonicEditorSurfaceView.GetSimpleTabList(),"onquantum");
        }
    }

    private void PlayButtonAction(View v) {
        RemoveContextMenu();
        if (!CheckSoundPool()) {
            return;
        }
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
            StopPlayer();
        }
    }

    private void StopPlayer() {
        if(player != null) {
            player.Stop();
            playTabsButton.setSelected(false);
        }
        player = null;
        pentatonicEditorSurfaceView.Stop();
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
                //Log.i("info"," BAR SELECTED : " + barSelected);
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
                        path = path + "/" + fileName + Constants.TAB_FILE_EXTENSION;
                        if(!SimpleTab.SaveTabsToXmlFile(path,pentatonicEditorSurfaceView.GetSimpleTabList(), "onquantum@gmail.com")) {
                            Toast.makeText(PentatonicEditorActivity.this, "File system error",Toast.LENGTH_SHORT);
                        }
                    } else {
                        Toast.makeText(PentatonicEditorActivity.this, "File system error",Toast.LENGTH_SHORT);
                    }
                    if (openTabsInterface != null) {
                        openTabsInterface.SuccessSaved();
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
                BPM = bpm;
                if(changeBPMProgress != null) {
                    changeBPMProgress.dismiss();
                }
                changeBPMProgress = new ProgressDialog(PentatonicEditorActivity.this);
                changeBPMProgress.setCanceledOnTouchOutside(false);
                changeBPMProgress.setMessage("Set " + bpm + " bets per minute");
                changeBPMProgress.show();

                pentatonicEditorSurfaceView.SetBPM(bpm);
                bpmDialogIsShow = false;
                setBpmDialog = null;
            }
            @Override
            public void OnCancelDialog() {
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
                    FileSystem.ClearCacheFile();
                    pentatonicEditorSurfaceView.LoadTabs(SimpleTab.LoadTabsFromXmlFile(FileSystem.GetTabsFilesPath() + "/" + tabsFileName + Constants.TAB_FILE_EXTENSION));
                }
            }
        }
    }

    private boolean CheckSoundPool() {
        if(!QSoundPool.getInstance().isSuccessLoaded()) {
            loadSoundPackProgress = null;
            loadSoundPackProgress = new ProgressDialog(PentatonicEditorActivity.this);
            loadSoundPackProgress.setMessage("First time loading... 0%");
            loadSoundPackProgress.show();
            loadSoundPackProgress.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    loadSoundPackProgress = null;
                }
            });
            return false;
        } else {
            return true;
        }
    }

    private void SetSoundPoolListener() {
        QSoundPool.getInstance().setOnSoundPoolSuccessLoaded(new QSoundPool.OnSoundPoolSuccessLoaded() {
            @Override
            public void soundPoolSuccessLoaded() {
                if(loadSoundPackProgress != null) {
                    loadSoundPackProgress.dismiss();
                    loadSoundPackProgress = null;
                    QSoundPool.getInstance().setOnSoundPoolSuccessLoaded(null);
                    QSoundPool.getInstance().setOnProgressUpdate(null);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        controllPanel.removeAllViews();
                        if (guitarEntity != null) {
                            fillSoundPackInfoPanel(guitarEntity.name, Integer.toString(BPM) + " bpm");
                        }
                    }
                });
            }
        });
        QSoundPool.getInstance().setOnProgressUpdate(new QSoundPool.OnProgressUpdate() {
            @Override
            public void progressUpdate(int progress) {
                if(loadSoundPackProgress != null) {
                    loadSoundPackProgress.setMessage("First time loading... " + progress + "%");
                }
            }
        });
    }

    // Info panel
    private void fillSoundPackInfoPanel(String soundPackName, String bpm) {
        if (controllPanel != null) {
            controllPanel.removeAllViews();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            View v = PentatonicEditorActivity.this.getLayoutInflater().inflate(R.layout.soun_pack_tempo_info_panel, null);
            if (v == null)
                return;
            TextView soundPackLabel =  (TextView) v.findViewById(R.id.textView12);
            TextView bpmLabel = (TextView)v.findViewById(R.id.textView13);
            if (soundPackLabel != null) {
                soundPackLabel.setText(soundPackName);
                soundPackLabel.setTypeface(typeFaceCapture);
            }
            if (bpmLabel != null) {
                bpmLabel.setText(bpm);
            }

            controllPanel.addView(v);
        }
    }
}