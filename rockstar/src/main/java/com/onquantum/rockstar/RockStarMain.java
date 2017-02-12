package com.onquantum.rockstar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;
import com.onquantum.rockstar.activities.BaseActivity;
import com.onquantum.rockstar.activities.SoundPacksListActivity;
import com.onquantum.rockstar.activities.PentatonicEditorActivity;
import com.onquantum.rockstar.common.Constants;
import com.onquantum.rockstar.file_system.FileSystem;
import com.onquantum.rockstar.activities.GuitarSimulatorActivity;
import com.onquantum.rockstar.gsqlite.DBPurchaseTable;
import com.onquantum.rockstar.gsqlite.PurchaseEntity;
import com.onquantum.rockstar.services.RegistrationIntentService;
import com.onquantum.rockstar.services.UpdatePurchaseTable;
import com.onquantum.rockstar.util.IabHelper;
import com.onquantum.rockstar.util.IabResult;
import com.onquantum.rockstar.util.Inventory;
import com.onquantum.rockstar.util.SkuDetails;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

import com.google.android.gms.common.ConnectionResult;

import org.json.JSONException;
import org.json.JSONObject;

public class RockStarMain extends BaseActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "iuMWhsSjgfenU4oaarZClgdfg";
    private static final String TWITTER_SECRET = "wLItp8AfJc5KklKzFXSFvJ80IujxvpBMjD2mg0CZdTPuJzL3dA";

    // Google+
    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient googleApiClient;
    private boolean mIntentInProgress;
    private ConnectionResult mConnectionResult;
    private boolean mSignInClicked;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    // Facebook
    private UiLifecycleHelper uiHelper;

    private Context context;
    private Button selectStyle;


    // Billing
    private IabHelper iabHelper;
    private IabHelper.QueryInventoryFinishedListener queryInventoryFinishedListener;
    private List<String> additionalSkuList;

    // For test only
    private boolean enableBilling = true;


    BroadcastReceiver broadcastReceiver;
    private boolean receiverIsRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = this;

        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);

        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                mSignInClicked = false;
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

        ImageButton playButton = (ImageButton)findViewById(R.id.play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(context, GuitarSimulatorActivity.class);
                startActivity(intent);
            }
        });

        final Button faceBookButton = (Button)findViewById(R.id.faceBookButton);
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


        (findViewById(R.id.button4)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SoundPacksListActivity.class));
            }
        });


        (findViewById(R.id.button5)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileSystem.ClearCacheFile();
                startActivity(new Intent(RockStarMain.this, PentatonicEditorActivity.class));
            }
        });

        Typeface titleFont = Typeface.createFromAsset(getAssets(),"font/BaroqueScript.ttf");
        ((TextView)this.findViewById(R.id.textView0)).setTypeface(titleFont);



        // GCM
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);

        /*******************************************************************************************/
        // Billing
        /*******************************************************************************************/
        if (this.enableBilling) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if(intent.getAction().equals(UpdatePurchaseTable.BROADCAST_COMPLETE_UPDATE_PURCHASE)) {
                        additionalSkuList = new ArrayList<>();
                        iabHelper = new IabHelper(RockStarMain.this, Constants.LICENSE_KEY);
                        iabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                            public void onIabSetupFinished(IabResult result) {
                                if (!result.isSuccess()) {
                                    Log.d("info", "RockStarMain :  Problem setting up In-app Billing: " + result);
                                    return;
                                }
                                Log.d("info", "RockStarMain :  Success setting up In-app Billing: " + result);
                                List<PurchaseEntity>purchaseEntityList = DBPurchaseTable.GetAllPurchaseEntity(RockStarMain.this);
                                for (PurchaseEntity purchaseEntity : purchaseEntityList) {
                                    additionalSkuList.add(purchaseEntity.bundle);
                                }
                                iabHelper.queryInventoryAsync(true, additionalSkuList, queryInventoryFinishedListener);
                            }
                        });

                        queryInventoryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
                            @Override
                            public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                                if (result.isFailure()) {
                                    return;
                                }
                                for (String productBundle : additionalSkuList) {
                                    SkuDetails skuDetails = inv.getSkuDetails(productBundle);
                                    if(skuDetails != null) {
                                        Log.i("info"," SKU DETAIL : is_purchased " + inv.hasPurchase(productBundle) + " : " + skuDetails.toString());
                                        PurchaseEntity purchaseEntity = DBPurchaseTable.GetPurchaseEntityByBundle(RockStarMain.this, productBundle);
                                        if(purchaseEntity != null) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(skuDetails.getJsonObject());
                                                purchaseEntity.has_purchased = inv.hasPurchase(productBundle);
                                                purchaseEntity.currency_code = jsonObject.getString("price_currency_code");
                                                float price = Float.parseFloat(jsonObject.getString("price_amount_micros")) / 1000000;
                                                purchaseEntity.price = Float.toString(price);
                                                DBPurchaseTable.AddPurchaseEntity(RockStarMain.this, purchaseEntity);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } else {
                                        Log.i("info"," SKU DETAIL : NULL");
                                    }
                                }
                            }
                        };
                    }
                    unregisterReceiver(broadcastReceiver);
                    broadcastReceiver = null;
                    receiverIsRegistered = false;
                }
            };

            IntentFilter intentFilter = new IntentFilter(UpdatePurchaseTable.BROADCAST_COMPLETE_UPDATE_PURCHASE);
            registerReceiver(broadcastReceiver, intentFilter);
        }
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        if(broadcastReceiver != null) {
            unregisterReceiver(this.broadcastReceiver);
            this.broadcastReceiver = null;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
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

        if (iabHelper != null) iabHelper.dispose();
        iabHelper = null;

        if(receiverIsRegistered && broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
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
}
