package edu.gatech.cdcproject.demo.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import edu.gatech.cdcproject.demo.network.Api;

import static edu.gatech.cdcproject.demo.util.LogUtils.*;
/**
 * Created by guoweidong on 2/18/16.
 */
public class ImageUploader {
    private static final String TAG = makeLogTag(ImageUploader.class);
    public static class ImageInfo {
        public String imageKey;
        public String imageURL;
    }

    private static final String LINE_FEED = "\r\n";


    public static ImageInfo upload(Drawable drawable) {
        try {
            // Transform the Image to HttpEntity.
            byte[] imageBytes = ImageConvertor.drawableToByteArray(drawable);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            HttpEntity httpEntity =  builder.addBinaryBody("image", imageBytes
                    , ContentType.create("image/jpeg"), "image").build();

            // Get URL where the image will be uploaded
            String url = Api.getClient().image().newImageUrl().execute().getStringResponse();
            HttpPost httpPost = new HttpPost(url);
            // Execute the http post
            httpPost.setEntity(httpEntity);

            // Process httpResponse
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(httpPost);
            LOGD(TAG, "response " + response.getStatusLine());
            InputStream in = response.getEntity().getContent();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in));
            String line;
            StringBuffer sb = new StringBuffer();
            while((line = rd.readLine()) != null) {
                sb.append(line);
                sb.append('\r');
            }
            rd.close();
            JSONObject jsonObject = new JSONObject(sb.toString());
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.imageKey = (String)jsonObject.get("imageKey");
            imageInfo.imageURL = (String)jsonObject.get("imageURL");
            return imageInfo;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ImageInfo upload(Bitmap bitmap) {
        try {
            // Transform the Image to HttpEntity.
            byte[] imageBytes = ImageConvertor.bitmapToByteArray(bitmap);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            HttpEntity httpEntity =  builder.addBinaryBody("image", imageBytes
                    , ContentType.create("image/jpeg"), "image").build();

            // Get URL where the image will be uploaded
            String url = Api.getClient().image().newImageUrl().execute().getStringResponse();
            LOGI(TAG, "URL is " + url);
            LOGI(TAG, "Image size is " + imageBytes.length);
            HttpPost httpPost = new HttpPost(url);
            // Execute the http post
            httpPost.setEntity(httpEntity);

            // Process httpResponse
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(httpPost);
            LOGI(TAG, "response " + response.getStatusLine());
            InputStream in = response.getEntity().getContent();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in));
            String line;
            StringBuffer sb = new StringBuffer();
            while((line = rd.readLine()) != null) {
                sb.append(line);
                sb.append('\r');
            }
            rd.close();
            JSONObject jsonObject = new JSONObject(sb.toString());
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.imageKey = (String)jsonObject.get("imageKey");
            imageInfo.imageURL = (String)jsonObject.get("imageURL");
            return imageInfo;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
