package edu.gatech.cdcproject.demo.community;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import static edu.gatech.cdcproject.demo.util.LogUtils.*;

import edu.gatech.cdcproject.backend.myApi.model.FoodImage;
import edu.gatech.cdcproject.demo.R;
import edu.gatech.cdcproject.demo.util.IntentUtils;

/**
 * Created by guoweidong on 10/24/15.
 */
public class CommunityRecyclerViewAdapter extends RecyclerView.Adapter<CommunityRecyclerViewAdapter.ViewHolder> {

    public final static String TAG = makeLogTag(CommunityRecyclerViewAdapter.class);

    private List<FoodImage> foodImages;
    private Context context;

    public ProgressDialog progressDialog;

    public CommunityRecyclerViewAdapter(List<FoodImage> communityModels, Context context) {
        this.foodImages = communityModels;
        this.context = context;
    }

    public void clearObjects() {
        int size = this.foodImages.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                foodImages.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    public void addObjects(List<FoodImage> foodImages) {
        this.foodImages.addAll(foodImages);
        this.notifyItemRangeInserted(getItemCount() - foodImages.size(), foodImages.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_community, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        final FoodImage foodImage = foodImages.get(i);

        Glide.with(context)
                .load(foodImage.getImageUrl())
                .into(viewHolder.foodImg);
        viewHolder.titleTxt.setText(foodImage.getTitle());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommunityDetailsActivity.class);
                intent.putExtra(IntentUtils.INTENT_EXTRA_FOODIMAGE_ID, foodImage.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodImages == null ? 0 : foodImages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView foodImg;
        public TextView titleTxt;

        public ViewHolder(View itemView) {
            super(itemView);
            foodImg = (ImageView)itemView.findViewById(R.id.foodImg);
            titleTxt = (TextView) itemView.findViewById(R.id.titleTxt);
        }

    }
}
