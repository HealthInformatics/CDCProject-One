package edu.gatech.cdcproject.demo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import biz.source_code.base64Coder.Base64Coder;
import static edu.gatech.cdcproject.demo.util.LogUtils.*;
/**
 * Created by guoweidong on 11/24/15.
 */
public class ImageConvertor {
    public final static String TAG = makeLogTag(ImageConvertor.class);
    public final static int DEFAULT_QUALITY = 100;

    /**
     * Convert Drawable to String. Normally used in Google Datastore in which
     * Blob type is projected as String type in Client.
     * @param drawable
     * @return
     */
    public static String drawableToString(Drawable drawable) {
        byte[] data = drawableToByteArray(drawable);
        StringBuffer out = new StringBuffer();
        out.append(Base64Coder.encode(data, 0, data.length));
        LOGI(TAG, "drawableToString data length: " + data.length);
        return out.toString();
    }

    public static byte[] drawableToByteArray(Drawable drawable) {
        return bitmapToByteArray(drawableToBitmap(drawable));
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        return ((BitmapDrawable)drawable).getBitmap();
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, DEFAULT_QUALITY, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static Drawable stringToDrawable(Context context, String image) {
        byte[] data = Base64Coder.decode(image);
        return byteArrayToDrawable(context, data);
    }

    public static Drawable byteArrayToDrawable(Context context, byte[] data) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        return bitmapToDrawable(context, bitmap);
    }

    public static Drawable bitmapToDrawable(Context context, Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(), bitmap);
    }

}
