package com.example.rohangoyal2014.jmi_hackathon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book2);
        Intent i=getIntent();
        if(i.hasExtra("name") && i.hasExtra("vicinity")){
            TextView name=findViewById(R.id.name);
            TextView vic=findViewById(R.id.vic);
            name.setText(i.getStringExtra("name"));
            vic.setText(i.getStringExtra("vicinity"));
        }
        Button book=findViewById(R.id.book);
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(BookActivity.this,"Booked",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
