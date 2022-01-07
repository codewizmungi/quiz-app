package com.mungwaagu.quizapp.main;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.mungwaagu.quizapp.R;
import com.mungwaagu.quizapp.includes.Constants;
import com.mungwaagu.quizapp.models.QuestionModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QuizActivity extends AppCompatActivity {

    String BASE_URL = Constants.BASE_URL;
    String METHOD = Constants.GET_METHOD;
    String FOLDER = Constants.REQUEST_QUESTIONS_FOLDER;

    CardView Next_btn;
    LinearLayout linearLayout1;
    TextView txtQuestions, txtQuestionsIndicator, totalQuestions, next_text, quiz_title;

    String quizCategoryString;

    private int count = 0;
    private int position = 0;
    private List<QuestionModel> list;
    private int score = 0;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back);
        upArrow.setColorFilter(this.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle(Html.fromHtml("<p></p>"));

        Bundle extras = getIntent().getExtras();
        quizCategoryString = extras.getString("quizCategory");

        //All Hooks

        linearLayout1 = findViewById(R.id.options_layout);
        txtQuestions = findViewById(R.id.question);
        txtQuestionsIndicator = findViewById(R.id.current_question_view);
        totalQuestions = findViewById(R.id.total_questions_view);
        next_text = findViewById(R.id.next_text);
        Next_btn = findViewById(R.id.next_btn);
        quiz_title = findViewById(R.id.quiz_title);

        String quiz_title_string = quizCategoryString + " QUIZ";
        quiz_title.setText(quiz_title_string);

        //Google Ads-----Banner Ads Integration
        MobileAds.initialize(this, getString(R.string.admob_id));
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        list = new ArrayList<>();

        getQuestions();

        for (int i = 0; i < 3; i++) {
            linearLayout1.getChildAt(i).setVisibility(View.VISIBLE);
            linearLayout1.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAns((Button) v);
                }
            });
        }

        Next_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                Next_btn.setEnabled(false);
                enableOptions(true);
                position++;

                //Show an Ad for every 10 Questions viewed
                if(position % 10 == 0){

                    //Google ads - interstatial Ad Integration
                    MobileAds.initialize(getApplicationContext(), getString(R.string.admob_id));
                    mInterstitialAd = new InterstitialAd(getApplicationContext());
                    mInterstitialAd.setAdUnitId(getString(R.string.inter_id));
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {

                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                            } else {
                                Log.d("TAG", "The interstitial wasn't loaded yet.");
                            }
                        }
                    });
                }

                if (position == list.size()) {
                    //Score Activities
                    Intent intent = new Intent(QuizActivity.this, ResultsActivity.class);
                    intent.putExtra("category", quizCategoryString);
                    intent.putExtra("score", score);
                    intent.putExtra("totalQuestions", list.size());
                    startActivity(intent);
                    finish();

                }else{
                    count = 0;
                    playAnim(txtQuestions, 0, list.get(position).getQuestions());
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getQuestions () {

        @SuppressLint("StaticFieldLeak") AsyncTask<Integer, Void, Void> asyncTask = new AsyncTask<Integer, Void, Void>() {

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(QuizActivity.this, "Please wait", "Loading Questions...");
            }

            @Override
            protected Void doInBackground(Integer... matchIds) {

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("category", quizCategoryString)
                        .build();

                Request request = new Request.Builder()
                        .url(BASE_URL+METHOD+FOLDER)
                        .post(formBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();

                    JSONArray array = new JSONArray(response.body().string());

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject object = array.getJSONObject(i);

                        QuestionModel QuestionModel = new QuestionModel(object.getString("question"),
                                object.getString("option_a"), object.getString("option_b"),
                                object.getString("option_c"), object.getString("correct_answer"));

                        list.add(QuestionModel);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                loadingDialog.dismiss();

                txtQuestionsIndicator.setText("Question "+(position + 1));
                totalQuestions.setText("/"+list.size());
                playAnim(txtQuestions, 0, list.get(position).getQuestions());

            }
        };

        asyncTask.execute();
    }

    private void playAnim(final View view, final int value, final String data) {

        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                if (value == 0 && count < 3) {
                    String option = "";
                    if (count == 0) {
                        option = list.get(position).getOptionA();
                    } else if (count == 1) {
                        option = list.get(position).getOptionB();
                    } else if (count == 2) {
                        option = list.get(position).getOptionC();
                    }
                    playAnim(linearLayout1.getChildAt(count), 0, option);
                    count++;
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onAnimationEnd(Animator animation) {

                if (value == 0) {

                    try {
                        ((TextView) view).setText(data);
                        txtQuestionsIndicator.setText("Question "+(position + 1));
                        totalQuestions.setText("/"+list.size());
                    } catch (ClassCastException ex) {
                        ((Button) view).setText(data);
                    }
                    view.setTag(data);


                    playAnim(view, 1, data);

                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    private void checkAns(Button selectedOptions) {
        enableOptions(false);
        Next_btn.setEnabled(true);
        if (selectedOptions.getText().toString().equals(list.get(position).getCorrectAnswer())) {
            //correct Answer
            score++;
            final MediaPlayer mp = MediaPlayer.create(this, R.raw.select_click);
            selectedOptions.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#14E39A")));
            selectedOptions.setElevation(5f);
            mp.start();
        } else {
            //wrong Answer
            final MediaPlayer mp = MediaPlayer.create(this, R.raw.click_error);
            selectedOptions.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF2B55")));
            selectedOptions.setElevation(5f);
            Button correctOption = linearLayout1.findViewWithTag(list.get(position).getCorrectAnswer());
            correctOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#14E39A")));
            correctOption.setElevation(5f);

            mp.start();
        }
    }

    private void enableOptions(boolean enable) {
        for (int i = 0; i < 3; i++) {
            linearLayout1.getChildAt(i).setEnabled(enable);
            linearLayout1.getChildAt(i).setElevation(5f);
            if (enable) {
                linearLayout1.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                linearLayout1.getChildAt(i).setElevation(5f);
            }
        }
    }


}