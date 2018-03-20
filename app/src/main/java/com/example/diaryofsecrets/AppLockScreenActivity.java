package com.example.diaryofsecrets;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diaryofsecrets.navigation.ChangeTheme;

public class AppLockScreenActivity extends AppCompatActivity implements TextWatcher {

    private Button login;
    private EditText setPasswordFirstDigit, setPasswordSecondDigit, setPasswordThirdDigit, setPasswordForthDigit;
    private DiaryPreference diaryPreference ;
    private TextView mForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        diaryPreference = new DiaryPreference(MyApplication.getContext());
        ChangeTheme.onActivityCreateSetTheme(this, diaryPreference.getTheme());
        setContentView(R.layout.activity_app_lock_screen);
        initializeView();
    }

    private void initializeView() {
        mForgotPassword = findViewById(R.id.forgot_password);
        login = findViewById(R.id.login);
        setPasswordFirstDigit = findViewById(R.id.get_password_first_digit);
        setPasswordSecondDigit = findViewById(R.id.get_password_second_digit);
        setPasswordThirdDigit = findViewById(R.id.get_password_third_digit);
        setPasswordForthDigit = findViewById(R.id.get_password_forth_digit);
        setPasswordFirstDigit.addTextChangedListener(this);
        setPasswordSecondDigit.addTextChangedListener(this);
        setPasswordThirdDigit.addTextChangedListener(this);
        setPasswordForthDigit.addTextChangedListener(this);
        setPasswordFirstDigit.requestFocus();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performLogin();
            }
        });

        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogAskingSecurityQuestion();
            }
        });
    }

    private void performLogin(){
        String password = setPasswordFirstDigit.getText().toString().trim() + setPasswordSecondDigit.getText().toString().trim() +
                setPasswordThirdDigit.getText().toString().trim() + setPasswordForthDigit.getText().toString().trim();
        if(diaryPreference.getPassword().trim().equals(password)){
            Intent catalogIntent = new Intent(AppLockScreenActivity.this, CatalogActivity.class);
            startActivity(catalogIntent);
        }else{
            Toast.makeText(AppLockScreenActivity.this, getString(R.string.password_incorrect),Toast.LENGTH_SHORT).show();
        }
    }

    private void showAlertDialogAskingSecurityQuestion() {
        // update the user name
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialogView = layoutInflater.inflate(R.layout.custom_dialog, null);
        AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(
                this);
        // set title
//        alertDialogBuilder1.setTitle(R.string.security);
        // set custom_dialog.xml to alertdialog builder
        alertDialogBuilder1.setView(dialogView);
        final EditText answer = (EditText) dialogView.findViewById(R.id.security_answer);
        TextView question = (TextView) dialogView.findViewById(R.id.security_question);
        String q = diaryPreference.getSecurityQuestion();
        question.setText(q);
        // set dialog message
        alertDialogBuilder1
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                // get user input and set it to etOutput
                                // edit text
                                if(diaryPreference.getSecurityAnswer().trim().equals(answer.getText().toString().trim())){
                                    Intent catalogIntent = new Intent(AppLockScreenActivity.this, CatalogActivity.class);
                                    startActivity(catalogIntent);
                                }else{
                                    Toast.makeText(AppLockScreenActivity.this, getString(R.string.incorrect_answer), Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog1 = alertDialogBuilder1.create();
        // show it
        alertDialog1.show();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        int editedHash = charSequence.hashCode();
        // Focus 2nd field if 1st is full
        if (setPasswordFirstDigit.getText().hashCode() == editedHash) {
            if (setPasswordFirstDigit.length() == 1) setPasswordSecondDigit.requestFocus();
        }
        // Focus 3rd field if 2nd is full, or focus 1st field if 2nd is empty
        else if (setPasswordSecondDigit.getText().hashCode() == editedHash) {
            if (setPasswordSecondDigit.length() == 1) setPasswordThirdDigit.requestFocus();
            else if (setPasswordSecondDigit.length() == 0) setPasswordFirstDigit.requestFocus();
        }
        // Focus 2nd field if 3rd is empty
        else if (setPasswordThirdDigit.getText().hashCode() == editedHash) {
            if (setPasswordThirdDigit.length() == 1) setPasswordForthDigit.requestFocus();
            else if(setPasswordThirdDigit.length() == 0) setPasswordSecondDigit.requestFocus();
        }

        else if(setPasswordForthDigit.getText().hashCode() == editedHash) {
            if(setPasswordForthDigit.length() == 0) setPasswordThirdDigit.requestFocus();
            else if(setPasswordForthDigit.length() == 1) performLogin();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
