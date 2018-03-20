package com.example.diaryofsecrets.navigation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diaryofsecrets.DiaryPreference;
import com.example.diaryofsecrets.MyApplication;
import com.example.diaryofsecrets.R;

/**
 * Created by Nandita Rai on 2/3/2018.
 */

public class SetAppLockFragment extends Fragment implements TextWatcher {

    public static final String TAG = SetAppLockFragment.class.getSimpleName();

    private EditText setPasswordFirstDigit, setPasswordSecondDigit, setPasswordThirdDigit, setPasswordForthDigit,
            confirmPasswordFirstDigit, confirmPasswordSecondDigit, confirmPasswordThirdDigit, confirmPasswordForthDigit, securityQuestionAnswer;
    private Spinner securityQuestionSpinner;
    private Button setPasswordButton;
    private TextView setPasswordText;
    private DiaryPreference diaryPreference;
    private String mPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.set_app_lock_fragment, container, false);
        diaryPreference = new DiaryPreference(MyApplication.getContext());
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        setPasswordText = (TextView) view.findViewById(R.id.set_password_text);
        if(diaryPreference.get)

        setPasswordFirstDigit = view.findViewById(R.id.set_password_first_digit);
        setPasswordSecondDigit = view.findViewById(R.id.set_password_second_digit);
        setPasswordThirdDigit = view.findViewById(R.id.set_password_third_digit);
        setPasswordForthDigit = view.findViewById(R.id.set_password_forth_digit);
        setPasswordFirstDigit.addTextChangedListener(this);
        setPasswordSecondDigit.addTextChangedListener(this);
        setPasswordThirdDigit.addTextChangedListener(this);
        setPasswordForthDigit.addTextChangedListener(this);

        confirmPasswordFirstDigit = view.findViewById(R.id.confirm_password_first_digit);
        confirmPasswordSecondDigit = view.findViewById(R.id.confirm_password_second_digit);
        confirmPasswordThirdDigit = view.findViewById(R.id.confirm_password_third_digit);
        confirmPasswordForthDigit = view.findViewById(R.id.confirm_password_forth_digit);
        confirmPasswordFirstDigit.addTextChangedListener(this);
        confirmPasswordSecondDigit.addTextChangedListener(this);
        confirmPasswordThirdDigit.addTextChangedListener(this);
        confirmPasswordForthDigit.addTextChangedListener(this);

//        setKeyListener();

        securityQuestionSpinner = view.findViewById(R.id.security_question_spinner);
        securityQuestionAnswer = view.findViewById(R.id.security_question_answer);
        setSpinner();

        view.findViewById(R.id.set_password_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateData()){
                    diaryPreference.setSecurityAnswer(securityQuestionAnswer.getText().toString().trim());
                    diaryPreference.setPassword(mPassword);
                    Toast.makeText(getActivity(), getString(R.string.password_set_successful),Toast.LENGTH_SHORT).show();
                    getActivity().getFragmentManager().popBackStack();
                }
            }
        });

    }

    private void setSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> securityQuestionAdapter = ArrayAdapter
                .createFromResource(getActivity(), R.array.security_questions_array,
                        android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        securityQuestionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        securityQuestionSpinner.setAdapter(securityQuestionAdapter);
        securityQuestionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                diaryPreference.setSecurityQuestionPosition(position);
                diaryPreference.setSecurityQuestion((String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    private boolean validateData() {
        if(!validatePassword()){
            return false;
        }
        if(diaryPreference.getSecurityQuestionPosition() == 0){
            Toast.makeText(getActivity(), getString(R.string.no_security_question_selected),Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(securityQuestionAnswer.getText().toString().trim())){
            Toast.makeText(getActivity(), getString(R.string.no_security_answer), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validatePassword() {
        return checkForNull() && checkForEquality();

    }

    private boolean checkForEquality() {
        if( setPasswordFirstDigit.getText().toString().trim().equals(confirmPasswordFirstDigit.getText().toString().trim()) &&
                setPasswordSecondDigit.getText().toString().trim().equals(confirmPasswordSecondDigit.getText().toString().trim()) &&
                setPasswordThirdDigit.getText().toString().trim().equals(confirmPasswordThirdDigit.getText().toString().trim()) &&
                setPasswordForthDigit.getText().toString().trim().equals(confirmPasswordForthDigit.getText().toString().trim())){
            return true;
        }
        else{
            Toast.makeText(getActivity(), getString(R.string.password_mismatch), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean checkForNull() {
        if( !TextUtils.isEmpty(setPasswordFirstDigit.getText()) || !TextUtils.isEmpty(setPasswordSecondDigit.getText()) ||
                !TextUtils.isEmpty(setPasswordThirdDigit.getText()) || !TextUtils.isEmpty(setPasswordForthDigit.getText())){
            mPassword = setPasswordFirstDigit.getText().toString().trim() + setPasswordSecondDigit.getText().toString().trim()
                    + setPasswordThirdDigit.getText().toString().trim() + setPasswordForthDigit.getText().toString().trim();
            return true;
        }
        else {
            Toast.makeText(getActivity(), R.string.password_empty, Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        int editedHash = charSequence.hashCode();
        // on set password fields
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
            else if(setPasswordForthDigit.length() == 1) confirmPasswordFirstDigit.requestFocus();
        }

        // on confirm password fields
        // Focus 2nd field if 1st is full
        if (confirmPasswordFirstDigit.getText().hashCode() == editedHash) {
            if (confirmPasswordFirstDigit.length() == 1) confirmPasswordSecondDigit.requestFocus();
        }
        // Focus 3rd field if 2nd is full, or focus 1st field if 2nd is empty
        else if (confirmPasswordSecondDigit.getText().hashCode() == editedHash) {
            if (confirmPasswordSecondDigit.length() == 1) confirmPasswordThirdDigit.requestFocus();
            else if (confirmPasswordSecondDigit.length() == 0) confirmPasswordFirstDigit.requestFocus();
        }
        // Focus 2nd field if 3rd is empty
        else if (confirmPasswordThirdDigit.getText().hashCode() == editedHash) {
            if (confirmPasswordThirdDigit.length() == 1) confirmPasswordForthDigit.requestFocus();
            else if(confirmPasswordThirdDigit.length() == 0) confirmPasswordSecondDigit.requestFocus();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
