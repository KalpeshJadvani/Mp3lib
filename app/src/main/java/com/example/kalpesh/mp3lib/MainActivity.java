package com.example.kalpesh.mp3lib;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.kalpesh.mp3listlib.Mp3List;

public class MainActivity extends Activity {
    static final int OPEN_MEDIA_PICKER = 1500;  // The request code
    TextView FilePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FilePath = (TextView)findViewById(R.id.filepath);

        findViewById(R.id.openmusic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), Mp3List.class);
                intent.putExtra("title","Select Mp3");
                startActivityForResult(intent,OPEN_MEDIA_PICKER);

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to

        if (requestCode == OPEN_MEDIA_PICKER) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK && data != null) {
                String result=data.getStringExtra("result");

                FilePath.setText(result);

               // Log.i("my","Result -> "+result);

            }
        }
    }


}



