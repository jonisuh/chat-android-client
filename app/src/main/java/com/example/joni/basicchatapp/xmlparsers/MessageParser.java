package com.example.joni.basicchatapp.xmlparsers;

import android.util.Xml;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import com.example.joni.basicchatapp.xmlentities.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Joni on 28.4.2016.
 */
public class MessageParser {
    private static final String ns = null;

    public ArrayList<Message> parse(InputStream in) throws XmlPullParserException, IOException {
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

    private ArrayList<Message>  readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Message> messages = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "messageXMLs");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("messageroot")) {
                messages.add(readMessage(parser));
            } else {
                skip(parser);
            }
        }
        return messages;
    }

    private Message readMessage(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "messageroot");
        int messageID = 0;
        int userID = 0;
        int groupID = 0;
        String message = null;
        String timestamp = null;
        String username = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("messageID")) {
                messageID = readMessageID(parser);
            } else if (name.equals("userID")) {
                userID = readUserID(parser);
            } else if (name.equals("groupID")) {
                groupID = readGroupID(parser);
            } else if (name.equals("message")) {
                message = readMessageContent(parser);
            } else if (name.equals("timestamp")) {
                timestamp = readTimestamp(parser);
            } else if (name.equals("username")) {
                username = readUsername(parser);
            }else {
                skip(parser);
            }
        }
        return new Message(userID,groupID,messageID,username,message,timestamp);
    }

    // Processes ID tags in the feed.
    private int readMessageID(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "messageID");
        int messageID = Integer.parseInt(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "messageID");
        return messageID;
    }
    private int readUserID(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "userID");
        int userID = Integer.parseInt(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "userID");
        return userID;
    }
    private int readGroupID(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "groupID");
        int groupID = Integer.parseInt(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "groupID");
        return groupID;
    }

    private String readMessageContent(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "message");
        String message = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "message");
        return message;
    }
    private String readTimestamp(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "timestamp");
        String timestamp = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "timestamp");
        return timestamp;
    }
    private String readUsername(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "username");
        String username = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "username");
        return username;
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
