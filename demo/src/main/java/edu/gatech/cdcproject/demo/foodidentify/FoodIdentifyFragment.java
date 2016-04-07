package edu.gatech.cdcproject.demo.foodidentify;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Credentials;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;
import com.clarifai.api.exception.ClarifaiException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import edu.gatech.cdcproject.backend.myApi.model.FoodImage;
import edu.gatech.cdcproject.demo.BuildConfig;
import edu.gatech.cdcproject.demo.R;

/**
 * Created by guoweidong on 3/28/16.
 */
public class FoodIdentifyFragment extends Fragment {


    private static final String TAG = FoodIdentifyFragment.class.getSimpleName();

    private static final int CODE_PICK = 1111;
    private static final int CODE_CAM=1112;

    private final ClarifaiClient client = new ClarifaiClient(BuildConfig.CLARIFAI_CLIENT_ID,
            BuildConfig.CLARIFAI_CLIENT_SECRET);
    private Button selectButton;
    private Button cameraButton;
    private Button confirmButton;
    private Button enterTextButton;
    private ImageView imageView;

    //private LinearLayout button_view;
    private ListView button_view;
    private ArrayAdapter adapter;

    private ArrayList<String> food = new ArrayList<String>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_identify, container, false);

        button_view=(ListView) view.findViewById(R.id.button_view);


        imageView = (ImageView) view.findViewById(R.id.image_view);
        selectButton = (Button) view.findViewById(R.id.select_button);
        cameraButton=(Button) view.findViewById(R.id.camera_button);
        confirmButton=(Button) view.findViewById(R.id.confirm_button);
        enterTextButton=(Button) view.findViewById(R.id.popup_menu);
        adapter = new ArrayAdapter<String>(getContext(), R.layout.activity_listview,food);
        button_view.setAdapter(adapter);
        button_view.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        food.remove(((TextView) view).getText().toString().toLowerCase());
                        adapter.notifyDataSetChanged();
                    }
                });


        selectButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Send an intent to launch the media picker.;
                        final Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), CODE_PICK);
                    }
                }
        );

        cameraButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent cam_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cam_intent, CODE_CAM);
                    }
                }
        );

        confirmButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent confirm_intent = new Intent(getActivity(), confirm_page.class);
                        confirm_intent.putStringArrayListExtra("data", food);
                        startActivity(confirm_intent);
                    }
                }
        );

        enterTextButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        alert_dialog();
                    }
                }
        );

        return view;
    }



    private void alert_dialog()
    {

        AlertDialog.Builder alert=new AlertDialog.Builder(getActivity());
        final EditText edittext=new EditText(getActivity());
        alert.setMessage("Enter Your Food");
        alert.setTitle("Food name");
        alert.setView(edittext);
        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String enter_value = edittext.getText().toString();
                if(!Check_Appear(enter_value)) {

                    food.add(0, enter_value.toLowerCase());
                    adapter.notifyDataSetChanged();

                }
                else
                    Toast.makeText(getContext(), "Already exists", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }
/*
  private void updateUIForUserEnter(final String result) {
    if (!result.equals("1")&&!result.equals("2")&&!result.equals(""))
    {
      if(!Check_Appear(food.get(result))) {
        TableRow tr = new TableRow(this);
        final Button temp_button = new Button(this);
        temp_button.setText(food.get(result));
        temp_button.setTextSize(12);
        Drawable close_icon = getDrawable(drawable.ic_delete);
        close_icon.setBounds(0, 0, 40, 40);
        temp_button.setCompoundDrawables(null, null, close_icon, null);
        temp_button.setOnClickListener(
                new View.OnClickListener() {
                  public void onClick(View v) {
                    temp_button.setVisibility(View.GONE);
                    food.remove(result);
                  }
                }
        );
        tr.addView(temp_button);
        buttons.add(temp_button);
        button_view.addView(tr);
      }
  }
    else if(result.equals("1"))
      Toast.makeText(getApplicationContext(),"Already exists",Toast.LENGTH_LONG).show();
    else if(result.equals("2"))
      Toast.makeText(getApplicationContext(),"Can not recognize this food",Toast.LENGTH_LONG).show();


  }
*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == CODE_PICK && resultCode == Activity.RESULT_OK)
        {
            // The user picked an image. Send it to Clarifai for recognition.
            Log.d(TAG, "User picked image: " + intent.getData());
            Bitmap bitmap = loadBitmapFromUri(intent.getData());
            if (bitmap != null)
            {
                imageView.setImageBitmap(bitmap);
                Toast.makeText(getContext(),"Recognizing...",Toast.LENGTH_SHORT).show();
                selectButton.setEnabled(false);

                // Run recognition on a background thread since it makes a network call.
                new AsyncTask<Bitmap, Void, RecognitionResult>()
                {
                    @Override protected RecognitionResult doInBackground(Bitmap... bitmaps)
                    {
                        RecognitionResult result= recognizeBitmap(bitmaps[0]);
                        return result;
                    }
                    @Override protected void onPostExecute(RecognitionResult result)
                    {
                        updateUIForResult(result);
                    }
                }.execute(bitmap);
            } else {
                Toast.makeText(getContext(),"Unable to load selected image.",Toast.LENGTH_LONG).show();
            }
        }
        else if(requestCode==CODE_CAM&&resultCode==Activity.RESULT_OK)
        {
            Bitmap bp=(Bitmap) intent.getExtras().get("data");
            if(bp!=null) {
                imageView.setImageBitmap(bp);
                Toast.makeText(getContext(), "Recognizing...", Toast.LENGTH_SHORT).show();
                cameraButton.setEnabled(false);
                new AsyncTask<Bitmap, Void, RecognitionResult>() {
                    @Override
                    protected RecognitionResult doInBackground(Bitmap... bitmaps) {

                        RecognitionResult result = recognizeBitmap(bitmaps[0]);
                        return result;
                    }
                    @Override
                    protected void onPostExecute(RecognitionResult result) {
                        updateUIForResult(result);
                    }
                }.execute(bp);
            }
            else
                Toast.makeText(getContext(),"Unable to load image.",Toast.LENGTH_LONG).show();
        }
    }

    /** Loads a Bitmap from a content URI returned by the media picker. */
    private Bitmap loadBitmapFromUri(Uri uri) {
        try {
            // The image may be large. Load an image that is sized for display. This follows best
            // practices from http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri), null, opts);
            int sampleSize = 1;
            while (opts.outWidth / (2 * sampleSize) >= imageView.getWidth() &&
                    opts.outHeight / (2 * sampleSize) >= imageView.getHeight()) {
                sampleSize *= 2;
            }

            opts = new BitmapFactory.Options();
            opts.inSampleSize = sampleSize;
            return BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri), null, opts);
        } catch (IOException e) {
            Log.e(TAG, "Error loading image: " + uri, e);
        }
        return null;
    }

    /** Sends the given bitmap to Clarifai for recognition and returns the result. */
    private RecognitionResult recognizeBitmap(Bitmap bitmap) {
        try {
            // Scale down the image. This step is optional. However, sending large images over the
            // network is slow and  does not significantly improve recognition performance.
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 320,
                    320 * bitmap.getHeight() / bitmap.getWidth(), true);

            // Compress the image as a JPEG.
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            scaled.compress(Bitmap.CompressFormat.JPEG, 90, out);
            byte[] jpeg = out.toByteArray();

            // Send the JPEG to Clarifai and return the result.
            return client.recognize(new RecognitionRequest(jpeg)).get(0);
        } catch (ClarifaiException e) {
            Log.e(TAG, "Clarifai error", e);
            return null;
        }
    }

    /** Updates the UI by displaying tags for the given result. */
    private void updateUIForResult(RecognitionResult result) {
        if (result != null)
        {
            if (result.getStatusCode() == RecognitionResult.StatusCode.OK)
            {
                for (Tag t : result.getTags())
                {
                    if(Check_Appear(t.getName()))
                        continue;

                    food.add(0,t.getName().toLowerCase());
                    adapter.notifyDataSetChanged();
                }
                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Log.e(TAG, "Clarifai: " + result.getStatusMessage());
                Toast.makeText(getContext(),"Sorry, there was an error recognizing your image.",Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(getContext(),"Sorry, there was an error recognizing your image.",Toast.LENGTH_LONG).show();
        }
        selectButton.setEnabled(true);
        cameraButton.setEnabled(true);

    }

    private boolean Check_Appear(String s)
    {
        for(String x:food)
        {
            if(x.toLowerCase().equals(s.toLowerCase()))
                return true;
        }
        return false;
    }

/*
  private void populateText()
  {
    Display display = getWindowManager().getDefaultDisplay();
    button_view.removeAllViews();
    Point size=new Point();
    display.getSize(size);
    int maxWidth =size.x  - 15;

    LinearLayout newLL = new LinearLayout(RecognitionActivity.this);
    newLL.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT));
    newLL.setGravity(Gravity.LEFT);
    newLL.setOrientation(LinearLayout.HORIZONTAL);

    int widthSoFar = 0;

    for (int i = 0 ; i < buttons.size() ; i++ )
    {
      buttons.get(i).measure(0, 0);
      widthSoFar += buttons.get(i).getMeasuredWidth();

      ViewGroup parent = (ViewGroup) buttons.get(i).getParent();
      if(parent!=null)
        parent.removeView(buttons.get(i));

      if(widthSoFar<=maxWidth) {
        newLL.addView(buttons.get(i));
      }
      else
      {
        button_view.addView(newLL);
        newLL = new LinearLayout(RecognitionActivity.this);
        newLL.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        newLL.setOrientation(LinearLayout.HORIZONTAL);
        newLL.setGravity(Gravity.LEFT);
        widthSoFar=0;
      }

    }
  }
*/
}
