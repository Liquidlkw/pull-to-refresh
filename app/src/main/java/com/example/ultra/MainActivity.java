package com.example.ultra;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ultra.util.DynamicTimeFormat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    /** 请求获取的URL地址 */
    public static final String url = "https://www.imooc.com/api/teacher?type=4&num=30";

    private List<Bean> beanList;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;

    static boolean isFirstEnter=true;
    private ClassicsHeader mClassicsHeader;
    private Drawable mDrawableProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        RefreshLayout refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        //下拉刷新监听
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {

                refreshlayout.finishRefresh(1000/*,false*/);//传入false表示刷新失败
                okhttp();

            }
        });
//        //下拉刷新监听
//        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore(RefreshLayout refreshlayout) {
//                refreshlayout.finishLoadMore(500/*,false*/);//传入false表示加载失败

        if (isFirstEnter) {
            isFirstEnter = false;
            refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
            okhttp();
        }
//
//
//            }
//        });

//        refreshLayout.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(true));

        mClassicsHeader = (ClassicsHeader)refreshLayout.getRefreshHeader();
        mClassicsHeader.setLastUpdateTime(new Date(System.currentTimeMillis()));
        mClassicsHeader.setTimeFormat(new SimpleDateFormat("更新于 MM-dd HH:mm", Locale.CHINA));
        mClassicsHeader.setTimeFormat(new DynamicTimeFormat("更新于 %s"));

        mDrawableProgress = ((ImageView)mClassicsHeader.findViewById(ClassicsHeader.ID_IMAGE_PROGRESS)).getDrawable();
        if (mDrawableProgress instanceof LayerDrawable) {
            mDrawableProgress = ((LayerDrawable) mDrawableProgress).getDrawable(0);
        }


    }

    private void okhttp() {
        //1.创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //2.创建Request对象，设置一个url地址,设置请求方式。
        Request request = new Request.Builder().url(url).method("GET",null).build();
        //3.创建一个call对象,参数就是Request请求对象
        Call call = okHttpClient.newCall(request);
        //4.请求加入调度，重写回调方法
        call.enqueue(new Callback() {
            //请求失败执行的方法
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("lkw", "---------------------------------onFailure--------------------------------- ");
                Toast.makeText(MainActivity.this, "刷新失败~", Toast.LENGTH_SHORT).show();
            }
            //请求成功执行的方法
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("lkw", "---------------------------------onResponse--------------------------------- ");
                //获取解析成功网络请求后的字符串，string（）方法为gson自带的方法
                String responseString = response.body().string();

                try {
                    JSONObject jsonObject = new JSONObject(responseString);

                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    String jsonArrayString =jsonArray.toString();
                    //当访问网络成功后会执行这一个方法
                    //下面我们使用gson来接收数据就可以了
                    //创建gson对象
                    Gson gson = new Gson();
                    //下面将json数据与Bean类相关联
                    //创建list集合，通过TypeToken将希望解析成的数据传入fromJson中
                    beanList = gson.fromJson(jsonArrayString,new TypeToken<List<Bean>>(){}.getType());
                    //下面遍历输出在Logcat中
                    for(Bean bean: beanList){
                        Log.e("lkw", "json数组: "+bean.getName());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("lkw", "--------------------run---------------------------");
                        recyclerView = findViewById(R.id.recyclerView);
                        manager = new LinearLayoutManager(MainActivity.this);
                        recyclerView.setLayoutManager(manager);
                        //设置分割线
                        recyclerView.addItemDecoration(new DividerItemDecoration(
                                MainActivity.this, DividerItemDecoration.VERTICAL));
                        recyclerView.setAdapter(new Adapter(beanList));
                        Toast.makeText(MainActivity.this, "刷新成功！", Toast.LENGTH_SHORT).show();
                    }
                });




            }
        });
    }

    private void initView() {


    }


}
