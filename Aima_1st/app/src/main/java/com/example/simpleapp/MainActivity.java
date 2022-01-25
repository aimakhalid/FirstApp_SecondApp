package com.example.simpleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button buttonresult;
    TextView textview;
    EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonresult = findViewById(R.id.btnresult);
        textview = findViewById(R.id.resulttextview);
        editText = findViewById(R.id.simpleSpinner);
        buttonresult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(editText.getText().toString())) {
                    textview.setText("");
                    Toast.makeText(MainActivity.this, "Empty field not allowed!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    textview.setText(editText.getText().toString() + "Cl");
                }
            }
        });

    }
}
