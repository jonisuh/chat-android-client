package com.example.joni.basicchatapp.services;

/**
 * Created by Joni on 1.5.2016.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.joni.basicchatapp.ChatProvider;
import com.example.joni.basicchatapp.ChatScreenActivity;
import com.example.joni.basicchatapp.GroupsTable;
import com.example.joni.basicchatapp.MenuActivity;
import com.example.joni.basicchatapp.R;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class ChatWebSocketService extends Service {
    private final IBinder mBinder = new ChatBinder();
    private boolean mAllowRebind = true;
    private WebSocketConnection mConnection;


    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("WSService", "service started");
        if (intent != null) {
            boolean reconnect = intent.getBooleanExtra("reconnect",false);
            Log.d("WSService", "1"+(mConnection == null));
            if(mConnection != null) {
                Log.d("WSService", "2" + mConnection.isConnected());
            }

            if (mConnection == null || mConnection.isConnected() == false || reconnect) {
                mConnection = new WebSocketConnection();

                final String wsuri = MenuActivity.WS_URI;
                try {
                    mConnection.connect(wsuri, new WebSocketHandler() {
                        @Override
                        public void onOpen() {
                            Log.d("WSService", "Status: Connected to " + wsuri);
                        }

                        @Override
                        public void onTextMessage(String payload) {
                            Log.d("WSService","New message in group "+payload);
                            String[] PROJECTION = new String[]{GroupsTable.COLUMN_ID};
                            String SELECTION = GroupsTable.COLUMN_ID+"="+payload;
                            Cursor c = getContentResolver().query(ChatProvider.GROUPS_CONTENT_URI, PROJECTION, SELECTION, null, null);

                            if(c != null && c.getCount() > 0){
                                Log.d("WSService","Message in my group");

                                int groupID = Integer.parseInt(payload);

                                Intent loadmessagesintent = new Intent(ChatWebSocketService.this, LoadGroupMessagesService.class);
                                loadmessagesintent.putExtra("groupID",groupID);
                                startService(loadmessagesintent);

                                if(!ChatScreenActivity.isVisible() || ChatScreenActivity.getGroupID() != groupID){
                                    sendNotification(groupID);
                                }
                            }

                        }

                        @Override
                        public void onClose(int code, String reason) {
                            Log.d("WSService", "Connection lost"+" "+code+" "+reason);
                        }

                    });
                } catch (WebSocketException e) {

                    Log.d("WSService", e.toString());
                }
            }


        }

        return START_STICKY;
    }

    public void sendMessage(String message){
        Log.d("WSService", "msg "+message);
        if(mConnection != null && mConnection.isConnected()) {
            mConnection.sendTextMessage(message);
            Log.d("WSService", "msg sent");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("WSService", "Service bound");
        return mBinder;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("WSService", "Service unbound");
        return mAllowRebind;
    }


    @Override
    public void onRebind(Intent intent) {
        Log.d("WSService", "Service rebound");
    }

    @Override
    public void onDestroy() {
        Log.d("WSService", "Service shutdown");
    }

    private void sendNotification(int id){
        Intent notificationIntent = new Intent(this, ChatScreenActivity.class);
        notificationIntent.putExtra("groupID",id);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setContentTitle("Basic Chat App").setContentText("New message in group "+id).setSmallIcon(R.drawable.notification_icon).setContentIntent(pendingIntent);
        Notification notification = builder.build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    public class ChatBinder extends Binder {
        public ChatWebSocketService getService() {
            // Return this instance of ChatWebSocketService so clients can call public methods
            return ChatWebSocketService.this;
        }
    }
}
