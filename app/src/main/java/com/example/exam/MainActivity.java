package com.example.exam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.exam.db.Exam;
import com.example.exam.gson.QAList;
import com.example.exam.gson.Result;
import com.example.exam.util.HttpUtil;
import com.example.exam.util.Utility;

import org.litepal.tablemanager.Connector;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button button1;
    private Button button2;
    private Button button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getString("responseText", null) == null) {
            requestResult();
            Toast.makeText(this, "首次执行requestResult", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1: {
                Intent intent1 = new Intent(this, ButtonOneActivity.class);
                startActivity(intent1);
                finish();
                break;
            }
            case R.id.button2: {
                Intent intent2 = new Intent(this, ButtonTwoActivity.class);
                startActivity(intent2);
                finish();
                break;
            }
            case R.id.button3: {
                Intent intent3 = new Intent(this, ButtonThreeActivity.class);
                startActivity(intent3);
                finish();
                break;
            }
        }
    }

    private void requestResult() {
        String addr = "http://jisujiakao.market.alicloudapi.com/driverexam/query?pagenum=1&pagesize=100&sort=normal&subject=1&type=C1";
        HttpUtil.sendOkHttpRequest(addr, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Result result = Utility.handleResultResponse(responseText);

                FileOutputStream out = null;
                BufferedWriter writer = null;
                out = openFileOutput("data.json", Context.MODE_PRIVATE);
                writer = new BufferedWriter(new OutputStreamWriter(out));
                writer.write(responseText);
                if (writer != null) {
                    writer.close();
                }

                Connector.getDatabase();
                for (QAList qaList : result.qaLists) {
                    Exam exam = new Exam();
                    exam.setQuestion(qaList.question);
                    exam.setOption1(qaList.option1);
                    exam.setOption2(qaList.option2);
                    exam.setOption3(qaList.option3);
                    exam.setOption4(qaList.option4);
                    exam.setAnswer(qaList.answer);
                    exam.setExplain(qaList.explain);
                    exam.setPic(qaList.pic);
                    exam.setType(qaList.type);
                    exam.setChapter(qaList.chapter);
                    exam.save();
                }

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                editor.putString("responseText", responseText);
                editor.apply();
            }

        });
    }
}
