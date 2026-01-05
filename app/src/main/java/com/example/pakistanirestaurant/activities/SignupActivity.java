package com.example.pakistanirestaurant.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pakistanirestaurant.R;
import com.example.pakistanirestaurant.database.DBHelper;
import com.example.pakistanirestaurant.models.User;
import com.example.pakistanirestaurant.utils.ThemeHelper;

public class SignupActivity extends AppCompatActivity {
    EditText etUsername, etEmail, etPassword; Button btnRegister;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        db = new DBHelper(this);
        etUsername=findViewById(R.id.etUsername);
        etEmail=findViewById(R.id.etEmail);
        etPassword=findViewById(R.id.etPassword);
        btnRegister=findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v->{
            String u=etUsername.getText().toString().trim();
            String e=etEmail.getText().toString().trim();
            String p=etPassword.getText().toString().trim();

            if(u.isEmpty() || e.isEmpty() || p.isEmpty()){
                Toast.makeText(this,"All fields required",Toast.LENGTH_SHORT).show();
                return;
            }
            User user=new User(u,e,p);
            if(db.addUser(user)){
                Toast.makeText(this,"Registered successfully",Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this,"Username already exists",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
