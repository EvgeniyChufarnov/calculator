package com.example.calculator;

import com.example.calculator.moshi_adapter.StringBuilderJSON;

public class MainPresenter {

    private static final int MAX_DIGITS = 9;
    private static final String DEFAULT_ZERO = "0";
    private static final String POINT = ".";
    private static final String ERROR_MESSAGE = "Error";
    @StringBuilderJSON
    private final StringBuilder currentNum;
    @StringBuilderJSON
    private final StringBuilder history;
    private transient MainView mainView;
    private Double previousNumber;
    private Operation previousOperation;
    private boolean hasPreviousNumber;
    private boolean resultIsShown;
    private boolean errorIsShown;

    public MainPresenter(MainView mainView) {
        this.mainView = mainView;
        currentNum = new StringBuilder();
        history = new StringBuilder();
        hasPreviousNumber = false;
        previousOperation = Operation.NONE;
        previousNumber = 0.0;
        resultIsShown = false;
    }

    public void restoreState(MainView mainView) {
        this.mainView = mainView;

        updateHistory();
        updateResult();
    }

    public void onNumClicked(String num) {
        if (currentNum.length() < MAX_DIGITS || resultIsShown || errorIsShown) {
            if (resultIsShown || errorIsShown) {
                if (previousOperation == Operation.NONE) {
                    onClear();
                }

                clearCurrentNum();
                resultIsShown = false;
                errorIsShown = false;
            }

            currentNum.append(num);

            updateResultWithoutFormatting();
        }
    }

    public void onCancel() {
        if (currentNum.length() != 0 && !resultIsShown && !errorIsShown) {
            currentNum.deleteCharAt(currentNum.length() - 1);
            updateResultWithoutFormatting();
        }
    }

    public void onClear() {
        previousNumber = 0.0;
        hasPreviousNumber = false;
        previousOperation = Operation.NONE;
        clearCurrentNum();
        clearHistory();
        updateResult();
        updateHistory();

        resultIsShown = false;
        errorIsShown = false;
    }

    public void addPoint() {
        if (!currentNum.toString().contains(POINT)) {
            if (resultIsShown || errorIsShown || currentNum.length() == 0) {
                clearCurrentNum();
                currentNum.append(DEFAULT_ZERO).append(POINT);
                resultIsShown = false;
                errorIsShown = false;
            } else if (currentNum.length() < MAX_DIGITS - 1) {
                currentNum.append(POINT);
            }
        }

        updateResultWithoutFormatting();
    }

    public void onOperation(Operation operation) {
        if (currentNum.length() == 0 && !hasPreviousNumber || errorIsShown)
            return;

        if (!hasPreviousNumber) {
            previousNumber = Double.parseDouble(currentNum.toString());
            previousOperation = operation;
            clearCurrentNum();
            hasPreviousNumber = true;
            updateResult();
            addToHistory(operation);
        } else if (!resultIsShown) {
            onCalculate();
            previousOperation = operation;
            clearHistory();
            addToHistory(operation);
        } else {
            previousOperation = operation;
            clearHistory();
            addToHistory(operation);
            clearCurrentNum();
            updateResult();
            updateHistory();
            resultIsShown = false;
        }
    }

    public void onCalculate() {
        if (!isValidOperation() || !checkDivisionByZero())
            return;

        try {
            double result = calculate();

            addToHistory();
            clearCurrentNum();
            currentNum.append(result);
            previousNumber = result;
            previousOperation = Operation.NONE;
            resultIsShown = true;
            updateResult();
        } catch (RuntimeException e) {
            onClear();
            showErrorMessage();
        }
    }

    private double calculate() throws RuntimeException {
        double result = 0.0;

        switch (previousOperation) {
            case PLUS: {
                result = previousNumber + Double.parseDouble(currentNum.toString());
                break;
            }
            case MINUS: {
                result = previousNumber - Double.parseDouble(currentNum.toString());
                break;
            }
            case MULTIPLY: {
                result = previousNumber * Double.parseDouble(currentNum.toString());
                break;
            }
            case DIVIDE: {
                result = previousNumber / Double.parseDouble(currentNum.toString());
                break;
            }
        }

        return result;
    }

    private boolean isValidOperation() {
        return hasPreviousNumber && previousOperation != Operation.NONE
                && !(currentNum.length() == 0) && !errorIsShown;
    }

    private boolean checkDivisionByZero() {
        if (currentNum.toString().equals(DEFAULT_ZERO) && previousOperation == Operation.DIVIDE) {
            showErrorMessage();
            return false;
        } else {
            return true;
        }
    }

    public void updateResult() {
        String result = Util.formatResult(currentNum.toString());
        clearCurrentNum();
        currentNum.append(result);
        if (mainView != null) {
            mainView.showResult(result);
        }
    }

    public void updateResultWithoutFormatting() {
        if (mainView != null) {
            mainView.showResult(currentNum.toString());
        }
    }

    public void addToHistory(Operation operation) {
        history.append(Util.formatResult(previousNumber.toString())).append(operation.sign);
        updateHistory();
    }

    public void addToHistory() {
        history.append(Util.formatResult(currentNum.toString()));
        updateHistory();
    }

    public void updateHistory() {
        if (mainView != null) {
            mainView.showHistory(history.toString());
        }
    }

    private void showErrorMessage() {
        onClear();
        errorIsShown = true;
        if (mainView != null) {
            mainView.showResult(ERROR_MESSAGE);
        }
    }

    private void clearCurrentNum() {
        currentNum.setLength(0);
    }

    private void clearHistory() {
        history.setLength(0);
    }

    void onDestroy() {
        mainView = null;
    }

    public enum Operation {
        PLUS("+"), MINUS("-"), MULTIPLY("*"), DIVIDE("/"), NONE("");

        private final String sign;

        Operation(String sign) {
            this.sign = sign;
        }
    }
}
