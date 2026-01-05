package com.example.pakistanirestaurant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pakistanirestaurant.R;
import com.example.pakistanirestaurant.database.DBHelper;
import com.example.pakistanirestaurant.utils.MySharedPrefManager;
import com.example.pakistanirestaurant.utils.ThemeHelper;
import com.google.mlkit.common.sdkinternal.SharedPrefManager;

// your other imports like ApiClient, FoodAdapter, DBHelper, SharedPrefManager, ThemeHelper

public class LoginActivity extends AppCompatActivity {
    EditText etUsername, etPassword; Button btnLogin, btnSignup;
    DBHelper db; SharedPrefManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DBHelper(this);
       MySharedPrefManager mySharedPrefManager = new MySharedPrefManager(this);;

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);

        btnLogin.setOnClickListener(v -> {
            String u = etUsername.getText().toString().trim();
            String p = etPassword.getText().toString().trim();
            if(u.isEmpty() || p.isEmpty()){
                Toast.makeText(this,"Enter username/password",Toast.LENGTH_SHORT).show();
                return;
            }
            if(db.checkUser(u,p)){
                mySharedPrefManager.setLoggedIn(true);
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this,"Invalid credentials",Toast.LENGTH_SHORT).show();
            }
        });

        btnSignup.setOnClickListener(v -> startActivity(new Intent(this, SignupActivity.class)));
    }
}
