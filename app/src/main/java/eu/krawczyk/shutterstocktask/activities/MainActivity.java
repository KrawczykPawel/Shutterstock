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
package eu.krawczyk.shutterstocktask.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import eu.krawczyk.shutterstocktask.R;
import eu.krawczyk.shutterstocktask.ShutterstockTask;
import eu.krawczyk.shutterstocktask.adapters.ImageAdapter;
import eu.krawczyk.shutterstocktask.helpers.RecyclerItemClickListener;
import eu.krawczyk.shutterstocktask.helpers.Utils;
import eu.krawczyk.shutterstocktask.model.ImageFile;
import eu.krawczyk.shutterstocktask.model.enums.EDownloadStatus;
import eu.krawczyk.shutterstocktask.services.DownloadService;

/**
 * @author Paweł Krawczyk
 * @class The MainActivity Class.
 * <p/>
 * @since 31 oct, 2014 General activity of application.
 */
public class MainActivity extends Activity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private boolean mBounded;
    private DownloadService mDownloadService;

    private int mCurrentCanceledId;

    private static List<ImageFile> sFakeData = new ArrayList<ImageFile>() {
        {
            add(new ImageFile("img1.jpg", "http://www2.shutterstock.com/webstack/img/lohp/carousel/Vectors/shutterstock_121795933.jpg", "http://image.shutterstock.com/display_pic_with_logo/614824/121795933/stock-vector-abstract-seamless-background-121795933.jpg"));
            add(new ImageFile("img2.jpg", "http://www2.shutterstock.com/webstack/img/lohp/carousel/Vectors/shutterstock_203963281.jpg", "http://image.shutterstock.com/display_pic_with_logo/563656/203963281/stock-vector-polygonal-running-man-vector-geometric-illustration-203963281.jpg"));
            add(new ImageFile("img3.jpg", "http://www2.shutterstock.com/webstack/img/lohp/carousel/Vectors/shutterstock_155719607.jpg", "http://image.shutterstock.com/display_pic_with_logo/1206974/155719607/stock-vector-molecule-and-communication-background-vector-illustration-graphic-design-useful-for-your-design-155719607.jpg"));
            add(new ImageFile("img4.jpg", "http://www2.shutterstock.com/webstack/img/lohp/carousel/Vectors/shutterstock_141038692.jpg", "http://image.shutterstock.com/display_pic_with_logo/184828/141038692/stock-vector-oriental-mandala-motif-round-lase-pattern-on-the-violet-background-like-snowflake-or-mehndi-paint-141038692.jpg"));
            add(new ImageFile("img5.jpg", "http://www2.shutterstock.com/webstack/img/lohp/carousel/Editorial/shutterstock_128470907.jpg", "http://image.shutterstock.com/display_pic_with_logo/90275/128470907/stock-photo-melbourne-january-roger-federer-of-switzerland-in-a-practice-sessionat-the-australian-128470907.jpg"));
            add(new ImageFile("img6.jpg", "http://www2.shutterstock.com/webstack/img/lohp/carousel/Editorial/shutterstock_194231714.jpg", "http://image.shutterstock.com/display_pic_with_logo/592525/194231714/stock-photo-brasov-romania-may-zorki-is-the-most-popular-of-all-zorki-cameras-introduced-in-194231714.jpg"));
            add(new ImageFile("img7.jpg", "http://www2.shutterstock.com/webstack/img/lohp/carousel/Illustrations/shutterstock_208193845.jpg", "http://image.shutterstock.com/display_pic_with_logo/2118710/208193845/stock-photo-low-poly-mountain-landscape-at-dusk-with-moon-208193845.jpg"));
            add(new ImageFile("img8.jpg", "http://www2.shutterstock.com/webstack/img/lohp/carousel/Illustrations/shutterstock_1234388.jpg", "http://image.shutterstock.com/display_pic_with_logo/2197/2197,1145745703,1/stock-photo-auszug-1234388.jpg"));
            add(new ImageFile("img9.jpg", "http://www2.shutterstock.com/webstack/img/lohp/carousel/Illustrations/shutterstock_155247809.jpg", "http://image.shutterstock.com/display_pic_with_logo/1103363/155247809/stock-vector-cute-card-with-girl-in-vector-romantic-valentines-day-background-155247809.jpg"));
            add(new ImageFile("img10.jpg", "http://www2.shutterstock.com/webstack/img/lohp/carousel/Illustrations/shutterstock_172720433.jpg", "http://image.shutterstock.com/display_pic_with_logo/804196/172720433/stock-photo-green-limes-slices-watercolor-tiled-background-172720433.jpg"));
            add(new ImageFile("img11.jpg", "http://www2.shutterstock.com/webstack/img/lohp/carousel/Icons/shutterstock_207942454.jpg", "http://image.shutterstock.com/display_pic_with_logo/1332355/207942454/stock-vector-white-icon-on-turquoise-background-207942454.jpg"));
            add(new ImageFile("img12.jpg", "http://www2.shutterstock.com/webstack/img/lohp/carousel/Icons/shutterstock_192055991.jpg", "http://image.shutterstock.com/display_pic_with_logo/265048/192055991/stock-vector-christmas-tree-icon-set-192055991.jpg"));

        }
    };

    private BroadcastReceiver mDownloadBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String string = bundle.getString(DownloadService.FILEPATH);
                int resultCode = bundle.getInt(DownloadService.RESULT);
                if (resultCode == EDownloadStatus.SUCCESS.ordinal()) {
                    Toast.makeText(MainActivity.this,
                            getText(R.string.toast_download_finished) + string,
                            Toast.LENGTH_LONG).show();
                } else if (resultCode == EDownloadStatus.CANCELLED.ordinal()) {
                    Toast.makeText(MainActivity.this, R.string.download_cancelled,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.download_failed,
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    private BroadcastReceiver mDownloadCancelBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mCurrentCanceledId = bundle.getInt(DownloadService.NOTIFICATION_ID);
                bindService(new Intent(MainActivity.this, DownloadService.class), mConnection, Context.BIND_AUTO_CREATE);
            }
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            mBounded = false;
            mDownloadService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            mBounded = true;
            DownloadService.LocalBinder mLocalBinder = (DownloadService.LocalBinder) service;
            mDownloadService = mLocalBinder.getServerInstance();
            mDownloadService.cancelDownload(mCurrentCanceledId);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ImageFile imageFile = sFakeData.get(position);
                        Intent intent = new Intent(MainActivity.this, DownloadService.class);
                        // add infos for the service which file to download and where to store
                        intent.putExtra(DownloadService.URL,
                                imageFile.getUrl());
                        intent.putExtra(DownloadService.NAME,
                                imageFile.getName());
                        startService(intent);
                        Toast.makeText(MainActivity.this, R.string.toast_download_started,
                                Toast.LENGTH_LONG).show();
                    }
                })
        );

        // specify an adapter (see also next example)
        RequestQueue requestQueue = Volley.newRequestQueue(ShutterstockTask.getApplication());
        ImageLoader mImageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(100);

            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
        sFakeData = Utils.multiplyList(sFakeData, 10);
        mAdapter = new ImageAdapter(sFakeData, mImageLoader);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mDownloadBroadcastReceiver, new IntentFilter(DownloadService.NOTIFICATION));
        registerReceiver(mDownloadCancelBroadcastReceiver, new IntentFilter(DownloadService.ACTION_CANCEL));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mDownloadBroadcastReceiver);
        unregisterReceiver(mDownloadCancelBroadcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}