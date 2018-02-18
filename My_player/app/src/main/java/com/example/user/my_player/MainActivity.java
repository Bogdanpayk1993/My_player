package com.example.user.my_player;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

import static com.example.user.my_player.R.id.parent;

public class MainActivity extends AppCompatActivity {

    ListView listView;

    List<String> list = new ArrayList<>();

    ListAdapter adapter;

    MediaPlayer mediaPlayer;

    SeekBar seekBar;

    AffableThread nSecond = new AffableThread();

    int resID, resumePosition = 0, t = 0, h = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(seekBarOnSeekListener);

        Field[] fields = R.raw.class.getFields();
        for(int i = 0; i < fields.length - 1; i++){
            list.add(fields[i].getName());
        }
        list.remove(0);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        nSecond.start();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                h = 0;
                t = i;
                Play(view);
            }
        });
    }

    public void Play(View view){
        if (h != 1) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            resID = getResources().getIdentifier(list.get(t), "raw", getPackageName());
            mediaPlayer = MediaPlayer.create(MainActivity.this, resID);
            seekBar.setMax(mediaPlayer.getDuration());
            resumePosition = seekBar.getProgress();
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
            h = 1;
        }
    }

    public void Pause(View view){
        h = 2;
        mediaPlayer.pause();
    }

    public void Stop(View view){
        h = 0;
        mediaPlayer.stop();
    }

    SeekBar.OnSeekBarChangeListener seekBarOnSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar){

        }

        @Override
        public void  onStartTrackingTouch(SeekBar seekBar){

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
            if (fromUser){
                mediaPlayer.seekTo(progress);
                seekBar.setProgress(progress);
            }
        }
    };

    class AffableThread extends Thread
    {
        @Override
        public void run(){
            while (true) {
                if (h == 1) {
                    resumePosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(resumePosition);
                }
                if (h == 0) {
                    resumePosition = 0;
                    seekBar.setProgress(resumePosition);
                }
                if (seekBar.getProgress() == seekBar.getMax()) {
                    resumePosition = 0;
                    seekBar.setProgress(resumePosition);
                    h = 0;
                }
            }
        }
    }
}
