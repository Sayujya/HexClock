package com.classypenguinstudios.hexclock;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.github.adnansm.timelytextview.TimelyView;

import java.util.Calendar;


public class MainActivity extends Activity {

    private static long lastTime;
    Thread updateTimer;
    private String[] textAbout = new String[]{" About HexClock \n", "HexClock was orignially written by Jacopo Colo \n The time is taken as hex color representation"};
    private boolean isClicked = false;
    private int intColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TimelyView timeDigit1TTV = (TimelyView) findViewById(R.id.TTVdigit1);
        final TimelyView timeDigit2TTV = (TimelyView) findViewById(R.id.TTVdigit2);
        final TimelyView timeDigit3TTV = (TimelyView) findViewById(R.id.TTVdigit3);
        final TimelyView timeDigit4TTV = (TimelyView) findViewById(R.id.TTVdigit4);
        final TimelyView timeDigit5TTV = (TimelyView) findViewById(R.id.TTVdigit5);
        final TimelyView timeDigit6TTV = (TimelyView) findViewById(R.id.TTVdigit6);
        final TimelyView[] timelyViews = {timeDigit1TTV, timeDigit2TTV, timeDigit3TTV, timeDigit4TTV, timeDigit5TTV, timeDigit6TTV};
        final TextSwitcher aboutTS = (TextSwitcher) findViewById(R.id.TSabout);
        final RelativeLayout mainRL = (RelativeLayout) findViewById(R.id.RLmain);
        final Typeface chamAndLimo = Typeface.createFromAsset(getAssets(), "chamandlimos.ttf");

        firstSet(timelyViews, mainRL);
        getActionBar().setElevation(0);
        getActionBar().setDisplayShowTitleEnabled(false);


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

        Animation inAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation outAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        aboutTS.setInAnimation(inAnim);
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
                                updateColorByTime(timelyViews, mainRL);
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

    private void updateColorByTime(TimelyView[] timelyViews, RelativeLayout mainRL) {
        final Calendar currentCal = Calendar.getInstance();
        final long finalTime = currentCal.get(Calendar.SECOND) + currentCal.get(Calendar.MINUTE) * 100 + currentCal.get(Calendar.HOUR_OF_DAY) * 10000;
        intColor = Color.parseColor(getColorString(finalTime));
        long prevTime;
        if (finalTime % 100 != 0) {
            prevTime = finalTime - 1;
        } else {
            if (finalTime % 10000 != 0) {
                prevTime = finalTime - 100 + 59;
            } else {
                if (finalTime != 0) {
                    prevTime = finalTime - 10000 + 5959;
                } else {
                    prevTime = 235959;
                }
            }
        }
        updateColor(timelyViews, mainRL, finalTime, prevTime);
    }

    private void updateColor(TimelyView[] timelyViews, RelativeLayout mainRL, long finalTime, long initialTime) {
        final Window currentWindow = this.getWindow();
        int initialDigit;
        int finalDigit;
        String finalTimeString = String.format("%06d", finalTime);
        String initialTimeString = String.format("%06d", initialTime);
        Log.d("HexClock", initialTimeString + " to " + finalTimeString);
        for (int i = 0; i <= 5; i++) {
            initialDigit = Character.getNumericValue(initialTimeString.charAt(i));
            finalDigit = Character.getNumericValue(finalTimeString.charAt(i));
            if (finalDigit != initialDigit) {
                Log.d("HexClock", initialDigit + " to " + finalDigit);
                timelyViews[i].animate(initialDigit, finalDigit).setDuration(600).start();
            }
        }
        mainRL.setBackgroundColor(intColor);
        currentWindow.setNavigationBarColor(intColor);
        currentWindow.setStatusBarColor(intColor);
        getActionBar().setBackgroundDrawable(new ColorDrawable(intColor));
    }

    private void firstSet(TimelyView[] timelyViews, RelativeLayout mainRL) {
        final Calendar currentCal = Calendar.getInstance();
        final long finalTime = currentCal.get(Calendar.SECOND) + currentCal.get(Calendar.MINUTE) * 100 + currentCal.get(Calendar.HOUR_OF_DAY) * 10000;
        String finalTimeString = String.format("%06d", finalTime);
        int finalDigit;
        for (int i = 0; i <= 5; i++) {
            finalDigit = Character.getNumericValue(finalTimeString.charAt(i));
            timelyViews[i].animate(0, finalDigit).setDuration(0).start();
        }
    }


    private String getColorString(long time){
        return "#" + String.format("%06d",time);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("Current Color", intColor);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        intColor = savedInstanceState.getInt("Current Color");

    }
}
