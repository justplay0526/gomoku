package com.example.gomoku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class second_activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Button btn_dlc = findViewById(R.id.btn_dlc);
        Button btn_single = findViewById(R.id.btn_single);
        Button btn_double = findViewById(R.id.btn_double);
        SeekBar seekBar = findViewById(R.id.seekBar);
        TextView tv_value = findViewById(R.id.tv_Value);
        tv_value.setText(getString(R.string.blockvalue)+"15");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    tv_value.setText(getString(R.string.blockvalue)+i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btn_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(second_activity.this,MyService.class);
                startService(intent);
                finish();
            }
        });

        btn_double.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(second_activity.this,MyService.class);
                startService(intent);
                finish();
            }
        });

        btn_dlc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(second_activity.this, "現在未實裝，以後也不會", Toast.LENGTH_SHORT).show();
            }
        });
    }
}