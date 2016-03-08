package com.clarifai.androidstarter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.R.drawable;


import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;
import com.clarifai.api.exception.ClarifaiException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.provider.MediaStore.Images.Media;


//This is the food identify activity, called by main activity.
public class RecognitionActivity extends Activity {
  private static final String TAG = RecognitionActivity.class.getSimpleName();

  private static final int CODE_PICK = 1111;
  private static final int CODE_CAM=1112;

  private final ClarifaiClient client = new ClarifaiClient(Credentials.CLIENT_ID,
      Credentials.CLIENT_SECRET);
  private Button selectButton;
  private Button cameraButton;
  private Button confirmButton;
  private ImageView imageView;
  private TextView textView;
  private TableLayout button_view;


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recognition);


    button_view=(TableLayout) findViewById(R.id.button_view);
    imageView = (ImageView) findViewById(R.id.image_view);
    textView = (TextView) findViewById(R.id.text_view);
    selectButton = (Button) findViewById(R.id.select_button);
    cameraButton=(Button) findViewById(R.id.camera_button);
    confirmButton=(Button) findViewById(R.id.confirm_button);






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
            new View.OnClickListener()
            {
              public void onClick(View v)
              {
                finish();
              }
            }
    );



    Intent receive_intent=getIntent();
  }








  @Override protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
    if (requestCode == CODE_PICK && resultCode == RESULT_OK)
    {
      // The user picked an image. Send it to Clarifai for recognition.
      Log.d(TAG, "User picked image: " + intent.getData());
      Bitmap bitmap = loadBitmapFromUri(intent.getData());
      if (bitmap != null) {
        imageView.setImageBitmap(bitmap);
        textView.setText("Recognizing...");
        selectButton.setEnabled(false);

        // Run recognition on a background thread since it makes a network call.
        new AsyncTask<Bitmap, Void, RecognitionResult>() {
          @Override protected RecognitionResult doInBackground(Bitmap... bitmaps) {
            return recognizeBitmap(bitmaps[0]);
          }
          @Override protected void onPostExecute(RecognitionResult result) {
            updateUIForResult(result);
          }
        }.execute(bitmap);
      } else {
        textView.setText("Unable to load selected image.");

      }
    }
    else if(requestCode==CODE_CAM&&resultCode==RESULT_OK)
    {
      Bitmap bp=(Bitmap) intent.getExtras().get("data");
      imageView.setImageBitmap(bp);
      new AsyncTask<Bitmap, Void, RecognitionResult>() {
        @Override protected RecognitionResult doInBackground(Bitmap... bitmaps) {
          return recognizeBitmap(bitmaps[0]);
        }
        @Override protected void onPostExecute(RecognitionResult result) {
          updateUIForResult(result);
        }
      }.execute(bp);
    }
  }

  /** Loads a Bitmap from a content URI returned by the media picker. */
  private Bitmap loadBitmapFromUri(Uri uri) {
    try {
      // The image may be large. Load an image that is sized for display. This follows best
      // practices from http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
      BitmapFactory.Options opts = new BitmapFactory.Options();
      opts.inJustDecodeBounds = true;
      BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, opts);
      int sampleSize = 1;
      while (opts.outWidth / (2 * sampleSize) >= imageView.getWidth() &&
             opts.outHeight / (2 * sampleSize) >= imageView.getHeight()) {
        sampleSize *= 2;
      }

      opts = new BitmapFactory.Options();
      opts.inSampleSize = sampleSize;
      return BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, opts);
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
        ArrayList<Button> mybutton=new ArrayList<Button>();
        StringBuilder b = new StringBuilder();
        int i=0;
        TableRow tr=new TableRow(this);
        for (Tag tag : result.getTags()) {
          b.append(b.length() > 0 ? ", " : "").append(tag.getName());
          if(i==0)
          {
            tr=new TableRow(this);
          }
          final Button temp_buton = new Button(this);
          temp_buton.setText(tag.getName());
          temp_buton.setTextSize(12);
          Drawable close_icon=getDrawable(drawable.ic_delete);
          close_icon.setBounds(0,0,40,40);
          temp_buton.setCompoundDrawables(null,null,close_icon,null );

          temp_buton.setOnClickListener(
                  new View.OnClickListener()
                  {
                    public void onClick(View v)
                    {
                      temp_buton.setVisibility(View.GONE);
                    }
                  }
          );


          tr.addView(temp_buton);
          mybutton.add(temp_buton);
          i++;
          if(i==3) {
            button_view.addView(tr);
            i=0;
          }
        }

        textView.setText("Success");
      }
      else
      {
        Log.e(TAG, "Clarifai: " + result.getStatusMessage());
        textView.setText("Sorry, there was an error recognizing your image.");
      }
    }
    else
    {
      textView.setText("Sorry, there was an error recognizing your image.");
    }
    selectButton.setEnabled(true);
  }
}
