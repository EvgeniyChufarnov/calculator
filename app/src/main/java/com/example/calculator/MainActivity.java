package com.example.calculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.calculator.moshi_adapter.StringBuilderJSONAdapter;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MainView {

    private static final String PRESENTER = "Presenter";
    private static final String SHARED_PREFERENCE = "sharedPreference";
    private static final String IS_NIGHT_THEME = "isNightTheme";

    private TextView resultTextView;
    private TextView historyTextView;
    private MainPresenter presenter;
    private JsonAdapter<MainPresenter> jsonAdapter;
    private boolean isNightTheme;

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

        if (savedInstanceState != null && savedInstanceState.containsKey(PRESENTER)) {
            String previousState = savedInstanceState.getString(PRESENTER);

            restoreState(previousState);
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