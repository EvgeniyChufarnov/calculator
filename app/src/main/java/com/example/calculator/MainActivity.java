package com.example.calculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.calculator.moshi_adapter.StringBuilderJSONAdapter;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MainView {

    private static final String PRESENTER = "Presenter";
    private static final String BACKGROUND_URI = "Background";
    private static final String SHARED_PREFERENCE = "sharedPreference";
    private static final String IS_NIGHT_THEME = "isNightTheme";

    private static final String FIRST_NUMBER = "first number";
    private static final String SECOND_NUMBER = "second number";
    private static final String OPERATION = "operation";
    private static final String IMAGE_URI = "image uri";

    private static final String URI_PREFIX = "http://";
    private static final String URI_PREFIX_CHECK = "http";

    private TextView resultTextView;
    private TextView historyTextView;
    private MainPresenter presenter;
    private JsonAdapter<MainPresenter> jsonAdapter;
    private boolean isNightTheme;
    private boolean hasBackground = false;
    private String backgroundURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isNightTheme = getDefaultTheme();
        setNightTheme(isNightTheme);

        resultTextView = findViewById(R.id.tv_result);
        historyTextView = findViewById(R.id.tv_history);

        init();

        Moshi moshi = new Moshi.Builder().add(new StringBuilderJSONAdapter()).build();
        jsonAdapter = moshi.adapter(MainPresenter.class);

        Bundle initialBundle = getIntent().getExtras();

        if (savedInstanceState == null && checkInitialValues(initialBundle)) {
            setInitialValues(initialBundle);
        } else if (savedInstanceState != null && savedInstanceState.containsKey(PRESENTER)) {
            String previousState = savedInstanceState.getString(PRESENTER);

            restoreState(previousState);

            if (savedInstanceState.containsKey(BACKGROUND_URI)) {
                setBackgroundImage(savedInstanceState.getString(BACKGROUND_URI));
            }
        } else {
            presenter = new MainPresenter(this);
        }
    }

    private void init() {
        for (Map.Entry<Integer, String> entry : Util.NUM_BUTTONS.entrySet()) {
            findViewById(entry.getKey()).setOnClickListener(view ->
                    presenter.onNumClicked(Util.NUM_BUTTONS.get(view.getId()))
            );
        }

        for (Map.Entry<Integer, MainPresenter.Operation> entry : Util.OPERATION_BUTTONS.entrySet()) {
            findViewById(entry.getKey()).setOnClickListener(view ->
                    presenter.onOperation(Util.OPERATION_BUTTONS.get(view.getId()))
            );
        }

        findViewById(R.id.b_cancel_entry).setOnClickListener(view ->
                presenter.onCancel()
        );

        findViewById(R.id.b_clear).setOnClickListener(view ->
                presenter.onClear()
        );

        findViewById(R.id.b_equals).setOnClickListener(view ->
                presenter.onCalculate()
        );

        findViewById(R.id.b_point).setOnClickListener(view ->
                presenter.addPoint()
        );

        ToggleButton toggleButton = findViewById(R.id.tb_night_theme);
        toggleButton.setChecked(isNightTheme);
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    setNightTheme(isChecked);
                    setDefaultTheme(isChecked);
                }
        );
    }

    private boolean checkInitialValues(Bundle bundle) {
        return bundle != null && bundle.containsKey(FIRST_NUMBER) && bundle.containsKey(SECOND_NUMBER)
                && bundle.containsKey(OPERATION) && bundle.containsKey(IMAGE_URI);
    }

    private void setInitialValues(Bundle bundle) {
        presenter = new MainPresenter(
                this,
                bundle.getString(FIRST_NUMBER),
                bundle.getString(SECOND_NUMBER),
                bundle.getString(OPERATION)
        );

        String uri = bundle.getString(IMAGE_URI);

        if (uri != null && !uri.isEmpty()) {
            setBackgroundImage(uri);
        }
    }

    private void setBackgroundImage(String uri) {
        ImageView background = findViewById(R.id.iv_background);

        if (!uri.startsWith(URI_PREFIX_CHECK)) {
            uri = URI_PREFIX + uri;
        }

        try {
            Picasso.get().load(uri).into(background);
            background.setVisibility(View.VISIBLE);
            if (!hasBackground) {
                hasBackground = true;
                backgroundURI = uri;
            }
        } catch (RuntimeException e) {
            Toast.makeText(this, R.string.background_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void restoreState(String previousState) {
        try {
            presenter = jsonAdapter.fromJson(previousState);
            if (presenter != null) {
                presenter.restoreState(this);
            } else {
                presenter = new MainPresenter(this);
            }
        } catch (IOException e) {
            presenter = new MainPresenter(this);
        }
    }

    @Override
    public void showResult(String result) {
        resultTextView.setText(result);
    }

    @Override
    public void showHistory(String result) {
        historyTextView.setText(result);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(PRESENTER, jsonAdapter.toJson(presenter));

        if (hasBackground) {
            outState.putString(BACKGROUND_URI, backgroundURI);
        }
    }

    private void setNightTheme(boolean isNightTheme) {
        AppCompatDelegate.setDefaultNightMode(
                isNightTheme ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    private boolean getDefaultTheme() {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE);
        return sharedPref.getBoolean(IS_NIGHT_THEME, false);
    }

    private void setDefaultTheme(boolean theme) {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(IS_NIGHT_THEME, theme);
        editor.apply();
    }
}