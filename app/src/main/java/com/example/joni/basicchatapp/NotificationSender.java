package com.example.joni.basicchatapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import java.util.HashMap;

/**
 * Created by Joni on 2.5.2016.
 */
public class NotificationSender {
    private HashMap<Integer, Integer> notificationCountMap;

    private NotificationSender(){
        notificationCountMap = new HashMap();
    }
    //Singleton

    public static NotificationSender getInstance() {
        return NotificationSingleton.INSTANCE;
    }

    private static class NotificationSingleton {
        private static final NotificationSender INSTANCE = new NotificationSender();
    }

    public void sendNotification(int groupID,String groupname,Context context){
        Intent notificationIntent = new Intent(context, ChatScreenActivity.class);
        notificationIntent.putExtra("groupID", groupID);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, groupID, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        int notificationNumber;
        if(notificationCountMap.containsKey(groupID)){
            notificationNumber = notificationCountMap.get(groupID);
        }else{
            notificationNumber = 1;
        }
        notificationCountMap.put(groupID,notificationNumber+1);

        String notificationsInGroup = "";
        String newMessagesCount = "";
        if(notificationNumber == 1){
            notificationsInGroup = "New message in group "+groupname;
            newMessagesCount = "One new message";
        }else{
            notificationsInGroup = "New messages in group "+groupname;
            newMessagesCount = notificationNumber+" new messages";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setContentTitle("Basic Chat App").setContentText(notificationsInGroup).setSmallIcon(R.drawable.notification_icon).setContentIntent(pendingIntent);
        builder.setSubText(newMessagesCount);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setNumber(notificationNumber);
        Notification notification = builder.build();


        //notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(groupID, notification);
    }
    public void cancelNotification(int groupID, Context context){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(groupID);
        notificationCountMap.remove(groupID);
    }
    /*
    AsyncTask.execute(new Runnable() {
        @Override
        public void run() {
            //TODO your background code
        }
    });
    private void sendNotification(int id){
        Intent notificationIntent = new Intent(this, ChatScreenActivity.class);
        notificationIntent.putExtra("groupID", id);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        String[] PROJECTION = new String[]{GroupsTable.COLUMN_ID,GroupsTable.COLUMN_NAME};
        String SELECTION = GroupsTable.COLUMN_ID+"="+id;
        Cursor c = getContentResolver().query(ChatProvider.GROUPS_CONTENT_URI, PROJECTION, SELECTION, null, null);

        String messagesInGroup = "";
        if(c != null && c.getCount() > 0){
            c.moveToNext();
            messagesInGroup = "in group "+c.getString(1);
            c.close();
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setContentTitle("Basic Chat App").setContentText("New messages "+messagesInGroup).setSmallIcon(R.drawable.notification_icon).setContentIntent(pendingIntent);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        Notification notification = builder.build();

        //notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(id, notification);
    }
    */
}
