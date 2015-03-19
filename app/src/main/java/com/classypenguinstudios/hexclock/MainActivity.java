package com.classypenguinstudios.hexclock;

import android.animation.Animator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.w3c.dom.Text;

import java.util.Calendar;


public class MainActivity extends Activity {

    private static long lastTime;
    private String[] textAbout = new String[]{" About HexClock \n", "HexClock was orignially written by Jacopo Colo \n The time is taken as hex color representation"};
    private boolean isClicked = false;
    Thread updateTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextSwitcher timeTS = (TextSwitcher) findViewById(R.id.TStime);
        final TextSwitcher aboutTS = (TextSwitcher) findViewById(R.id.TSabout);
        final RelativeLayout mainRL = (RelativeLayout) findViewById(R.id.RLmain);

        getActionBar().setElevation(0);
        getActionBar().setDisplayShowTitleEnabled(false);

        final Typeface chamAndLimo = Typeface.createFromAsset(getAssets(), "chamandlimo.ttf");

        aboutTS.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                TextView newText = new TextView(MainActivity.this);
                newText.setWidth(aboutTS.getWidth());
                newText.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
                newText.setTextSize(16);
                newText.setPadding(12,12,12,12);
                newText.setTextColor(Color.WHITE);
                newText.setTypeface(chamAndLimo);
                return newText;
            }
        });

        timeTS.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                TextView newText = new TextView(MainActivity.this);
                newText.setGravity(Gravity.CENTER);
                newText.setTextSize(32);
                newText.setPadding(12, 12, 12, 12);
                newText.setTextColor(Color.WHITE);
                newText.setTypeface(chamAndLimo);
                return newText;
            }
        });

        Animation inAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation outAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        aboutTS.setInAnimation(inAnim);
        aboutTS.setOutAnimation(outAnim);
        timeTS.setInAnimation(inAnim);
        timeTS.setOutAnimation(outAnim);
        aboutTS.setText(textAbout[0]);

        aboutTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClicked){
                    aboutTS.setText(textAbout[0]);
                } else {
                    aboutTS.setText(textAbout[1]);
                }
                isClicked = !isClicked;
            }
        });

        updateTimer = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateColor(timeTS, mainRL);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        updateTimer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updateColor(TextSwitcher timeTS, RelativeLayout mainRL){
        final Calendar currentCal = Calendar.getInstance();
        final long time = currentCal.get(Calendar.SECOND) + currentCal.get(Calendar.MINUTE) * 100 + currentCal.get(Calendar.HOUR_OF_DAY) * 10000;
        final int intColor = Color.parseColor(getColorString(time));
        final Window currentWindow = this.getWindow();
        timeTS.setText(getColorString(time));
        mainRL.setBackgroundColor(intColor);
        currentWindow.setNavigationBarColor(intColor);
        currentWindow.setStatusBarColor(intColor);
        getActionBar().setBackgroundDrawable(new ColorDrawable(intColor));
    }

    private String getColorString(long time){
        return "#" + String.format("%06d",time);
    }
}
