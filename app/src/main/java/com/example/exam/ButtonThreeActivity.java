package com.example.exam;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exam.db.ErrorExam;

import org.litepal.LitePal;

public class ButtonThreeActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView e_questionView;
    private TextView e_option1View;
    private TextView e_option2View;
    private TextView e_option3View;
    private TextView e_option4View;

    private TextView e_answerView;
    private TextView e_explainView;

    private TextView e_picView;
    private TextView e_typeView;
    private TextView e_chapterView;

    private Button e_upButton;
    private Button e_downButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_three);

        e_questionView = findViewById(R.id.e_questionView);
        e_option1View = findViewById(R.id.e_option1View);
        e_option2View = findViewById(R.id.e_option2View);
        e_option3View = findViewById(R.id.e_option3View);
        e_option4View = findViewById(R.id.e_option4View);

        e_answerView = findViewById(R.id.e_answerView);
        e_explainView = findViewById(R.id.e_explainView);

        e_picView = findViewById(R.id.e_picView);
        e_typeView = findViewById(R.id.e_typeView);
        e_chapterView = findViewById(R.id.e_chapterView);

        e_upButton = findViewById(R.id.e_upButton);
        e_downButton = findViewById(R.id.e_downButton);
        e_upButton.setOnClickListener(this);
        e_downButton.setOnClickListener(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //如果有标记，查找上次标记题目位置，否则查询第一个（记录上次题目位置）
        if (preferences.getInt("id_error", 0) != 0) {
            int id = preferences.getInt("id_error", 0);
            ErrorExam errorExam = LitePal.find(ErrorExam.class, id);
            showResultInfo(errorExam);
        } else {
//            ErrorExam errorExam = LitePal.findFirst(ErrorExam.class);
//            showResultInfo(errorExam);
            Toast.makeText(this, "没有错题！", Toast.LENGTH_SHORT).show();
            e_upButton.setEnabled(false);
            e_downButton.setEnabled(false);
        }
    }

    private void showResultInfo(ErrorExam errorExam) {

        e_questionView.setText("问题" + errorExam.getQId() + "：" + errorExam.getQuestion());
        e_option1View.setText("选项1：" + errorExam.getOption1());
        e_option2View.setText("选项2：" + errorExam.getOption2());
        e_option3View.setText("选项3：" + errorExam.getOption3());
        e_option4View.setText("选项4：" + errorExam.getOption4());

        e_answerView.setText("答案：" + errorExam.getAnswer());
        e_explainView.setText("解析：" + errorExam.getExplain());

        e_picView.setText("图源：" + errorExam.getPic());
        e_typeView.setText("类型：" + errorExam.getType());
        e_chapterView.setText("章节：" + errorExam.getChapter());
    }

    @Override
    public void onClick(View view) {
        ErrorExam errorExam = LitePal.findLast(ErrorExam.class);
        int lastId = errorExam.getId();
        int id = 2;
        switch (view.getId()) {
            case R.id.e_upButton: {
                e_downButton.setEnabled(true);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                if (preferences.getInt("id_error", 2) != 0) {
                    id = preferences.getInt("id_error", 1);
                }
                id--;
                errorExam = LitePal.find(ErrorExam.class, id);
                //bug: 从最后一道题点击上一题会连跳2题
                showResultInfo(errorExam);
                if (errorExam.getId() == 1) {
                    Toast.makeText(this, "这是第一道错题！", Toast.LENGTH_SHORT).show();
                    e_upButton.setEnabled(false);
                } else {
                    id--;
                }
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putInt("id_error", id);
                editor.apply();
            }
            case R.id.e_downButton: {
                e_upButton.setEnabled(true);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                if (preferences.getInt("id_error", 2) != 0) {
                    id = preferences.getInt("id_error", 2);
                }
                errorExam = LitePal.find(ErrorExam.class, id);
                showResultInfo(errorExam);
                if (errorExam.getId() < lastId) {
                    id++;
                } else {
                    Toast.makeText(this, "最后一道错题了！", Toast.LENGTH_SHORT).show();

                    e_downButton.setEnabled(false);
                }
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putInt("id_error", id);
                editor.apply();
            }
        }
    }
}
