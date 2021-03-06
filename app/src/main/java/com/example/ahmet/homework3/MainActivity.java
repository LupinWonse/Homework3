/*
 Homwork 4
 MainActivity
 Akarsh Gupta     - 800969888
 Ahmet Gencoglu   - 800982227
*/

package com.example.ahmet.homework3;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TriviaMainActivity {
    public static final String QUESTION_ARRAY_KEY = "QUESTIONS";
    private ArrayList<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(isOnline()) {
            new GetTriviaAsync(this).execute();
        } else {
            Toast.makeText(this,getString(R.string.toastNoConnection),Toast.LENGTH_LONG).show();
        }
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setTrivia(ArrayList<Question> questions) {
        // TODO Better handling of no questions case
        if (questions.size() == 0){
            Toast.makeText(this, "No Questions Found on Server", Toast.LENGTH_SHORT).show();
        }
        else {
            ((Button) findViewById(R.id.buttonStart)).setEnabled(true);
            ((ProgressBar) findViewById(R.id.progressTriviaLoading)).setVisibility(View.INVISIBLE);
            ((ImageView) findViewById(R.id.imageTriviaLogo)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.textTriviaStatus)).setText(getString(R.string.textReady));
            this.questions = questions;
        }
    }

    public void endActivity(View view){
        finish();
    }

    public void startTrivia(View view){
        Intent triviaIntent = new Intent(this,TriviaActivity.class);
        triviaIntent.putExtra(QUESTION_ARRAY_KEY,questions);
        startActivity(triviaIntent);
    }
}
