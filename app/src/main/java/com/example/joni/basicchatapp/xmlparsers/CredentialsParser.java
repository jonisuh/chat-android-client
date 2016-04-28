package com.example.joni.basicchatapp.xmlparsers;

import android.util.Xml;

import com.example.joni.basicchatapp.xmlentities.LoginCredentials;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Joni on 27.4.2016.
 */
public class CredentialsParser {
    private static final String ns = null;

    public LoginCredentials parse(InputStream in) throws XmlPullParserException, IOException {
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

    private LoginCredentials readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        LoginCredentials credentials = new LoginCredentials();

        parser.require(XmlPullParser.START_TAG, ns, "returninformation");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("userID")) {
                credentials.setId(readId(parser));
            } else if(name.equals("authCred")){
                credentials.setAuthcredentials(readCredentials(parser));
            }else {
                skip(parser);
            }
        }
        return credentials;
    }


    // Processes ID tags in the feed.
    private int readId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "userID");
        int id = Integer.parseInt(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "userID");
        return id;
    }



    // Processes name tags in the feed.
    private String readCredentials(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "authCred");
        String cred = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "authCred");
        return cred;
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
