package com.onquantum.rockstar.tabulature;

import android.util.Log;
import android.util.Xml;

import com.onquantum.rockstar.common.XmlHelper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 12/25/15.
 */
public class SimpleTab {
    private int guitarString = 1;
    private int guitarBar = 1;
    private long startQuartet = 0;
    private long Duration = 0;

    public SimpleTab(){}

    public SimpleTab(int guitarString, int guitarBar, long startQuartet, long duration) {
        this.guitarString = guitarString;
        this.guitarBar = guitarBar;
        this.startQuartet = startQuartet;
        this.Duration = duration;
    }

    public int getGuitarString() { return guitarString; }
    public int getGuitarBar() { return guitarBar; }

    public long getStartQuartetMS(int BPM) {
        if(startQuartet > 0)
            return (long)(60f / BPM * 1000L * startQuartet);
        return 0;
    }
    public long getStartQuartet() { return startQuartet; }
    public long getDuration() { return Duration; }
    public long getDurationMS(int BPM) {
        return (long)(60f / BPM * 1000L * 4) / Duration;
    }

    public void setGuitarString(int guitarString) { this.guitarString = guitarString; }
    public void setGuitarBar(int guitarBar) { this.guitarBar = guitarBar; }
    public void setStartQuartet(long startQuartet) { this.startQuartet = startQuartet; }
    public void setDuration(long duration) { this.Duration = duration; }
    @Override
    public String toString() {
        return "SimpleTab : guitarString : " + guitarString + ", guitarBar : " + guitarBar + ", startQuartet : " + startQuartet + ", Duration : " + Duration;
    }


    // Helper methods
    public static boolean SaveTabsToXmlFile(String filePath, List<SimpleTab>tabsList) {
        boolean result = false;
        File file = new File(filePath);
        FileOutputStream fileOutputStream = null;
        try {

            fileOutputStream = new FileOutputStream(file);
            XmlSerializer xmlSerializer = Xml.newSerializer();
            xmlSerializer.setOutput(fileOutputStream,"UTF-8");
            xmlSerializer.startDocument(null,Boolean.valueOf(true));
            xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            xmlSerializer.startTag(null,"pentatonic");
            for (int i = 0; i < tabsList.size(); i++) {
                SimpleTab simpleTab = tabsList.get(i);

                xmlSerializer.startTag(null,"tab");

                xmlSerializer.startTag(null,"string");
                xmlSerializer.text(Integer.toString(simpleTab.getGuitarString()));
                xmlSerializer.endTag(null,"string");

                xmlSerializer.startTag(null,"bar");
                xmlSerializer.text(Integer.toString(simpleTab.getGuitarBar()));
                xmlSerializer.endTag(null,"bar");

                xmlSerializer.startTag(null,"start");
                xmlSerializer.text(Long.toString(simpleTab.getStartQuartet()));
                xmlSerializer.endTag(null,"start");

                xmlSerializer.startTag(null,"duration");
                xmlSerializer.text(Long.toString(simpleTab.getDuration()));
                xmlSerializer.endTag(null,"duration");

                xmlSerializer.endTag(null,"tab");
            }
            xmlSerializer.endTag(null,"pentatonic");
            xmlSerializer.endDocument();
            xmlSerializer.flush();

            result = true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            result = false;
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        } finally {
            if(fileOutputStream != null)
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return result;
    }

    public static List<SimpleTab> LoadTabsFromXmlFile(String path) {
        List<SimpleTab>simpleTabsList = new ArrayList<>();

        File file = new File(path);
        if(file.exists() == false)
            return null;
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        XmlPullParser xmlPullParser = Xml.newPullParser();
        try {
            xmlPullParser.setInput(inputStream, "UTF-8");
            xmlPullParser.nextTag();
            xmlPullParser.require(XmlPullParser.START_TAG, null, "pentatonic");
            while (xmlPullParser.next() != XmlPullParser.END_TAG) {
                if(xmlPullParser.getEventType() != XmlPullParser.START_TAG)
                    continue;
                String tab = xmlPullParser.getName();
                if(tab.equals("tab")) {
                    SimpleTab simpleTab = new SimpleTab();
                    xmlPullParser.require(XmlPullParser.START_TAG, null, "tab");
                    while (xmlPullParser.next() != XmlPullParser.END_TAG) {
                        if(xmlPullParser.getEventType() != XmlPullParser.START_TAG)
                            continue;
                        String value = xmlPullParser.getName();
                        if(value.equals("string")) {
                            if(xmlPullParser.next() == XmlPullParser.TEXT) {
                                simpleTab.setGuitarString(Integer.parseInt(xmlPullParser.getText()));
                                xmlPullParser.nextTag();
                            }
                        } else if(value.equals("bar")) {
                            if(xmlPullParser.next() == XmlPullParser.TEXT) {
                                simpleTab.setGuitarBar(Integer.parseInt(xmlPullParser.getText()));
                                xmlPullParser.nextTag();
                            }
                        } else if(value.equals("start")) {
                            if(xmlPullParser.next() == XmlPullParser.TEXT) {
                                simpleTab.setStartQuartet(Long.parseLong(xmlPullParser.getText()));
                                xmlPullParser.nextTag();
                            }
                        } else if(value.equals("duration")) {
                            if(xmlPullParser.next() == XmlPullParser.TEXT) {
                                simpleTab.setDuration(Long.parseLong(xmlPullParser.getText()));
                                xmlPullParser.nextTag();
                            }
                        } else {
                            XmlHelper.skip(xmlPullParser);
                        }
                    }
                    Log.i("info",simpleTab.toString());
                    simpleTabsList.add(simpleTab);
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return simpleTabsList;
    }
}
