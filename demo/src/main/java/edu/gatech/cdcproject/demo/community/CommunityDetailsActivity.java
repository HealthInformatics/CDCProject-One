package edu.gatech.cdcproject.demo.community;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import edu.gatech.cdcproject.backend.myApi.model.FoodImage;
import edu.gatech.cdcproject.demo.R;
import edu.gatech.cdcproject.demo.about.AboutActivity;
import edu.gatech.cdcproject.demo.util.IntentUtils;
import edu.gatech.cdcproject.demo.util.TimeUtils;

import static edu.gatech.cdcproject.demo.network.Api.myApi;
/**
 * Created by guoweidong on 3/30/16.
 */
public class CommunityDetailsActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_details);

        setupUI();
    }

    private void setupUI() {
        //Cardview
        cardView = (CardView)findViewById(R.id.cardView);
        //Progressbar
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("About");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommunityDetailsActivity.this.onBackPressed();
            }
        });
        // FoodImage
        Long id = getIntent().getLongExtra(IntentUtils.INTENT_EXTRA_FOODIMAGE_ID, -1);
        if(id != -1) {
            new AsyncTask<Long, Void, FoodImage>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    cardView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                protected FoodImage doInBackground(Long... params) {
                    try {
                        return myApi().image().get(params[0]).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(FoodImage foodImage) {
                    super.onPostExecute(foodImage);
                    if (foodImage != null) {
                        Glide.with(CommunityDetailsActivity.this).load(foodImage.getImageUrl()).into((ImageView) findViewById(R.id.foodImg));

                        ((TextView) findViewById(R.id.titleTxt)).setText(foodImage.getTitle());

                        ((TextView) findViewById(R.id.timeTxt)).setText(TimeUtils.formatSimpleDate(foodImage.getCreated().getValue()));
                    }
                    progressBar.setVisibility(View.GONE);
                    cardView.setVisibility(View.VISIBLE);
                }
            }.execute(id);
        }
    }


}
