package com.onquantum.rockstar.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.onquantum.rockstar.file_system.FileSystem;
import com.onquantum.rockstar.tabulature.SimpleTab;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Admin on 1/7/16.
 */
public class HandleIntentOpenTab extends Activity {
    private String fileName = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<SimpleTab>tabs = OpenTabsFromIntent(getIntent());
        if(tabs != null && tabs.size() > 0) {
            String path = FileSystem.GetCachePath() + "/cache_tabs";
            SimpleTab.SaveTabsToXmlFile(path, tabs,"onquantum");
        }
        Intent intent = new Intent(this, PentatonicEditorActivity.class);
        intent.putExtra("fileName", fileName.subSequence(0, fileName.lastIndexOf(".")));
        startActivity(intent);
        finish();
    }

    private List<SimpleTab> OpenTabsFromIntent(Intent intent) {
        Uri uri = intent.getData();

        List<SimpleTab>simpleTab = null;
        if(uri != null) {
            String scheme = uri.getScheme();
            if(ContentResolver.SCHEME_CONTENT.equals(scheme) || ContentResolver.SCHEME_FILE.equals(scheme)) {
                if (ContentResolver.SCHEME_FILE.equals(scheme)) {
                    fileName = uri.getLastPathSegment();
                }
                ContentResolver contentResolver = this.getContentResolver();
                InputStream inputStream = null;
                try {
                    inputStream = contentResolver.openInputStream(uri);
                    simpleTab = SimpleTab.LoadTabFromStream(inputStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return simpleTab;
    }
}
