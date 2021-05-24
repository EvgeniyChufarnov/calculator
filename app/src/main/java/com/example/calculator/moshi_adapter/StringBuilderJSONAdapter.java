package com.example.calculator.moshi_adapter;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

public class StringBuilderJSONAdapter {
    @ToJson
    public String toJson(@StringBuilderJSON StringBuilder input) {
        return input.toString();
    }

    @FromJson
    @StringBuilderJSON
    public StringBuilder fromJson(String input) {
        return new StringBuilder(input);
    }
}
