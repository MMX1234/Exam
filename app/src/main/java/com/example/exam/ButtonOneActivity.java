package com.example.exam;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exam.db.Exam;

import org.litepal.LitePal;

public class ButtonOneActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView questionView;
    private TextView option1View;
    private TextView option2View;
    private TextView option3View;
    private TextView option4View;

    private TextView answerView;
    private TextView explainView;

    private TextView picView;
    private TextView typeView;
    private TextView chapterView;

    private Button upButton;
    private Button downButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_one);

        questionView = findViewById(R.id.questionView);
        option1View = findViewById(R.id.option1View);
        option2View = findViewById(R.id.option2View);
        option3View = findViewById(R.id.option3View);
        option4View = findViewById(R.id.option4View);

        answerView = findViewById(R.id.answerView);
        explainView = findViewById(R.id.explainView);

        picView = findViewById(R.id.picView);
        typeView = findViewById(R.id.typeView);
        chapterView = findViewById(R.id.chapterView);

        upButton = findViewById(R.id.upButton);
        downButton = findViewById(R.id.downButton);
        upButton.setOnClickListener(this);
        downButton.setOnClickListener(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //如果有标记，查找上次标记题目位置，否则查询第一个（记录上次题目位置）
        if (preferences.getInt("id", 2) != 0) {
            int id = preferences.getInt("id", 1);
            Exam exam = LitePal.find(Exam.class, id);
            showResultInfo(exam);
        } else {
            Exam exam = LitePal.findFirst(Exam.class);
            showResultInfo(exam);
        }
    }


    private void showResultInfo(Exam exam) {

        questionView.setText("问题" + exam.getId() + "：" + exam.getQuestion());
        option1View.setText("选项1：" + exam.getOption1());
        option2View.setText("选项2：" + exam.getOption2());
        option3View.setText("选项3：" + exam.getOption3());
        option4View.setText("选项4：" + exam.getOption4());

        answerView.setText("答案：" + exam.getAnswer());
        explainView.setText("解析：" + exam.getExplain());

        picView.setText("图源：" + exam.getPic());
        typeView.setText("类型：" + exam.getType());
        chapterView.setText("章节：" + exam.getChapter());
    }

    @Override
    public void onClick(View view) {
        Exam exam = LitePal.findLast(Exam.class);
        int lastId = exam.getId();
        int id = 2;
        switch (view.getId()) {
            case R.id.upButton: {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                if (preferences.getInt("id", 2) != 0) {
                    id = preferences.getInt("id", 1);
                }
                id--;
                exam = LitePal.find(Exam.class, id);
                //bug: 从最后一道题点击上一题会连跳2题
                showResultInfo(exam);
                if (exam.getId() == 1) {
                    Toast.makeText(this, "这是第一题！", Toast.LENGTH_SHORT).show();
                    upButton.setEnabled(false);
                } else {
                    id--;
                }
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putInt("id", id);
                editor.apply();
            }
            case R.id.downButton: {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                if (preferences.getInt("id", 2) != 0) {
                    id = preferences.getInt("id", 2);
                }
                exam = LitePal.find(Exam.class, id);
                showResultInfo(exam);
                if (exam.getId() < lastId) {
                    id++;
                } else {
                    Toast.makeText(this, "最后一题了！", Toast.LENGTH_SHORT).show();
                    downButton.setEnabled(false);
                }
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putInt("id", id);
                editor.apply();
            }
        }
    }

}
