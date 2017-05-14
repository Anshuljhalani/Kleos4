package org.technocracy.app.kleos.fcm;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import org.technocracy.app.kleos.activity.HomeActivity;

/**
 * Created by MOHIT on 15-Sep-16.
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = FirebaseMessagingService.class.getSimpleName();
    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Notification notification = new Notification(remoteMessage.getData().get("title"),
                remoteMessage.getData().get("message"), remoteMessage.getData().get("image"),
                remoteMessage.getData().get("created_at"));

        String title = notification.getTitle();
        String message = notification.getMessage();
        String image = notification.getImageUrl();
        String timestamp = notification.getCreatedAt();

        Log.e(TAG, "From: " + remoteMessage.getFrom());
        Log.e(TAG, "Title: " + title);
        Log.e(TAG, "message: " + message);
        Log.e(TAG, "image: " + image);
        Log.e(TAG, "timestamp: " + timestamp);

        Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
        resultIntent.putExtra("message", message);

        if (TextUtils.isEmpty(image)) {
            showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
        } else {
            showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, image);
        }
    }

    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

}
