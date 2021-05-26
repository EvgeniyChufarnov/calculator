package com.example.calculator;

import java.util.Map;

public class Util {
    private static final String DELETE_ZERO_REGEX = "\\.?0*$";
    private static final String POINT = ".";
    private static final String POWER_E = "E";
    private static final int MAX_SIZE = 10;
    private static final int MAX_SIZE_WITH_POWER = 7;
    public static Map<Integer, String> NUM_BUTTONS = Map.of(
            R.id.b_0, "0",
            R.id.b_1, "1",
            R.id.b_2, "2",
            R.id.b_3, "3",
            R.id.b_4, "4",
            R.id.b_5, "5",
            R.id.b_6, "6",
            R.id.b_7, "7",
            R.id.b_8, "8",
            R.id.b_9, "9"
    );
    public static Map<Integer, MainPresenter.Operation> OPERATION_BUTTONS = Map.of(
            R.id.b_plus, MainPresenter.Operation.PLUS,
            R.id.b_minus, MainPresenter.Operation.MINUS,
            R.id.b_divide, MainPresenter.Operation.DIVIDE,
            R.id.b_multiply, MainPresenter.Operation.MULTIPLY
    );

    public static String formatResult(String result) {
        String formattedResult = result;

        if (result.contains(POINT) && !result.contains(POWER_E)) {
            formattedResult = result.replaceAll(DELETE_ZERO_REGEX, "");
        }

        if (result.contains(POWER_E) && result.length() > MAX_SIZE) {
            int index_E = result.indexOf(POWER_E);
            String power = result.substring(index_E);
            formattedResult = result.substring(0, MAX_SIZE_WITH_POWER) + power;
        }

        return formattedResult;
    }
}
