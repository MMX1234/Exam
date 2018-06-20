package com.example.exam;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exam.db.ErrorExam;
import com.example.exam.db.Exam;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

public class ButtonTwoActivity extends AppCompatActivity {
    //上部分
    private TextView testQTypeView;
    private TextView testQView;
    private ImageView testPicView;

    private View internalView;

    //单选
    private RadioGroup rbGroup;
    private RadioButton option1RB;
    private RadioButton option2RB;
    private RadioButton option3RB;
    private RadioButton option4RB;
    //判断
    private RadioGroup tfbGroup;
    private RadioButton trueButton;
    private RadioButton falseButton;
    //中下
    private LinearLayout typeLayout;
    private Button nextButton;
    //底栏
    private TextView main_right_tx;
    private TextView main_error_tx;
    private DefineTimer countDownTimer;
    private TextView question_countdown;
    private TextView main_total_tx;
    //对错总数
    private int right_sum = 0;
    private int error_sum = 0;

    private int questionType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_two);

        testQTypeView = findViewById(R.id.testQTypeView);
        testQView = findViewById(R.id.testQView);
        testPicView = findViewById(R.id.testPicView);

        typeLayout = findViewById(R.id.typeLayout);
        nextButton = findViewById(R.id.nextButton);

        main_right_tx = findViewById(R.id.main_right_tx);
        main_error_tx = findViewById(R.id.main_error_tx);
        question_countdown = findViewById(R.id.question_countdown);
        main_total_tx = findViewById(R.id.main_total_tx);

        startTimer();
        Exam exam = LitePal.findFirst(Exam.class);
        showResultInfo(exam);

        nextButton.setOnClickListener(new View.OnClickListener() {
            Exam exam = LitePal.findLast(Exam.class);
            int lastId = exam.getId();
            int id = 2;

            @Override
            public void onClick(View view) {
                typeLayout = findViewById(R.id.typeLayout);
                if (questionType == 1) {
                    rbGroup = internalView.findViewById(R.id.rbGroup);
                    if (rbGroup.getCheckedRadioButtonId() != -1) {
                        RadioButton mRB = findViewById(rbGroup.getCheckedRadioButtonId());
                        if (exam.getAnswer().contentEquals(mRB.getText().subSequence(0, 1))) {
                            right_sum++;
                        } else {
                            error_sum++;
                            addErrorExam(exam.getId());
                        }
                    } else {
                        error_sum++;
                        addErrorExam(exam.getId());
                    }
                } else if (questionType == 2) {
                    tfbGroup = internalView.findViewById(R.id.tfbGroup);
                    if (tfbGroup.getCheckedRadioButtonId() != -1) {
                        RadioButton mTFB = findViewById(tfbGroup.getCheckedRadioButtonId());
                        if (exam.getAnswer().contentEquals(mTFB.getText())) {
                            right_sum++;
                        } else {
                            error_sum++;
                            addErrorExam(exam.getId());
                        }
                    } else {
                        error_sum++;
                        addErrorExam(exam.getId());
                    }
                }
                if (error_sum <= 10) {
                    if (right_sum + error_sum < 100) {
                        main_right_tx.setText(String.valueOf(right_sum));
                        main_error_tx.setText(String.valueOf(error_sum));
                        typeLayout.removeAllViews();
                        exam = LitePal.find(Exam.class, id);
                        showResultInfo(exam);
                        if (exam.getId() < lastId) {
                            id++;
                        } else {
                            id = 100;
                            Toast.makeText(ButtonTwoActivity.this, "最后一题了！", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(ButtonTwoActivity.this, "不及格！", Toast.LENGTH_SHORT).show();
                    nextButton.setEnabled(false);
                }
            }
        });
    }

    private void showResultInfo(Exam exam) {
        typeLayout.removeAllViews();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ButtonTwoActivity.this).edit();
        if (exam.getAnswer().equals("A") || exam.getAnswer().equals("B") || exam.getAnswer().equals("C") || exam.getAnswer().equals("D")) {
            questionType = 1;
            editor.putString("q_type", "单选");
            editor.apply();
            internalView = LayoutInflater.from(this).inflate(R.layout.layout_rb, typeLayout, false);
            option1RB = internalView.findViewById(R.id.option1RB);
            option2RB = internalView.findViewById(R.id.option2RB);
            option3RB = internalView.findViewById(R.id.option3RB);
            option4RB = internalView.findViewById(R.id.option4RB);

            testQTypeView.setText("单选题");
            testQView.setText("问题" + exam.getId() + "：" + exam.getQuestion());
            option1RB.setText(exam.getOption1());
            option2RB.setText(exam.getOption2());
            option3RB.setText(exam.getOption3());
            option4RB.setText(exam.getOption4());

            typeLayout.addView(internalView);
        } else if (exam.getAnswer().equals("对") || exam.getAnswer().equals("错")) {
            questionType = 2;
            editor.putString("q_type", "判断");
            editor.apply();
            internalView = LayoutInflater.from(this).inflate(R.layout.layout_tfb, typeLayout, false);

            testQTypeView.setText("判断题");
            testQView.setText("问题" + exam.getId() + "：" + exam.getQuestion());

            typeLayout.addView(internalView);
        }
        main_total_tx.setText(String.valueOf(exam.getId()) + "/100");
    }

    private long currentTime = 2700000L;

    public void startTimer() {
        countDownTimer = new DefineTimer(currentTime, 1000) {//2700 45分钟
            @Override
            public void onTick(long l) {
                currentTime = l;
                int allSecond = (int) l / 1000;//秒
                int minute = allSecond / 60;
                int second = allSecond - minute * 60;
                question_countdown.setText("时间" + minute + ":" + second);
            }

            @Override
            public void onFinish() {
                question_countdown.setText("时间 00:00");
                Toast.makeText(ButtonTwoActivity.this, "时间结束", Toast.LENGTH_SHORT).show();
                nextButton.setEnabled(false);
            }
        };
        countDownTimer.start();
    }

    private void addErrorExam(int id) {
        Connector.getDatabase();
        ErrorExam errorExam = new ErrorExam();
        Exam exam = LitePal.find(Exam.class, id);
        errorExam.setQId(String.valueOf(exam.getId()));
        errorExam.setQuestion(exam.getQuestion());
        errorExam.setOption1(exam.getOption1());
        errorExam.setOption2(exam.getOption2());
        errorExam.setOption3(exam.getOption3());
        errorExam.setOption4(exam.getOption4());
        errorExam.setAnswer(exam.getAnswer());
        errorExam.setExplain(exam.getExplain());
        errorExam.setPic(exam.getPic());
        errorExam.setType(exam.getType());
        errorExam.setChapter(exam.getChapter());
        errorExam.save();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putInt("id_error", 1);
        editor.apply();
    }
}
