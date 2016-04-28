package com.example.joni.basicchatapp.xmlparsers;

import android.util.Xml;

import com.example.joni.basicchatapp.xmlentities.LoginCredentials;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import com.example.joni.basicchatapp.xmlentities.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Joni on 28.4.2016.
 */
public class GroupParser {
    private static final String ns = null;

    public ArrayList<Group> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private ArrayList<Group> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Group> groups = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "groups");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("group")) {
                groups.add(readGroup(parser));
            } else {
                skip(parser);
            }
        }
        return groups;
    }

    private Group readGroup(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "group");
        int id = 0;
        String groupname = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("groupID")) {
                id = readId(parser);
            } else if (name.equals("groupName")) {
                groupname = readGroupName(parser);
            } else {
                skip(parser);
            }
        }
        return new Group(id, groupname);
    }

    // Processes ID tags in the feed.
    private int readId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "groupID");
        int id = Integer.parseInt(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "groupID");
        return id;
    }



    // Processes name tags in the feed.
    private String readGroupName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "groupName");
        String groupname = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "groupName");
        return groupname;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
