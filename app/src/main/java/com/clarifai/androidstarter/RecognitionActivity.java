package com.clarifai.androidstarter;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.EditText;
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
import android.util.Pair;
import android.content.DialogInterface;
import android.widget.Toast;
import android.view.Gravity;
import android.view.Display;
import android.content.Context;
import android.graphics.Point;
import android.widget.ListView;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;
import com.clarifai.api.exception.ClarifaiException;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;



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
  private Button enterTextButton;
  private ImageView imageView;

  //private LinearLayout button_view;
  private ListView button_view;
  private ArrayAdapter adapter;

  private ArrayList<String> food = new ArrayList<String>();


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recognition);

    button_view=(ListView) findViewById(R.id.button_view);


    imageView = (ImageView) findViewById(R.id.image_view);
    selectButton = (Button) findViewById(R.id.select_button);
    cameraButton=(Button) findViewById(R.id.camera_button);
    confirmButton=(Button) findViewById(R.id.confirm_button);
    enterTextButton=(Button) findViewById(R.id.popup_menu);

    adapter = new ArrayAdapter<String>(this, R.layout.activity_listview,food);
    button_view.setAdapter(adapter);
    button_view.setOnItemClickListener(
            new OnItemClickListener()
            {
              public void onItemClick(AdapterView<?> parent, View view, int position, long id)
              {
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
                Intent confirm_intent = new Intent(RecognitionActivity.this, confirm_page.class);
                confirm_intent.putStringArrayListExtra("data",food);
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
    Intent receive_intent=getIntent();
  }



  private void alert_dialog()
  {

    AlertDialog.Builder alert=new AlertDialog.Builder(this);
    final EditText edittext=new EditText(RecognitionActivity.this);
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
          Toast.makeText(getApplicationContext(),"Already exists",Toast.LENGTH_SHORT).show();
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

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
    if (requestCode == CODE_PICK && resultCode == RESULT_OK)
    {
      // The user picked an image. Send it to Clarifai for recognition.
      Log.d(TAG, "User picked image: " + intent.getData());
      Bitmap bitmap = loadBitmapFromUri(intent.getData());
      if (bitmap != null)
      {
        imageView.setImageBitmap(bitmap);
        Toast.makeText(getApplicationContext(),"Recognizing...",Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getApplicationContext(),"Unable to load selected image.",Toast.LENGTH_LONG).show();
      }
    }
    else if(requestCode==CODE_CAM&&resultCode==RESULT_OK)
    {
      Bitmap bp=(Bitmap) intent.getExtras().get("data");
      if(bp!=null) {
        imageView.setImageBitmap(bp);
        Toast.makeText(getApplicationContext(), "Recognizing...", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getApplicationContext(),"Unable to load image.",Toast.LENGTH_LONG).show();
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
          for (Tag t : result.getTags())
          {
            if(Check_Appear(t.getName()))
              continue;

            food.add(0,t.getName().toLowerCase());
            adapter.notifyDataSetChanged();
          }
          Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
      }
      else
      {
        Log.e(TAG, "Clarifai: " + result.getStatusMessage());
        Toast.makeText(getApplicationContext(),"Sorry, there was an error recognizing your image.",Toast.LENGTH_LONG).show();
      }
    }
    else
    {
      Toast.makeText(getApplicationContext(),"Sorry, there was an error recognizing your image.",Toast.LENGTH_LONG).show();
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
