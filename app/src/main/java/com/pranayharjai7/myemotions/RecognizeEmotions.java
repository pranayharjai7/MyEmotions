package com.pranayharjai7.myemotions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.pranayharjai7.myemotions.mtcnn.Box;
import com.pranayharjai7.myemotions.mtcnn.EmotionPyTorchClassifier;
import com.pranayharjai7.myemotions.mtcnn.MTCNN;

import java.util.Vector;

public class RecognizeEmotions {

    private MTCNN mtcnnFaceDetector = null;
    private EmotionPyTorchClassifier emotionClassifierPyTorch = null;
    private static int minFaceSize = 32;
    private Context applicationContext;

    public RecognizeEmotions(Context applicationContext) {
        this.applicationContext = applicationContext;
        init();
    }

    private void init() {
        try {
            emotionClassifierPyTorch = new EmotionPyTorchClassifier(applicationContext);
        } catch (final Exception e) {
            Log.e("abc", "Exception initializing EmotionPyTorchClassifier!", e);
        }
        try {
            emotionClassifierPyTorch = new EmotionPyTorchClassifier(applicationContext);
            mtcnnFaceDetector = new MTCNN(applicationContext);
        } catch (final Exception e) {
            Log.e("abc", "Exception initializing MTCNNModel!" + e);
        }
    }

    public Bitmap recognizeEmotions(Bitmap sampledImage) {
        return mtcnnDetectionAndEmotionPyTorchRecognition(sampledImage);
    }

    private Bitmap mtcnnDetectionAndEmotionPyTorchRecognition(Bitmap sampledImage) {

        Bitmap bmp = sampledImage;
        Bitmap resizedBitmap = bmp;
        double minSize = 600.0;
        double scale = Math.min(bmp.getWidth(), bmp.getHeight()) / minSize;
        if (scale > 1.0) {
            resizedBitmap = Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth() / scale), (int) (bmp.getHeight() / scale), false);
            bmp = resizedBitmap;
        }
        long startTime = SystemClock.uptimeMillis();
        Vector<Box> bboxes = mtcnnFaceDetector.detectFaces(resizedBitmap, minFaceSize);//(int)(bmp.getWidth()*MIN_FACE_SIZE));

        Bitmap tempBmp = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(tempBmp);
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setAntiAlias(true);
        p.setFilterBitmap(true);
        p.setDither(true);
        p.setColor(Color.BLUE);
        p.setStrokeWidth(5);

        Paint p_text = new Paint();
        p_text.setColor(Color.WHITE);
        p_text.setStyle(Paint.Style.FILL);
        p_text.setColor(Color.BLUE);
        p_text.setTextSize(24);

        c.drawBitmap(bmp, 0, 0, null);

        for (Box box : bboxes) {
            Rect bbox = box.transform2Rect();//new android.graphics.Rect(Math.max(0,box.left()),Math.max(0,box.top()),box.right(),box.bottom());
            p.setColor(Color.RED);
            c.drawRect(bbox, p);
            if (emotionClassifierPyTorch != null && bbox.width() > 0 && bbox.height() > 0) {
                int w = bmp.getWidth();
                int h = bmp.getHeight();
                Rect bboxOrig = new android.graphics.Rect(
                        Math.max(0, w * bbox.left / resizedBitmap.getWidth()),
                        Math.max(0, h * bbox.top / resizedBitmap.getHeight()),
                        Math.min(w, w * bbox.right / resizedBitmap.getWidth()),
                        Math.min(h, h * bbox.bottom / resizedBitmap.getHeight())
                );
                Bitmap faceBitmap = Bitmap.createBitmap(bmp, bboxOrig.left, bboxOrig.top, bboxOrig.width(), bboxOrig.height());
                String resultEmotion = emotionClassifierPyTorch.recognize(faceBitmap);
                c.drawText(resultEmotion, Math.max(0, bbox.left), Math.max(0, bbox.top - 20), p_text);
                Toast.makeText(applicationContext, resultEmotion, Toast.LENGTH_SHORT).show();
            }
        }
        return tempBmp;
    }
}
