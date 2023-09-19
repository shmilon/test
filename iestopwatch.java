package com.shmilon.iestopwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.testing.FakeReviewManager;
import com.google.android.play.core.tasks.Task;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.shmilon.iestopwatch.Ads.Admob;
import com.shmilon.iestopwatch.devs.DevActivity;
import com.shmilon.iestopwatch.timeit.Split;
import com.shmilon.iestopwatch.timeit.Stopwatch;
import com.startapp.sdk.adsbase.StartAppAd;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Stopwatch.OnTickListener {



    // custom functionality initialization
    MyFunction myFunction = new MyFunction();
    Admob admob; // admob ads init

    // initial variable and default value defined
    double avg = 0, avgOld = 0, awResult = 0, stTime = 0, targetPerHour = 0,targetEfficiency = 0,
            yold = 0, y = 0,p2 = 0;
    Boolean play = null;
    double mysplit = 0;
    long mySplitTime = 0;

    String adUnitId, outputString;

    // ui elements initialization
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;

    ImageView imageMenu, btn_play, btn_pause, btn_loop, btn_stop;

    // creating object of ImageView and Text View
    TextView myStopwatchShow,myaverageTimeShow,mystandardTimeResult,
            mytargetResult, myallowanceParcentResult,mytargetEfficiencyResult,loopTime,avgSecText,
            alloSecText,stdSecText,time, splitLog;
    EditText mygetAllowanceEditText,mygetEfficiencyEditText;
    Stopwatch stopwatch; // stopwatch initialized
    NestedScrollView loopSection;
    RelativeLayout TopBanner,BottomBanner;

    // variables for backPressed functionality
    private static final int TIME_DELAY = 2000;
    private static long backPressed;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ui elements identification

         btn_play = findViewById(R.id.btn_play);
         btn_pause = findViewById(R.id.btn_pause);
         btn_loop = findViewById(R.id.btn_loop);
         btn_stop = findViewById(R.id.btn_stop);

        btn_loop.setVisibility(View.GONE);
        btn_stop.setVisibility(View.GONE);
        btn_pause.setVisibility(View.GONE);

        myStopwatchShow = findViewById(R.id.mystopWatchtimer);
        myaverageTimeShow = findViewById(R.id.myaverageTimeShow);
        mystandardTimeResult = findViewById(R.id.mystandardTimeResult);
        mytargetResult = findViewById(R.id.targetResult);
        myallowanceParcentResult = findViewById(R.id.myallowanceParcentResult);
        mytargetEfficiencyResult = findViewById(R.id.targetEfficiencyResult);
        loopTime = findViewById(R.id.loopTime);
        mygetAllowanceEditText = findViewById(R.id.getAllowanceEditText);
        mygetEfficiencyEditText = findViewById(R.id.getEfficiencyEditText);
        loopSection = findViewById(R.id.loopSection);
        TopBanner = findViewById(R.id.TopBanner);
        //BottomBanner = findViewById(R.id.BottomBanner);

        avgSecText = findViewById(R.id.avgSecText);
        alloSecText = findViewById(R.id.alloSecText);
        stdSecText = findViewById(R.id.stdSecText);

        myaverageTimeShow.setText(getString(R.string.timer));
        mystandardTimeResult.setText(String.valueOf(stTime));
        mytargetResult.setText(String.valueOf(targetPerHour));

        // admob banner ads
        Admob.AdmobBannerAds(MainActivity.this,TopBanner,"banner");
//      Admob.AdmobBannerAds(MainActivity.this,BottomBanner,"banner");

        //showing profile pic getting from google
        //CircularImageView profileImage = findViewById(R.id.profileimage);

//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if (account != null) {
//            Uri personPhoto = account.getPhotoUrl();
//            if (personPhoto != null) {
//                Glide.with(this).load(personPhoto).into(profileImage);
//            }
//        }

        // stopwatch object
        stopwatch = new Stopwatch();
        stopwatch.setDebugMode(true);

        stopwatch.setTextView(myStopwatchShow);
        splitLog = findViewById(R.id.loopTime);
        stopwatch.setOnTickListener(this);
        stopwatch.setClockDelay(50);

//        profileImage.setOnClickListener(view -> {
//            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
//        });


        // Navagation Drawar------------------------------
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_View);
        imageMenu = findViewById(R.id.actionbarMenu);

        toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Drawar click event
        // Drawer item Click event ------
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
//                    case R.id.mCalculator:
//                        myFunction.openCalculator(MainActivity.this);
//                        drawerLayout.closeDrawers();
//                        break;
//
//                    case R.id.mNote:
//                        Toast.makeText(MainActivity.this, "Note", Toast.LENGTH_SHORT).show();
//                        myFunction.openNotes(MainActivity.this);
//                        drawerLayout.closeDrawers();
//                        break;


                    case R.id.mAbout:
                        startActivity(new Intent(MainActivity.this, DevActivity.class));
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.MoreApps:
                        Toast.makeText(MainActivity.this, "More Apps", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, MoreAppActivity.class));
                        drawerLayout.closeDrawers();
                        break;

//                    case R.id.mRate:
//                        Toast.makeText(MainActivity.this, "Rate us", Toast.LENGTH_SHORT).show();
//                        ///myFunction.askRatings(MainActivity.this);
//                        drawerLayout.closeDrawers();
//                        break;

                    case R.id.shareApp:

                        myFunction.shareApp(MainActivity.this);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.mUpdate:
                        startActivity(new Intent(MainActivity.this, UpdateFromPlayActivity.class));

                        drawerLayout.closeDrawers();
                        break;

                    case R.id.Policy:
                        Toast.makeText(MainActivity.this, "Privacy Policy", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        drawerLayout.closeDrawers();
                        break;

                    default:
                        Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                        break;

                }

                return false;
            }
        });
        //------------------------------

        // ------------------------
        // App Bar Click Event
        imageMenu = findViewById(R.id.actionbarMenu);

        imageMenu.setOnClickListener(view -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });


        // ------------------------


        mygetAllowanceEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                if (mygetAllowanceEditText.getText().length() > 0 && mygetAllowanceEditText.getText().length() < 100) {
                    if (!charSequence.equals("")) {
                        double massValue = Double.parseDouble(charSequence.toString());
                        if (massValue > 10) {
                            mygetAllowanceEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        } else {
                            mygetAllowanceEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        }
                    }




                    //convert value into float
                    //String s = mygetAllowanceEditText.getText().toString();
                    //outputString+= s; // data store the to output
                    y = Double.parseDouble(mygetAllowanceEditText.getText().toString());
                    if (avg > 0 ){
                        awResult = avg * (y * 0.01);
                        myFunction.FunAverageTimeFormat(awResult,myallowanceParcentResult);
                    }else{
                        avg= 0;
                        awResult = avg * (y * 0.01);
                    }

                    // do Time Format
                    myFunction.FunAverageTimeFormat(awResult,myallowanceParcentResult);

                    stTime = avg + awResult;
                    myFunction.FunAverageTimeFormat(stTime,mystandardTimeResult);
                    myFunction.ShowUnitText(awResult,stdSecText);

                    // target result calculation
                    if(avg>0){
                        targetPerHour = 3600 / (stTime/1000);
                        mytargetResult.setText("" + (int) targetPerHour);
                    }else{
                        mytargetResult.setText("0");
                    }

                }
            }

            // implementation of afterTextChanged on Editable text field
            @Override
            public void afterTextChanged(Editable editable) {

                if (mygetAllowanceEditText.getText().length() == 0) {
                    awResult = 0;
                    targetPerHour = 0;
                    myallowanceParcentResult.setText(String.valueOf(awResult));
                    myFunction.ShowUnitText(awResult,alloSecText);
                    mytargetResult.setText(String.valueOf(targetPerHour));
                    mystandardTimeResult.setText("0");
                }
                if(mygetAllowanceEditText.getText().length() > 0){
                    myFunction.FunAverageTimeFormat(awResult,myallowanceParcentResult);
                    myFunction.ShowUnitText(awResult,alloSecText);
                    mytargetResult.setText("" + String.format("%.2f", targetPerHour));
                }
            }
        });

        mygetEfficiencyEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                if (mygetEfficiencyEditText.getText().length() > 0 && mygetEfficiencyEditText.getText().length() < 100) {
                    if (!charSequence.equals("") && !charSequence.equals(".")) {
                        double massValue = Double.parseDouble(charSequence.toString());
                        if (massValue > 10) {
                            mygetEfficiencyEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        } else {
                            mygetEfficiencyEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "Please, type correct value", Toast.LENGTH_SHORT).show();
                    }

                    //convert value into double
                    //String s = mygetEfficiencyEditText.getText().toString();
                    double p = Double.parseDouble(mygetEfficiencyEditText.getText().toString());

                    if(targetPerHour > 0){
                        targetEfficiency = targetPerHour * (p * 0.01);
                        mytargetEfficiencyResult.setText(String.format("%.2f", targetEfficiency).toString());
                    }else{
                        targetPerHour= 0;
                        //targetPerHour = avg * (y * 0.01);
                        mytargetEfficiencyResult.setText(String.format("%.2f", targetEfficiency).toString());
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (mygetEfficiencyEditText.getText().length() == 0) {
                    targetEfficiency = 0;
                    mytargetEfficiencyResult.setText("" + String.format("%.2f", targetEfficiency));
                }
                if(mygetAllowanceEditText.getText().length() > 0){
                    mytargetEfficiencyResult.setText("" + String.format("%.2f", targetEfficiency));
                }

            }
        });

        // method calling for creating app icon on the launcher screen
        myFunction.createShortCut(MainActivity.this);
    }

    @Override
    public void onClick(View v) {
        Log.d("ONCLICK", v.toString());
        ScrollView s = findViewById(R.id.scrollViewMain);


        switch (v.getId()) {
            case R.id.btn_play:
                    s.fullScroll(View.FOCUS_UP);
                    if (stopwatch.isStarted() && stopwatch.isPaused()){
                        stopwatch.resume();
                        myFunction.myVibrator(this, 100);
                    }
                    if (!stopwatch.isStarted() && !stopwatch.isPaused()){
                        stopwatch.start();
                        myFunction.myVibrator(this, 100);
                        splitLog.setText(null);
                    }

                btn_play.setVisibility(View.GONE);
                btn_stop.setVisibility(View.GONE);
                btn_loop.setVisibility(View.VISIBLE);
                btn_pause.setVisibility(View.VISIBLE);

                break;
            case R.id.btn_stop:

                if (stopwatch.isStarted())
                    stopwatch.stop();

                myFunction.myVibrator(this, 100);

                btn_play.setVisibility(View.VISIBLE);
                btn_stop.setVisibility(View.GONE);
                btn_loop.setVisibility(View.GONE);
                btn_pause.setVisibility(View.GONE);

                myStopwatchShow.setText("00:00:00");

                stopwatch.setTextView(myStopwatchShow);
                loopTime.setText("");
                myaverageTimeShow.setText("");
                mygetAllowanceEditText.setText("");
                mygetEfficiencyEditText.setText("");
                mystandardTimeResult.setText("");
                mytargetEfficiencyResult.setText("");
                avg = 0;

                break;
            case R.id.btn_pause:
                myFunction.myVibrator(this, 100);
                s.fullScroll(View.FOCUS_UP);

                if (stopwatch.isStarted() && !stopwatch.isPaused())
                    stopwatch.pause();
                myFunction.myVibrator(this, 100);

                btn_play.setVisibility(View.VISIBLE);
                btn_stop.setVisibility(View.VISIBLE);
                btn_loop.setVisibility(View.GONE);
                btn_pause.setVisibility(View.GONE);

                break;
            case R.id.btn_loop:
                myFunction.myVibrator(this, 100);
                s.fullScroll(View.FOCUS_UP);

                if (stopwatch.isStarted() && !stopwatch.isPaused())
                    stopwatch.split();

                // time Split and view it to activity
                StringBuilder stringBuilder = new StringBuilder();
                LinkedList<Split> splits = stopwatch.getSplits();
                for (Split split : splits) {
                    long lapTime = split.getLapTime();
                    String lp = Long.toString(lapTime);

                    long splitTime = split.getSplitTime();
                    String spt = Long.toString(splitTime);

                    stringBuilder.append("C").append(splits.indexOf(split)).append("\t\t")
                            .append(myFunction.mySplitTimeFormat(lp)).append("        ")
                            .append(myFunction.mySplitTimeFormat(spt)).append("\n");

                    if (!stopwatch.isPaused()){
                        mysplit = splits.size();
                        mySplitTime = splitTime;
                    }


                }
                loopTime.setText(stringBuilder.toString());
                ((NestedScrollView) (findViewById(R.id.loopSection))).fullScroll(View.FOCUS_DOWN);

                avg = mySplitTime / mysplit;
                avgOld = avg;

                if(avg > 0){
                    myFunction.FunAverageTimeFormat(avg,myaverageTimeShow);
                    myFunction.ShowUnitText(avg,avgSecText);
                }else{
                    avg = 0;
                    myFunction.FunAverageTimeFormat(avg,myaverageTimeShow);
                    myFunction.ShowUnitText(avg,avgSecText);
                }

                break;
        }
    }

    @Override
    public void onTick(Stopwatch stopwatch) {
        Log.d("MAINACTIVITYLISTENER", String.valueOf(stopwatch.getElapsedTime()));
    }


    // backpressure and exit from the application
    @Override
    public void onBackPressed() {
        if (backPressed + TIME_DELAY > System.currentTimeMillis()) {
           super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit!",
                    Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    }

}
