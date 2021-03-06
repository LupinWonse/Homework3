/*
 Homwork 4
 TriviaActivity
 Akarsh Gupta     - 800969888
 Ahmet Gencoglu   - 800982227
*/

package com.example.ahmet.homework3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TriviaActivity extends AppCompatActivity implements GetImageAsync.IImageDisplay {

    public static final String CORRECT_ANSWERS_KEY = "CORRECT";
    public static final String QUESTIONS_COUNT_KEY = "COUNT_OF_QUESTIONS";

    GetImageAsync imageRetirever;

    private int currentQuestion;
    private int correctQuestions = 0;

    private ArrayList<Question> questionsList;

    private TextView textQuestionNumber;
    private TextView textQuestionText;
    private ImageView imageQuestionImage;
    private ProgressBar progressImageLoading;
    private TextView textImageLoadingLabel;
    private TextView textTimeLeft;

    private CountDownTimer timer;

    private RadioGroup choicesRadioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);

        choicesRadioGroup = (RadioGroup) findViewById(R.id.radioGroupChoices);
        textQuestionNumber = (TextView) findViewById(R.id.textQuestionNumber);
        textQuestionText = (TextView) findViewById(R.id.textQuestionText);
        imageQuestionImage = (ImageView) findViewById(R.id.imageQuestionImage);
        progressImageLoading = (ProgressBar) findViewById(R.id.progressImageLoading);
        textImageLoadingLabel = (TextView) findViewById(R.id.textImageLoading);
        textTimeLeft = (TextView) findViewById(R.id.textTimeLeft);

        imageRetirever = new GetImageAsync(this);

        if(getIntent().getSerializableExtra(MainActivity.QUESTION_ARRAY_KEY) != null){
            questionsList = (ArrayList<Question>) getIntent().getSerializableExtra(MainActivity.QUESTION_ARRAY_KEY);
            showQuestion(0);
            startCountdown();
        } else
        {
            Toast.makeText(this,getResources().getString(R.string.toastNoDatabase),Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(timer != null){
            timer.cancel();
        }
    }

    private void startCountdown(){
        timer = new CountDownTimer(120 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textTimeLeft.setText(getString(R.string.textTimeLeft) + " "
                        + Long.toString(millisUntilFinished/1000) + " "
                        + getString(R.string.textSeconds));
            }

            @Override
            public void onFinish() {
                showResults();
            }
        }.start();
    }

    private void showQuestion(int i) {

        imageQuestionImage.setImageBitmap(null);
        progressImageLoading.setVisibility(View.VISIBLE);
        textImageLoadingLabel.setVisibility(View.VISIBLE);


        Question question = questionsList.get(i);
        textQuestionNumber.setText("Q" + Integer.toString(question.getId() + 1));
        textQuestionText.setText(question.getText());

        choicesRadioGroup.removeAllViews();
        for (String choice: question.getChoices()) {
            addRadioButton(choice);
        }

        if (question.getImage() != null){
            imageRetirever = new GetImageAsync(this);
            imageRetirever.execute(question.getImage());
        } else {
            imageQuestionImage.setImageResource(R.drawable.question_mark);
            progressImageLoading.setVisibility(View.INVISIBLE);
            textImageLoadingLabel.setVisibility(View.INVISIBLE);
        }
    }

    private void addRadioButton(String radioButtonText){
        RadioButton radioButton = new RadioButton(this);
        radioButton.setText(radioButtonText);
        choicesRadioGroup.addView(radioButton,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void setImage(Bitmap image) {
        imageQuestionImage.setImageBitmap(image);
        progressImageLoading.setVisibility(View.INVISIBLE);
        textImageLoadingLabel.setVisibility(View.INVISIBLE);
    }

    public void nextQuestion(View view) {
        imageRetirever.cancel(false);

        if(checkAnswer()) {
            correctQuestions++;
        }

        if (currentQuestion < questionsList.size() - 1) {
            currentQuestion++;
            showQuestion(currentQuestion);
        } else {
            showResults();
        }
    }

    private void showResults() {
        Intent intent = new Intent(this, StatsActivity.class);
        intent.putExtra(CORRECT_ANSWERS_KEY,correctQuestions);
        intent.putExtra(QUESTIONS_COUNT_KEY,questionsList.size());
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        currentQuestion = 0;
        showQuestion(0);
        startCountdown();
    }

    public boolean checkAnswer(){
        int givenAnswer = choicesRadioGroup.indexOfChild(choicesRadioGroup.findViewById(choicesRadioGroup.getCheckedRadioButtonId())) + 1;

        if (questionsList.get(currentQuestion).getAnswer() == givenAnswer) {
            return true;
        } else {
            return false;
        }
    }

    public void quit(View view){
        finish();
    }
}
