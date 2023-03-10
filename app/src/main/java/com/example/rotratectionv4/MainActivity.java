package com.example.rotratectionv4;

import static androidx.fragment.app.FragmentManager.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.text.Layout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;

import com.example.rotratectionv4.R;
import com.example.rotratectionv4.utils.CameraProcess;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final CameraProcess cameraProcess = new CameraProcess();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = this;
        titleFontColor(context);
        RunAnimationTitleText();
        nextBtnLiveCam();

        // Apply for camera permission
        if (!cameraProcess.allPermissionsGranted(this)) {
            cameraProcess.requestPermissions(this);
        }

    }

    public void nextBtnLiveCam(){
        Button nextToLiveCam = findViewById(R.id.btn_liveDetect);

        nextToLiveCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LiveCam.class);
                startActivity(intent);
            }
        });

    }



    public void titleFontColor(Context context) {
        TextView titleText = (TextView) findViewById(R.id.txtView_title);

        titleText.post(new Runnable() {
            @Override
            public void run() {

                final Rect textBound = new Rect(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

                final Layout layout = titleText.getLayout();
                for(int i = 0; i < titleText.getLineCount(); i++) {
                    float left = layout.getLineLeft(i);
                    float right = layout.getLineRight(i);
                    if(left < textBound.left) textBound.left = (int)left;
                    if(right > textBound.right) textBound.right = (int)right;
                }
                textBound.top = layout.getLineTop(0);
                textBound.bottom = layout.getLineBottom(titleText.getLineCount() - 1);
                if(titleText.getIncludeFontPadding()) {
                    Paint.FontMetrics fontMetrics = titleText.getPaint().getFontMetrics();
                    textBound.top += (fontMetrics.ascent - fontMetrics.top);
                    textBound.bottom -= (fontMetrics.bottom - fontMetrics.descent);
                }

                double angleInLinear = Math.toDegrees(270);

                double r = Math.sqrt(Math.pow(textBound.bottom - textBound.top, 2) +
                        Math.pow(textBound.right - textBound.left, 2)) / 2;

                float centerX = textBound.left + (textBound.right - textBound.left) / 2;
                float centerY = textBound.top + (textBound.bottom - textBound.top) / 2;

                float startX = (float)Math.max(textBound.left, Math.min(textBound.right, centerX - r * Math.cos(angleInLinear)));
                float startY = (float)Math.min(textBound.bottom, Math.max(textBound.top, centerY - r * Math.sin(angleInLinear)));

                float endX = (float)Math.max(textBound.left, Math.min(textBound.right, centerX + r * Math.cos(angleInLinear)));
                float endY = (float)Math.min(textBound.bottom, Math.max(textBound.top, centerY + r * Math.sin(angleInLinear)));

                Shader textShader = new LinearGradient(startX, startY, endX, endY,
                        new int[]{context.getResources().getColor(R.color.goldenRod),
                                context.getResources().getColor(R.color.gold)},
                        new float[]{0, 1}, Shader.TileMode.CLAMP);

                titleText.setTextColor(Color.WHITE);
                titleText.getPaint().setShader(textShader);
            }
        });
    }

    private void RunAnimationTitleText()
    {
       /* Animation a = AnimationUtils.loadAnimation(this, R.anim.txtitleanim);
        a.reset();
        TextView tv = (TextView) findViewById(R.id.txtView_title);
        tv.clearAnimation();
        tv.startAnimation(a);*/

        // Get the TextView to be animated
        TextView textView = findViewById(R.id.txtView_title);

        ImageView logoView = findViewById(R.id.logoView);

        Button btnLiveDetect = findViewById(R.id.btn_liveDetect);

        Button btnModule = findViewById(R.id.btn_module);

// Get the original size of the TextView
        int originalWidth = textView.getWidth();
        int originalHeight = textView.getHeight();

// Define the animations
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 2.0f, 1.0f, 2.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);

// Set the fillAfter property to true to retain the final state of the animation
        scaleAnimation.setFillAfter(true);

// Apply the animation to the TextView
        textView.startAnimation(scaleAnimation);

// Define a new animation to return the TextView to its original size
        ScaleAnimation reverseScaleAnimation = new ScaleAnimation(2.0f, 1.0f, 2.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        reverseScaleAnimation.setDuration(1000);
        reverseScaleAnimation.setStartOffset(1000); // Add a delay of 1000 milliseconds

// Set the fillBefore property to true to start the reverse animation from the initial state of the TextView
        reverseScaleAnimation.setFillBefore(true);

// Apply the reverse animation to the TextView
        textView.startAnimation(reverseScaleAnimation);

// Reset the size of the TextView after the animation is complete
        textView.layout(textView.getLeft(), textView.getTop(), textView.getLeft() + originalWidth, textView.getTop() + originalHeight);

        // Define a new animation to fade in the ImageView
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setStartOffset(2000); // Add a delay of 2000 milliseconds

// Set the fillBefore property to true to start the animation from the initial state of the ImageView
        alphaAnimation.setFillBefore(true);

// Apply the animation to the ImageView
        logoView.startAnimation(alphaAnimation);
        btnLiveDetect.startAnimation(alphaAnimation);
        btnModule.startAnimation(alphaAnimation);

// Reset the size of the TextView after the animation is complete
        textView.layout(textView.getLeft(), textView.getTop(), textView.getLeft() + originalWidth, textView.getTop() + originalHeight);
    }



}