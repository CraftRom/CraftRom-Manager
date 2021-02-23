package com.craftrom.manager.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.craftrom.manager.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Mike on 12/13/2014.
 */
public class LogActivity extends AppCompatActivity {

    private TextView text;
    private ScrollView scrollView;
    private StringBuilder builder;

    private static final String DEFAULT_NAME = "last_kmsg_%s.txt";
    private static final String DEFAULT_LOC = Environment.getExternalStorageDirectory().getAbsolutePath();

    @SuppressLint({"ObsoleteSdkInt", "StaticFieldLeak"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        scrollView = findViewById(R.id.scroller);
        text = findViewById(R.id.text);
        text.setTextIsSelectable(true);

        final File last_kmsg = new File("/proc/last_kmsg");
        if (!last_kmsg.exists() || !last_kmsg.isFile()) {
            text.setText("last_kmsg NF");
            return;
        }

        new AsyncTask<Void, Void, Void>() {

            private ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(LogActivity.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setIndeterminate(true);
                dialog.setMessage(getString(R.string.msg_pleaseWait));
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    builder = new StringBuilder();
                    InputStreamReader ISreader = new InputStreamReader(Runtime.getRuntime().exec("su -c cat " + last_kmsg.getAbsolutePath()).getInputStream());
                    BufferedReader reader = new BufferedReader(ISreader);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line).append("\n\r");
                    }
                    reader.close();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                text.post(new Runnable() {
                    @Override
                    public void run() {
                        text.setText(builder.toString());
                    }
                });
                if (dialog != null && dialog.isShowing())
                    text.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            scrollView.smoothScrollTo(0, text.getBottom());
                        }
                    }, 500);
            }
        }.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.slide_in_rtl, R.anim.slide_out_rtl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.log_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search: {
                search();
                break;
            }
            case R.id.action_pageDown: {
                scrollView.post(() -> scrollView.smoothScrollTo(0, scrollView.getScrollY() + scrollView.getHeight()));
                break;
            }
            case R.id.action_pageUp: {
                scrollView.post(() -> scrollView.smoothScrollTo(0, scrollView.getScrollY() - scrollView.getHeight()));
                break;
            }
            case android.R.id.home: {
                onBackPressed();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }





    @SuppressLint("ObsoleteSdkInt")
    private void search() {

        final RelativeLayout topSearch = findViewById(R.id.topSearch);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            topSearch.setElevation(5);

            int cx = topSearch.getRight();
            int cy = topSearch.getTop();
            int finalRadius = (int) Math.sqrt(Math.pow(topSearch.getWidth(), 2) + Math.pow(topSearch.getHeight(), 2));

            if (topSearch.getVisibility() == View.INVISIBLE) {
                Animator animator = ViewAnimationUtils.createCircularReveal(topSearch, cx, cy, 0, finalRadius);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationEnd(animation);
                        topSearch.setVisibility(View.VISIBLE);
                    }
                });
                animator.start();
            } else {
                Animator animator = ViewAnimationUtils.createCircularReveal(topSearch, cx, cy, finalRadius, 0);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        topSearch.setVisibility(View.INVISIBLE);
                    }
                });
                animator.start();
            }
        } else {
            topSearch.setVisibility(topSearch.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
        }
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {

                topSearch.setVisibility(View.INVISIBLE);
                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(topSearch.getWindowToken(), 0);

                String toFind = ((EditText) findViewById(R.id.searchBox)).getText().toString().trim();

                if (toFind.length() > 0) {
                    new AsyncTask<Void, Void, Boolean>() {
                        SpannableString sString;
                        ProgressDialog dialog;

                        @SuppressLint("StaticFieldLeak")
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            dialog = new ProgressDialog(LogActivity.this);
                            dialog.setCancelable(false);
                            dialog.setIndeterminate(true);
                            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            dialog.setMessage(getString(R.string.msg_pleaseWait));
                            dialog.show();
                        }

                        @SuppressLint("StaticFieldLeak")
                        @Override
                        protected Boolean doInBackground(Void... params) {
                            sString = new SpannableString(builder.toString());
                            String tmp = builder.toString();
                            ArrayList<Integer> indexes = new ArrayList<>();
                            String toFind = ((EditText) findViewById(R.id.searchBox)).getText().toString().trim();
                            StringBuilder toRep = new StringBuilder();
                            for (int i = 0; i < toFind.length(); i++) {
                                toRep.append(" ");
                            }

                            while (tmp.contains(toFind.toLowerCase()) || tmp.contains(toFind.toUpperCase())) {
                                if (tmp.contains(toFind.toLowerCase())) {
                                    int i = tmp.indexOf(toFind.toLowerCase());
                                    indexes.add(i);
                                    tmp = tmp.replaceFirst(toFind.toLowerCase(), toRep.toString());
                                }
                                if (tmp.contains(toFind.toUpperCase())) {
                                    int i = tmp.indexOf(toFind.toUpperCase());
                                    indexes.add(i);
                                    tmp = tmp.replaceFirst(toFind.toUpperCase(), toRep.toString());
                                }
                            }
                            for (int i = 0; i < indexes.size(); i++) {
                                int start = indexes.get(i);
                                int end = start + toFind.length();
                                sString.setSpan(new BackgroundColorSpan(Color.YELLOW), start, end, 0);
                                sString.setSpan(new RelativeSizeSpan(2f), start, end, 0);
                            }
                            return indexes.size() > 0;
                        }

                        @SuppressLint("StaticFieldLeak")
                        @Override
                        protected void onPostExecute(Boolean bool) {
                            super.onPostExecute(bool);
                            if (dialog != null && dialog.isShowing()) {
                                dialog.hide();
                            }
                            if (bool) {
                                text.postDelayed(() -> {
                                    text.setText(sString);
                                    Toast.makeText(getApplicationContext(), R.string.msg_textHighlighted, Toast.LENGTH_LONG).show();
                                }, 100);
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.msg_textNotFound, Toast.LENGTH_LONG).show();
                            }
                        }
                    }.execute();
                } else {
                    text.setText(builder.toString());
                }
            }
        });
    }

}
