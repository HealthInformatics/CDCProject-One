package edu.gatech.cdcproject.demo.community;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import edu.gatech.cdcproject.backend.myApi.model.FoodImage;
import edu.gatech.cdcproject.demo.R;
import edu.gatech.cdcproject.demo.util.IntentUtils;
import edu.gatech.cdcproject.demo.util.TimeUtils;

import static edu.gatech.cdcproject.demo.network.Api.myApi;
/**
 * Created by guoweidong on 3/30/16.
 */
public class CommunityDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_details);

        setupUI();
    }

    private void setupUI() {
        Long id = getIntent().getLongExtra(IntentUtils.INTENT_EXTRA_FOODIMAGE_ID, -1);
        if(id != -1) {
            new AsyncTask<Long, Void, FoodImage>() {
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
                }
            }.execute(id);
        }
    }


}
