package edu.gatech.cdcproject.demo.community;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fasterxml.jackson.core.format.DataFormatDetector;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.gatech.cdcproject.backend.myApi.model.CollectionResponseFoodImage;
import edu.gatech.cdcproject.backend.myApi.model.FoodImage;
import edu.gatech.cdcproject.demo.R;
import edu.gatech.cdcproject.demo.network.Api;
import edu.gatech.cdcproject.demo.ui.MainActivity;
import edu.gatech.cdcproject.demo.util.ImageUploader;

import static edu.gatech.cdcproject.demo.util.LogUtils.*;
import static edu.gatech.cdcproject.demo.network.Api.myApi;
/**
 * Created by guoweidong on 3/28/16.
 */
public class CommunityFragment extends Fragment implements SwipyRefreshLayout.OnRefreshListener, View.OnClickListener{
    private static final String TAG = makeLogTag(CommunityFragment.class);

    private static final int REQUEST_CODE_OPEN_CAMERA = 201;
    private static final int REQUEST_CODE_PHOTO = 202;


    private SwipyRefreshLayout swipyRefreshLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FloatingActionButton plusFab, cameraFab, photoFab;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;

    private boolean isFabOpen;

    private CommunityRecyclerViewAdapter adapter;
    private String cursor;

    private String imageName;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        setupUI(view);

        updateObjects();

        return view;
    }

    private void setupUI(View view) {
        //Animation
        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_backward);
        //SwipyRefreshLayout
        swipyRefreshLayout = (SwipyRefreshLayout)view.findViewById(R.id.swipyRefreshLayout);
        swipyRefreshLayout.setOnRefreshListener(this);
        //Progressbar
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        //FAB
        plusFab = (FloatingActionButton)view.findViewById(R.id.plusFab);
        cameraFab = (FloatingActionButton)view.findViewById(R.id.cameraFab);
        photoFab = (FloatingActionButton)view.findViewById(R.id.photoFab);
        plusFab.setOnClickListener(this);
        cameraFab.setOnClickListener(this);
        photoFab.setOnClickListener(this);
        //RecyclerView
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CommunityRecyclerViewAdapter(new
                ArrayList<FoodImage>(), getContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        if(direction == SwipyRefreshLayoutDirection.TOP) {
            updateObjects();
        } else if(direction == SwipyRefreshLayoutDirection.BOTTOM) {
            appendObjects();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.plusFab:
                animateFAB();
                break;
            case R.id.cameraFab:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_CODE_OPEN_CAMERA);
                }
                break;
            case R.id.photoFab:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select " +
                        "Picture"), REQUEST_CODE_PHOTO);
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LOGI(TAG, "resultCode: " + requestCode);
        if (requestCode == REQUEST_CODE_OPEN_CAMERA) {
            LOGI(TAG, "resultCode: " + (resultCode == Activity.RESULT_OK));
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                Drawable drawable = new BitmapDrawable(getResources(), imageBitmap);
                startAddTitleDialog(drawable);
            }
        } else if (requestCode == REQUEST_CODE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    LOGI(TAG, "data is empty");
                }
                try {
                    InputStream in = getActivity().getContentResolver().openInputStream
                            (data.getData());
                    Drawable drawable = BitmapDrawable.createFromStream(in,
                            "image");
                    startAddTitleDialog(drawable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startAddTitleDialog(final Drawable drawable) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Please add a title to the photo.");

        // Set up the input
        final EditText input = new EditText(getContext());
        input.setHint("Title should be at least 3 characters.");
        // Specify the type of input expected;
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String content = input.getText().toString();
                if(content.trim().length() < 3) {
                    Toast.makeText(getContext(), "Title should be at least 3 characters.", Toast.LENGTH_SHORT).show();
                    imageName = null;
                    return;
                }
                imageName = content;
                new AddImageTask().execute(drawable);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void animateFAB(){

        if (isFabOpen){

            plusFab.startAnimation(rotate_backward);
            cameraFab.startAnimation(fab_close);
            photoFab.startAnimation(fab_close);
            cameraFab.setClickable(false);
            photoFab.setClickable(false);
            isFabOpen = false;
            LOGI(TAG, "FAB close");

        } else {

            plusFab.startAnimation(rotate_forward);
            cameraFab.startAnimation(fab_open);
            photoFab.startAnimation(fab_open);
            cameraFab.setClickable(true);
            photoFab.setClickable(true);
            isFabOpen = true;
            LOGI(TAG,"FAB open");

        }
    }

    private void updateObjects() {
        new InitializeObjectsTask().execute();
    }

    private void appendObjects() {
        new AppendObjectsTask().execute(cursor);
    }

    private class InitializeObjectsTask extends AsyncTask<Void, Void, CollectionResponseFoodImage> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            swipyRefreshLayout.setEnabled(false);
            adapter.clearObjects();
        }

        @Override
        protected CollectionResponseFoodImage doInBackground(Void... params) {
            try {
                return myApi().image().list().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(CollectionResponseFoodImage param) {
            //handle visibility
            super.onPostExecute(param);
            if(param != null && param.getItems() != null) {
                adapter.addObjects(param.getItems());
                cursor = param.getNextPageToken();
            }
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            swipyRefreshLayout.setEnabled(true);
            swipyRefreshLayout.setRefreshing(false);
        }
    }

    private class AppendObjectsTask extends AsyncTask<String, Void, CollectionResponseFoodImage> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipyRefreshLayout.setEnabled(false);
        }

        @Override
        protected CollectionResponseFoodImage doInBackground(String... params) {
            try {
                return myApi().image().list().setCursor(cursor).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(CollectionResponseFoodImage param) {
            //handle visibility
            if(param != null && param.getItems() != null) {
                adapter.addObjects(param.getItems());
                cursor = param.getNextPageToken();
            }
            swipyRefreshLayout.setEnabled(true);
            swipyRefreshLayout.setRefreshing(false);
            //set data for list

        }
    }

    private class AddImageTask extends AsyncTask<Drawable, Void, Void> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Upload");
            progressDialog.setMessage("Please wait for photo uploading...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Drawable... params) {
            try {
                FoodImage foodImage = new FoodImage();
                foodImage.setTitle(imageName);

                ImageUploader.ImageInfo imageInfo = ImageUploader.upload(params[0]);
                foodImage.setImageKey(imageInfo.imageKey);
                foodImage.setImageUrl(imageInfo.imageURL);

                myApi().image().insert(foodImage)
                        .execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void collectionResponseReport) {
            //handle visibility
            progressDialog.dismiss();
        }

    }



}
