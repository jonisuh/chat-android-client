package com.example.joni.basicchatapp.xmlparsers;

import android.util.Xml;

import com.example.joni.basicchatapp.xmlentities.*;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Joni on 28.4.2016.
 */
public class UserParser {
    private static final String ns = null;

    public ArrayList<User> parse(InputStream in) throws XmlPullParserException, IOException {
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

    private ArrayList<User> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<User> users = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "users");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("user")) {
                users.add(readUser(parser));
            }else {
                skip(parser);
            }
        }
        return users;
    }
    private User readUser(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "user");
        int id = 0;
        String username = null;
        String fname = null;
        String lname = null;
        String title = null;
        String department = null;
        String email = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("userID")) {
                id = readId(parser);
            } else if (name.equals("username")) {
                username = readUserName(parser);
            }
            else if (name.equals("firstname")) {
                fname = readFirstname(parser);
            }
            else if (name.equals("lastname")) {
                lname = readLastname(parser);
            }
            else if (name.equals("title")) {
                title = readTitle(parser);
            }
            else if (name.equals("department")) {
                department = readDepartment(parser);
            }
            else if (name.equals("email")) {
                email = readEmail(parser);
            }else {
                skip(parser);
            }
        }
        return new User(id, username,fname,lname,email,title,department);
    }

    // Processes ID tags in the feed.
    private int readId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "userID");
        int id = Integer.parseInt(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "userID");
        return id;
    }
    // Processes name tags in the feed.
    private String readUserName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "username");
        String username = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "username");
        return username;
    }
    private String readLastname(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "lastname");
        String lastname = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "lastname");
        return lastname;
    }
    private String readDepartment(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "department");
        String department = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "department");
        return department;
    }
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }
    private String readEmail(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "email");
        String email = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "email");
        return email;
    }
    private String readFirstname(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "firstname");
        String firstname = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "firstname");
        return firstname;
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
