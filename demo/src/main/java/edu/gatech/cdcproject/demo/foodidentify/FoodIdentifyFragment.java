package edu.gatech.cdcproject.demo.foodidentify;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.gatech.cdcproject.demo.R;

/**
 * Created by guoweidong on 3/28/16.
 */
public class FoodIdentifyFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_food_identify, container, false);
    }
}
