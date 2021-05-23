package com.example.calculator;

import java.util.Map;

public class Util {
    private static final String DELETE_ZERO_REGEX = "[0/.]+$";
    private static final String POINT = ".";
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
        if (result.contains(POINT))
            return result.replaceAll(DELETE_ZERO_REGEX, "");
        else {
            return result;
        }
    }
}
