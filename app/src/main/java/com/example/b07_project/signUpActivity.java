package com.example.b07_project;

import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import encryption.MD5Encryption;

public class signUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_sign_up);
        //code from https://developer.android.com/guide/topics/ui/controls/spinner
        Spinner spinner = (Spinner) findViewById(R.id.accountTypeMenu);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.account_type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


    }
    /*
    uncomment this once we have a signout button else impossible to signout
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            boolean IsCustomer=true; // access user data
            launch_page(IsCustomer);
        }
    }*/
    public void launch_page(boolean IsCustomer){
        if(IsCustomer){
            Intent intent = new Intent(this, CustomerOrderActivity.class);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(this, StoreActivity.class);
            startActivity(intent);
        }
    }
    public void createAccount(View view){

        TextView emailText = findViewById(R.id.SignUpUserEmailAddress);
        TextView passwordText = findViewById(R.id.SignUpUserPassword);
        TextView confirmPasswordText = findViewById(R.id.signUpConfirmPassword);
        Spinner dropDownMenu = findViewById(R.id.accountTypeMenu);


        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String confirmPassword = confirmPasswordText.getText().toString();
        String accountType = dropDownMenu.getSelectedItem().toString();
        
        //https://www.javatpoint.com/java-email-validation
        String validEmail = ("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\" +
                ".[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$");

        Pattern pattern = Pattern.compile(validEmail);
        Matcher matcher = pattern.matcher(email);
        if(!matcher.matches()){
            emailText.setError("please enter a valid email address");

            return;
        }


        //TODO check if email is already in the account



        if (!password.equals(confirmPassword) ){
            confirmPasswordText.setError("does not equal password");
            confirmPasswordText.setText("");
            return;
        }

        if (password.equals("")){
            passwordText.setError("can not be empty");
            return;
        }

        if (password.length() < 6){
            passwordText.setError("must be longer than 6 characters!!!");
            return;
        }


        MD5Encryption encrypt = new MD5Encryption();
        String encryptedPassword = encrypt.encrypt(password);

        //TODO add the encrypted password and email as a new account
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            launch_page(accountType.equals("Customer Account"));//hard coded for now, ideally use getResource to check
                        } else {
                            // If sign in fails, display a message to the user.
                            try {
                                Toast.makeText(signUpActivity.this, "Authentication failed." + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }catch (NullPointerException e){
                                Toast.makeText(signUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}