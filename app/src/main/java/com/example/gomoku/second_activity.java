package com.example.gomoku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class second_activity extends AppCompatActivity {
    private int sbValue = 15;
    private int playWithBot ; // 1 is Bot, 2 is 2 Player

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Button btn_dlc = findViewById(R.id.btn_dlc);
        Button btn_single = findViewById(R.id.btn_single);
        Button btn_double = findViewById(R.id.btn_double);
        SeekBar seekBar = findViewById(R.id.seekBar);
        TextView tv_value = findViewById(R.id.tv_Value);
        tv_value.setText(getString(R.string.blockvalue)+ sbValue);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    tv_value.setText(getString(R.string.blockvalue)+i);
                    sbValue = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        btn_single.setOnClickListener(view -> {
            playWithBot = 1;
            Intent intent = new Intent(this,MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("sbKey",sbValue);
            bundle.putInt("BotKey",playWithBot);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        btn_double.setOnClickListener(view -> {
            playWithBot = 2;
            Intent intent = new Intent(this,MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("sbKey",sbValue);
            bundle.putInt("BotKey",playWithBot);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        btn_dlc.setOnClickListener(view -> Toast.makeText(second_activity.this, getString(R.string.dlcversion), Toast.LENGTH_SHORT).show());
    }
}