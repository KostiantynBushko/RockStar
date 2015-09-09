package com.onquantum.rockstar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;
import com.onquantum.rockstar.activities.AboutActivity;
import com.onquantum.rockstar.activities.SoundPacksListActivity;
import com.onquantum.rockstar.pentatonic_editor.PentatonicEditorActivity;
import com.onquantum.rockstar.sequencer.QSoundPool;
import com.onquantum.rockstar.activities.GuitarSimulatorActivity;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.net.MalformedURLException;
import java.net.URL;

import io.fabric.sdk.android.Fabric;

import com.google.android.gms.common.ConnectionResult;

public class RockStarMain extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "iuMWhsSjgfenU4oaarZClgdfg";
    private static final String TWITTER_SECRET = "wLItp8AfJc5KklKzFXSFvJ80IujxvpBMjD2mg0CZdTPuJzL3dA";

    // Google+
    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient googleApiClient;
    private boolean mIntentInProgress;
    private ConnectionResult mConnectionResult;
    private boolean mSignInClicked;

    // Facebook
    private UiLifecycleHelper uiHelper;

    private Context context;
    private Button selectStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setContentView(R.layout.main);
        setContentView(R.layout.main);
        context = this;

        // Social initialisation
        //TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        //Fabric.with(this, new Twitter(authConfig));
        //Fabric.with(this, new TweetComposer());


        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);

        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                mSignInClicked = false;
                //Toast.makeText(context, "User is connected!", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onConnectionSuspended(int i) {
                googleApiClient.connect();
            }
        }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                if (!mIntentInProgress) {
                    // Store the ConnectionResult so that we can use it later when the user clicks
                    // 'sign-in'.
                    mConnectionResult = connectionResult;

                    if (mSignInClicked) {
                        // The user has already clicked 'sign-in' so we attempt to resolve all
                        // errors until the user is signed in, or they cancel.
                        resolveSignInError();
                    }
                }
            }
        }).addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN).build();

        Button playButton = (Button)findViewById(R.id.play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(context, GuitarSimulatorActivity.class);
                startActivity(intent);
            }
        });

        //Facebook facebook = new Facebook(getResources().getString(R.string.facebook_app_id));

        Button faceBookButton = (Button)findViewById(R.id.faceBookButton);
        faceBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("info"," Click facebook");
                FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(RockStarMain.this)
                        .setLink("https://play.google.com/store/apps/details?id=" + context.getPackageName())
                        .setPicture("http://rockstar-onquantum.rhcloud.com/files/images/rock_star_banner_web.png")
                        .setApplicationName(getResources().getString(R.string.app_name))
                        .build();
                uiHelper.trackPendingDialogCall(shareDialog.present());
                /*Session.openActiveSession((Activity) context, true, new Session.StatusCallback() {
                    @Override
                    public void call(Session session, SessionState sessionState, Exception e) {
                        if (session.isOpened()) {
                            Request.newMeRequest(session, new Request.GraphUserCallback() {
                                @Override
                                public void onCompleted(GraphUser graphUser, Response response) {
                                    if (graphUser != null) {
                                        FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(RockStarMain.this)
                                                .setLink("https://play.google.com/store/apps/details?id=" + context.getPackageName())
                                                .setPicture("http://rockstar-onquantum.rhcloud.com/files/images/rock_star_banner_web.png")
                                                .build();
                                        uiHelper.trackPendingDialogCall(shareDialog.present());
                                    }
                                }
                            }).executeAsync();
                        }
                    }
                });*/
            }
        });

        Button twitterButton = (Button)findViewById(R.id.twitterButton);
        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
                Fabric.with(context, new Twitter(authConfig));
                Fabric.with(context, new TweetComposer());

                String url = "https://play.google.com/store/apps/details?id=" + context.getPackageName();
                String imageUri = "android.resource://" + context.getPackageName() + "/" + R.drawable.rock_star_baner_1;

                TweetComposer.Builder builder = null;
                try {
                    builder = new TweetComposer.Builder(RockStarMain.this)
                            .text("Rock Star Guitar")
                            .url(new URL(url))
                            .image(Uri.parse(imageUri));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                builder.show();
            }
        });

        Button googlePlus = (Button)findViewById(R.id.googlePlus);
        googlePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!googleApiClient.isConnected()) {
                    mSignInClicked = true;
                    resolveSignInError();
                } else {
                    Intent shareIntent = new PlusShare.Builder(context)
                            .setType("text/plain")
                            .setText("Try \"Rock Star\" application")
                            .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName()))
                            .getIntent();
                    startActivityForResult(shareIntent, 0);
                }
            }
        });

        ((Button)findViewById(R.id.buttonInfo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AboutActivity.class);
                startActivity(intent);
            }
        });

        ((Button)findViewById(R.id.button4)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SoundPacksListActivity.class));
                //startService(new Intent(context, UpdateGuitarsService.class));
            }
        });


        ((Button)findViewById(R.id.button5)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RockStarMain.this, PentatonicEditorActivity.class));
            }
        });

        Typeface titleFont = Typeface.createFromAsset(getAssets(),"font/BaroqueScript.ttf");
        ((TextView)this.findViewById(R.id.textView0)).setTypeface(titleFont);

        QSoundPool.getInstance().setContext(getApplicationContext());
        QSoundPool.getInstance().loadSound();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.i("info", String.format(" RockStarMain Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("info", " RockStarMain Success! Facebook");
            }
        });


        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }
            mIntentInProgress = false;
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();

        //startService(new Intent(context, UpdateGuitarsService.class));
        //startService(new Intent(context, UpdatePurchaseTable.class));

    }
    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
    }


    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                googleApiClient.connect();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }
}
