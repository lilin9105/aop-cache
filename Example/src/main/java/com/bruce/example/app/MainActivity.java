package com.bruce.example.app;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import com.github.bluzwang.aopcache.database.CacheToDatabase;
import com.github.bluzwang.aopcache.acache.CacheMemory;
import com.github.bluzwang.aopcache.cache.CacheUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.Random;


public class MainActivity extends Activity {

    TextView tv;
    ScrollView sv;

    @CacheToDatabase()
    @CacheMemory(timeOutMs = 5000)
    private Observable<String> getResult(int i) {
        return Observable.just(i)
                .map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        StringBuffer buffer = new StringBuffer();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        for (int j = 0; j < 10; j++) {
                            buffer.append(integer);
                        }
                        return buffer.toString();
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CacheUtil.setApplicationContext(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        sv = (ScrollView) findViewById(R.id.sv);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int nextInt = new Random().nextInt(5);
                final long startTime = System.currentTimeMillis();
                getResult(nextInt)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                long usingTime = System.currentTimeMillis() - startTime;
                                String msg;
                                msg = s + " using time " + usingTime;
                                tv.append("\n");
                                if (usingTime > 1000) {
                                    tv.append(Html.fromHtml("<font color=\"#ff0000\">"+ msg +"</font>")) ;
                                } else {
                                    tv.append(msg);
                                }
                                sv.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });
            }
        });
    }
}
