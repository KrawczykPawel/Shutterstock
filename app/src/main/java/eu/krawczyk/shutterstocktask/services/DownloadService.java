/*
* Copyright (C) 2014 Paweł Krawczyk
*
* Licensed under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License. You may obtain a copy of
* the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations under
* the License.
*/

package eu.krawczyk.shutterstocktask.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import eu.krawczyk.shutterstocktask.R;
import eu.krawczyk.shutterstocktask.ShutterstockTask;
import eu.krawczyk.shutterstocktask.helpers.Constants;
import eu.krawczyk.shutterstocktask.model.enums.EDownloadStatus;

/**
 * @author Paweł Krawczyk
 * @class The DownloadService Class.
 * <p/>
 * @since 31 oct, 2014 IntentService for downloading images.
 */
public class DownloadService extends IntentService {

    // Notification variables
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private int mNotificationId;
    // Constants
    private static final int BUFFER_SIZE = 4096;
    private static final int MAX_PROGRESS = 100;
    public static final String URL = "urlpath";
    public static final String NAME = "name";
    public static final String FILEPATH = "filepath";
    public static final String RESULT = "result";
    public static final String NOTIFICATION_ID = "notificationId";
    public static final String NOTIFICATION = "eu.krawczyk.shutterstocktask.service.receiver";
    public static final String ACTION_CANCEL = "eu.krawczyk.shutterstocktask.service.receiver.cancel";
    // Download result
    private int mResult = EDownloadStatus.FAILED.ordinal();

    private boolean mIsCancelled = false;
    private IBinder mBinder = new LocalBinder();

    public DownloadService() {
        super("DownloadService");
        mNotifyManager =
                (NotificationManager) ShutterstockTask.getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String urlPath = intent.getStringExtra(URL);
        String name = intent.getStringExtra(NAME);
        File output = new File(Environment.getExternalStorageDirectory(),
                name);
        // check if output directory exists
        if (output.exists()) {
            output.delete();
        }
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            URL url = new URL(urlPath);
            fileOutputStream = new FileOutputStream(output.getPath());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e(Constants.General.TAG, "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage());
                mResult = EDownloadStatus.FAILED.ordinal();
            } else {
                prepareNotification();
                int fileLength = connection.getContentLength();
                inputStream = connection.getInputStream();
                byte data[] = new byte[BUFFER_SIZE];
                long total = 0;
                int count;
                while ((count = inputStream.read(data)) != -1 && mIsCancelled == false) {
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) {
                        updateNotification((int) (total * 100 / fileLength));
                    }
                    fileOutputStream.write(data, 0, count);
                }
                // finished
                if (mIsCancelled == true) {
                    mResult = EDownloadStatus.CANCELLED.ordinal();
                    // Reset mIsCancelled flag
                    mIsCancelled = false;
                } else {
                    mResult = EDownloadStatus.SUCCESS.ordinal();
                }
                updateNotification(MAX_PROGRESS);
            }
        } catch (Exception e) {
            Log.e(Constants.General.TAG, "Exception while downloading image: " + e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(Constants.General.TAG, "Exception while closing stream: " + e);
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    Log.e(Constants.General.TAG, "Exception while closing stream: " + e);
                }
            }
        }
        publishResults(output.getAbsolutePath(), mResult, 100);
    }

    public void cancelDownload(int notificationId) {
        if (notificationId == mNotificationId) {
            mIsCancelled = true;
        }
    }

    // Create progress notification with cancel action
    private void prepareNotification() {
        mNotificationId = new Random().nextInt(Integer.MAX_VALUE);
        Intent notificationIntent = new Intent(ACTION_CANCEL);
        notificationIntent.putExtra(NOTIFICATION_ID, mNotificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(getText(R.string.notification_title))
                .setContentText(getText(R.string.notification_progress))
                .setSmallIcon(R.drawable.ic_stat_notification);
        mBuilder.addAction(R.drawable.ic_stat_deny, getText(android.R.string.cancel), pendingIntent);
    }

    // Update progress on notification or change it to finished/canceled notification
    private void updateNotification(int currentProgress) {
        if (currentProgress < MAX_PROGRESS) {
            mBuilder.setProgress(MAX_PROGRESS, currentProgress, false);
        } else if (mResult == EDownloadStatus.SUCCESS.ordinal()) {
            mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setContentTitle(getText(R.string.notification_title)).setContentText(getText(R.string.notification_completed)).setSmallIcon(R.drawable.ic_stat_notification).setProgress(0, 0, false);
        } else if (mResult == EDownloadStatus.CANCELLED.ordinal()) {
            mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setContentTitle(getText(R.string.notification_title)).setContentText(getText(R.string.download_cancelled)).setSmallIcon(R.drawable.ic_stat_notification).setProgress(0, 0, false);
        }
        mNotifyManager.notify(mNotificationId, mBuilder.build());
    }

    // Publish result in form of toast in MainActivity
    private void publishResults(String outputPath, int result, int progress) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(FILEPATH, outputPath);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //Service binder
    public class LocalBinder extends Binder {
        public DownloadService getServerInstance() {
            return DownloadService.this;
        }
    }
}
