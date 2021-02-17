package com.example.yhyhealthydemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.example.yhyhealthydemo.adapter.VideoListAdapter;
import com.example.yhyhealthydemo.datebase.VideoListData;
import com.example.yhyhealthydemo.module.ApiProxy;
import com.example.yhyhealthydemo.tools.SpacesItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.yhyhealthydemo.module.ApiProxy.VIDEO_LIST;

public class VideoDetailActivity extends AppCompatActivity {

    private static final String TAG = "VideoDetailActivity";

    private RecyclerView recyclerView;

    //api
    private ApiProxy proxy;
    private VideoListData listData;

    //進度條
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        proxy = ApiProxy.getInstance();
        listData = new VideoListData();

        initView();

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null){
            String attrId = bundle.getString("AttrID");
            String serviceItemId = bundle.getString("ServiceItemId");
            String videoName = bundle.getString("AttName");
            initVideo(attrId, serviceItemId, videoName);
        }
    }

    private void initView() {
        recyclerView = findViewById(R.id.rv_video);
        int spacingInPixels = 10;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
    }

    private void initVideo(String attrId, String serviceItemId, String videoName) {
        //取得手機語系
        String language = getResources().getConfiguration().locale.getLanguage();
        String country = getResources().getConfiguration().locale.getCountry();
        String defaultLan = language + "-" + country;

        JSONObject json = new JSONObject();
        try {
            json.put("serviceItemId", serviceItemId);
            json.put("attrId", attrId);
            json.put("offset",1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        proxy.buildEdu(VIDEO_LIST, json.toString(), defaultLan, requestListener);
    }

    private ApiProxy.OnApiListener requestListener = new ApiProxy.OnApiListener() {
        @Override
        public void onPreExecute() {
            if(progressDialog == null){
                progressDialog = ProgressDialog.show(VideoDetailActivity.this, getString(R.string.title_process), getString(R.string.process), true);
            }else {
                progressDialog.show();
            }
        }

        @Override
        public void onSuccess(JSONObject result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    parserJson(result);
                }
            });
        }

        @Override
        public void onFailure(String message) {
            Log.d(TAG, "onFailure: " + message);
        }

        @Override
        public void onPostExecute() {
            progressDialog.dismiss();
        }
    };

    private void parserJson(JSONObject result) {
        listData = VideoListData.newInstance(result.toString());
        VideoListAdapter adapter = new VideoListAdapter(this, listData.getVideoList());
        recyclerView.setAdapter(adapter);
    }
}