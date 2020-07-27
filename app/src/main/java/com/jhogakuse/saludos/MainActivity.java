package com.jhogakuse.saludos;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_introduce)
    EditText mainIntroduce;
    @BindView(R.id.main_buttons)
    LinearLayout mainButtons;
    @BindView(R.id.contentMain)
    TextInputLayout contentMain;

    private boolean isEditInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        configEditText();
    }

    private void configEditText() {
//        mainButtons.setOnClickListener(view);
        mainIntroduce.setOnTouchListener(touch);
        mainIntroduce.addTextChangedListener(watcher);
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!isEditInProgress && Metodos.canReplaceOperator(s)){
                isEditInProgress = true;
                mainIntroduce.getText().delete(mainIntroduce.getText().length() - 2, mainIntroduce.getText().length() - 1);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            isEditInProgress = false;
        }
    };

    private View.OnClickListener view = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            input.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    };

    private View.OnTouchListener touch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP){
                if(event.getRawX() >= (mainIntroduce.getRight() - mainIntroduce.getCompoundDrawables()[Constantes.DRAWABLE_RIGHT].getBounds().width())){
                    if (mainIntroduce.length() > 0){
                        final int length = mainIntroduce.getText().length();
                        mainIntroduce.getText().delete(length-1, length);
                    }
                }
                return true;
            }
            return false;
        }
    };

    @OnClick({R.id.main_btnSeven, R.id.main_btnEight, R.id.main_btnNine, R.id.main_btnFour, R.id.main_btnFive,
            R.id.main_btnSix, R.id.main_btnOne, R.id.main_btnTwo, R.id.main_btnThree, R.id.main_btnPoint, R.id.main_btnZero})
    public void onClickNumbers(View view) {
        final String valStr = ((Button) view).getText().toString();
        switch (view.getId()) {
            case R.id.main_btnZero:
            case R.id.main_btnOne:
            case R.id.main_btnTwo:
            case R.id.main_btnThree:
            case R.id.main_btnFour:
            case R.id.main_btnFive:
            case R.id.main_btnSix:
            case R.id.main_btnSeven:
            case R.id.main_btnEight:
            case R.id.main_btnNine:
                mainIntroduce.getText().append(valStr);
                break;
            case R.id.main_btnPoint:
                final String operacion = mainIntroduce.getText().toString();
                final String operador = Metodos.getOperator(operacion);
                final int count = operacion.length() - operacion.replace(".", "").length();
                if (!operacion.contains(Constantes.POINT) ||
                        (count < 2 && (!operador.equals(Constantes.OPERATOR_NULL)))) {
                    mainIntroduce.getText().append(valStr);
                }
                break;
            default:
                break;
        }
    }

    @OnClick({R.id.main_btnC, R.id.main_btnDiv, R.id.main_btnMul, R.id.main_btnRes, R.id.main_btnSum, R.id.main_btnEqual})
    public void onClickControls(View view) {
        switch (view.getId()) {
            case R.id.main_btnC:
                mainIntroduce.setText("");
                break;
            case R.id.main_btnDiv:
            case R.id.main_btnMul:
            case R.id.main_btnRes:
            case R.id.main_btnSum:
                resolve(false);
                final String operador = ((Button)view).getText().toString();
                final String operacion = mainIntroduce.getText().toString();
                final String ultimoCaracter = operacion.isEmpty() ? "" : operacion.substring(operacion.length() - 1);
                if(operador.equals(Constantes.OPERATOR_SUB)){
                    if(operacion.isEmpty() ||
                            (!(ultimoCaracter.equals(Constantes.OPERATOR_SUB)) &&
                            !(ultimoCaracter.equals(Constantes.POINT)))){
                        mainIntroduce.getText().append(operador);
                    }
                } else {
                    if(!operacion.isEmpty() &&
                            !(ultimoCaracter.equals(Constantes.OPERATOR_SUB)) &&
                            !(ultimoCaracter.equals(Constantes.POINT))){
                        mainIntroduce.getText().append(operador);
                    }
                }
            break;
            case R.id.main_btnEqual:
                resolve(true);
                break;
        }
    }

    private void resolve(boolean fromResult) {
        Metodos.tryResolve(fromResult, mainIntroduce, new OnResolveCallback() {
            @Override
            public void onShowMessage(int errorRes) {
                ShowMessage(errorRes);
            }

            @Override
            public void onIsEditing() {
                isEditInProgress = true;
            }
        });
    }

    private void ShowMessage(int errorRes) {
        Snackbar.make(contentMain, errorRes, Snackbar.LENGTH_SHORT).show();
    }
}
