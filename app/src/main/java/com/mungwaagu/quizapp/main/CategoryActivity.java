package com.mungwaagu.quizapp.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.mungwaagu.quizapp.R;
import com.mungwaagu.quizapp.adapters.CategoryAdapter;
import com.mungwaagu.quizapp.includes.Constants;
import com.mungwaagu.quizapp.models.CategoryModel;

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


public class CategoryActivity extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;

    String BASE_URL = Constants.BASE_URL;
    String METHOD = Constants.GET_METHOD;
    String FOLDER = Constants.REQUEST_CATEGORIES_FOLDER;

    GridLayoutManager gridLayout;
    CategoryAdapter categories_adapter;
    RecyclerView categories_rv;
    private List<CategoryModel> categoryModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); ///Eneter into fullscreen mode

        //Google Ads - Banner Ads Integration
        MobileAds.initialize(this, getString(R.string.admob_id));
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        /*/Google ads - interstatial Ads Integration
        MobileAds.initialize(this, getString(R.string.admob_id));
        mInterstitialAd = new InterstitialAd(this);
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
        });*/

        categoryModelList = new ArrayList<>();
        categories_rv = findViewById(R.id.categories_rv);

        getCategories();

        gridLayout = new GridLayoutManager(getApplicationContext(), 1);
        categories_rv.setLayoutManager(gridLayout);
        categories_adapter = new CategoryAdapter(CategoryActivity.this, categoryModelList);
        categories_rv.setAdapter(categories_adapter);

    }

    //Method to Fetch Categories
    private void getCategories () {

        @SuppressLint("StaticFieldLeak") AsyncTask<Integer, Void, Void> asyncTask = new AsyncTask<Integer, Void, Void>() {

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(CategoryActivity.this, "Please wait", "Loading Categories...");
            }

            @Override
            protected Void doInBackground(Integer... matchIds) {

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
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

                        CategoryModel categoryModel = new CategoryModel(object.getString("category"),
                                object.getString("category_total_questions"));

                        categoryModelList.add(categoryModel);
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

                categories_adapter.notifyDataSetChanged();

                loadingDialog.dismiss();

            }
        };

        asyncTask.execute();
    }


}