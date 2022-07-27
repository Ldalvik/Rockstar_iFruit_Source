package com.rockstargames.home.listwidget;

import android.content.Context;
import android.net.Uri;
import android.util.Xml;
import com.amazon.device.home.GroupedListHeroWidget;
import com.amazon.device.home.HeroWidgetIntent;
import com.amazon.device.home.HomeManager;
import com.rockstargames.hal.ActivityWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.http.HttpHost;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
public class HomeWidgetCreator {
    private Context mAppContext;
    private HomeManager mHomeManager;
    private GroupedListHeroWidget mWidget = null;

    public HomeWidgetCreator(Context context) throws ClassNotFoundException {
        this.mAppContext = null;
        this.mHomeManager = null;
        Class.forName("com.amazon.device.home.HomeManager");
        this.mAppContext = context;
        this.mHomeManager = HomeManager.getInstance(context);
    }

    public void updateHomeWidgetWithData(String str) throws XmlPullParserException, IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(str.getBytes());
        XmlPullParser newPullParser = Xml.newPullParser();
        newPullParser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", false);
        newPullParser.setInput(byteArrayInputStream, null);
        ArrayList arrayList = new ArrayList();
        for (int eventType = newPullParser.getEventType(); eventType != 1; eventType = newPullParser.next()) {
            String name = newPullParser.getName();
            if (eventType == 2) {
                if (name.equalsIgnoreCase("homeWidget")) {
                    newPullParser.require(2, null, "homeWidget");
                }
                if (name.equalsIgnoreCase("group")) {
                    arrayList.add(parseGroup(newPullParser));
                }
            }
        }
        GroupedListHeroWidget groupedListHeroWidget = new GroupedListHeroWidget();
        this.mWidget = groupedListHeroWidget;
        groupedListHeroWidget.setGroups(arrayList);
        this.mHomeManager.updateWidget(this.mWidget);
    }

    private GroupedListHeroWidget.Group parseGroup(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        xmlPullParser.require(2, null, "group");
        GroupedListHeroWidget.Group group = new GroupedListHeroWidget.Group();
        ArrayList arrayList = new ArrayList();
        group.setGroupName(xmlPullParser.getAttributeValue(null, "name"));
        int eventType = xmlPullParser.getEventType();
        while (eventType != 3) {
            String name = xmlPullParser.getName();
            if (eventType == 2 && name.equalsIgnoreCase("entry")) {
                arrayList.add(parseEntry(xmlPullParser));
            }
            eventType = xmlPullParser.next();
        }
        group.setListEntries(arrayList);
        return group;
    }

    private GroupedListHeroWidget.ListEntry parseEntry(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        xmlPullParser.require(2, null, "entry");
        String str = "";
        String str2 = str;
        String str3 = str2;
        String str4 = str3;
        while (xmlPullParser.next() != 3) {
            if (xmlPullParser.getEventType() == 2) {
                String name = xmlPullParser.getName();
                if (name.equals("iconUri")) {
                    str4 = readIconUri(xmlPullParser);
                } else if (name.equals("primaryText")) {
                    str = readText(xmlPullParser);
                } else if (name.equals("subtitleText")) {
                    str2 = readText(xmlPullParser);
                } else if (name.equals("link")) {
                    str3 = readLink(xmlPullParser);
                } else {
                    skip(xmlPullParser);
                }
            }
        }
        return createHomeEntry(str, str2, str3, str4);
    }

    private String readIconUri(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        xmlPullParser.require(2, null, "iconUri");
        String readText = readText(xmlPullParser);
        xmlPullParser.require(3, null, "iconUri");
        return readText;
    }

    private String readLink(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        String str;
        xmlPullParser.require(2, null, "link");
        String attributeValue = xmlPullParser.getAttributeValue(null, "type");
        String attributeValue2 = xmlPullParser.getAttributeValue(null, "alt");
        String attributeValue3 = xmlPullParser.getAttributeValue(null, "value");
        if (attributeValue.equalsIgnoreCase("app")) {
            str = attributeValue + "|" + attributeValue2 + "|" + attributeValue3;
        } else {
            str = attributeValue + "|" + attributeValue3;
        }
        xmlPullParser.nextTag();
        xmlPullParser.require(3, null, "link");
        return str;
    }

    private String readText(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        if (xmlPullParser.next() == 4) {
            String text = xmlPullParser.getText();
            xmlPullParser.nextTag();
            return text;
        }
        return "";
    }

    private void skip(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        if (xmlPullParser.getEventType() == 2) {
            int i = 1;
            while (i != 0) {
                int next = xmlPullParser.next();
                if (next == 2) {
                    i++;
                } else if (next == 3) {
                    i--;
                }
            }
            return;
        }
        throw new IllegalStateException();
    }

    private GroupedListHeroWidget.ListEntry createHomeEntry(String str, String str2, String str3, String str4) {
        HeroWidgetIntent heroWidgetIntent = new HeroWidgetIntent(WidgetBroadcastReceiver.class.getName());
        heroWidgetIntent.setData(str3);
        GroupedListHeroWidget.ListEntry primaryText = new GroupedListHeroWidget.ListEntry(ActivityWrapper.getActivity()).setContentIntent(heroWidgetIntent).setVisualStyle(GroupedListHeroWidget.VisualStyle.DEFAULT).setPrimaryText(str);
        if (str4.startsWith(HttpHost.DEFAULT_SCHEME_NAME)) {
            primaryText.setPrimaryIcon(Uri.parse(str4));
        } else {
            primaryText.setPrimaryIcon(this.mAppContext.getResources().getIdentifier(str4, "drawable", this.mAppContext.getPackageName()));
        }
        if (str2 != null && str2.length() > 0) {
            primaryText.setSecondaryText(str2);
        }
        return primaryText;
    }
}
