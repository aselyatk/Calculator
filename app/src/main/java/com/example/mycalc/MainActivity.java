package com.example.mycalc;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvExpression, tvResult;
    MaterialButton btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
    MaterialButton btnDot, btnAdd, btnSub, btnDiv, btnMul;
    MaterialButton btnOB, btnCB, btnEqual, btnC, btnAC;
    boolean lastNumeric = false;
    boolean stateError = false;
    boolean lastEqual = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);
        tvExpression = findViewById(R.id.tvExpression);

        btn0 = findViewById(R.id.btn0);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
        btn9 = findViewById(R.id.btn9);
        btnDot = findViewById(R.id.btnDot);
        btnAC = findViewById(R.id.btnAC);
        btnC = findViewById(R.id.btnC);
        btnAdd = findViewById(R.id.btnAdd);
        btnSub = findViewById(R.id.btnSub);
        btnDiv = findViewById(R.id.btnDiv);
        btnMul = findViewById(R.id.btnMul);
        btnOB = findViewById(R.id.btnOB);
        btnCB = findViewById(R.id.btnCB);
        btnEqual = findViewById(R.id.btnEqual);

        btn0.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btnDot.setOnClickListener(this);
        btnAC.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnSub.setOnClickListener(this);
        btnDiv.setOnClickListener(this);
        btnMul.setOnClickListener(this);
        btnOB.setOnClickListener(this);
        btnCB.setOnClickListener(this);
        btnEqual.setOnClickListener(this);

        if (savedInstanceState != null) {
            tvExpression.setText(savedInstanceState.getString("expression"));
            tvResult.setText(savedInstanceState.getString("result"));
        }
    }

    @Override
    public void onClick(View v) {
        MaterialButton button = (MaterialButton) v;
        String btnText = button.getText().toString();
        String data = tvExpression.getText().toString();

        if (btnText.equals("AC")) {
            tvExpression.setText("");
            tvResult.setText("");
            lastNumeric = false;
            stateError = false;
            lastEqual = false;
            resetTextSize();
            return;
        }

        if (btnText.equals("C")) {
            if (data.length() != 0 && !stateError) {
                data = data.substring(0, data.length() - 1);
                tvExpression.setText(data);

                if (data.isEmpty()) {
                    tvResult.setText("");
                } else {
                    String finalResult = evaluateExpression(data);
                    if (!finalResult.equals("Error")) {
                        tvResult.setText(removeTrailingZeros(finalResult));
                    } else {
                        tvResult.setText("");
                    }
                }
            }
            lastNumeric = data.length() > 0 && Character.isDigit(data.charAt(data.length() - 1));
            return;
        }

        // Если произошло нажатие на "=", считаем и выводим результат
        if (btnText.equals("=")) {
            if (lastNumeric && !stateError && !lastEqual) {
                String finalResult = evaluateExpression(data);
                if (!finalResult.equals("Error")) {
                    tvResult.setText(removeTrailingZeros(finalResult));
                    tvExpression.setText("");
                    increaseTextSize();
                    lastEqual = true;
                } else {
                    tvResult.setText("Error");
                    stateError = true;
                }
            }
            return;
        }

        // Предотвращение ввода нескольких операторов подряд
        if ("+-*/".contains(btnText)) {
            if (lastEqual) {
                lastEqual = false;
                tvExpression.setText(tvResult.getText());
                resetTextSize();
            }
            if (data.isEmpty() || !lastNumeric || stateError) {
                return;
            }
            lastNumeric = false;
        }

        if (btnText.equals(".")) {
            String[] tokens = data.split("[+\\-*/]");
            String lastNumber = tokens[tokens.length - 1];
            if (lastNumber.contains(".")) {
                return;
            }
        }

        if (data.equals("0")) {
            data = "";
        }

        data += btnText;
        tvExpression.setText(data);

        if (!"+-*/".contains(btnText)) {
            lastNumeric = true;
            lastEqual = false;
            if (!stateError) {
                String finalResult = evaluateExpression(data);
                if (!finalResult.equals("Error")) {
                    tvResult.setText(removeTrailingZeros(finalResult));
                } else {
                    tvResult.setText("");
                }
            }
        }

        Log.i("result", tvExpression.getText().toString());
    }

    private String evaluateExpression(String expression) {
        Context rhino = Context.enter();
        rhino.setOptimizationLevel(-1);
        try {
            Scriptable scope = rhino.initStandardObjects();
            String result = rhino.evaluateString(scope, expression, "JavaScript", 1, null).toString();
            DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
            return decimalFormat.format(Double.parseDouble(result));
        } catch (Exception e) {
            return "Error";
        } finally {
            Context.exit();
        }
    }

    private String removeTrailingZeros(String result) {
        if (result.contains(".")) {
            result = result.replaceAll("\\.0*$", "");
        }
        return result;
    }

    private void increaseTextSize() {
        tvResult.setTextSize(70);
    }

    private void resetTextSize() {
        tvResult.setTextSize(48);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("expression", tvExpression.getText().toString());
        outState.putString("result", tvResult.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tvExpression.setText(savedInstanceState.getString("expression"));
        tvResult.setText(savedInstanceState.getString("result"));
    }
}
