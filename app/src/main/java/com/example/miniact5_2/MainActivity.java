package com.example.miniact5_2;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    Handler h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.h = new Handler();
    }

    public void onDownloadWeb(View view) {
        final EditText eText = findViewById(R.id.editText);
        final TextView tView = findViewById(R.id.textView);

        tView.setText("");
        final Thread tr = new Thread(){
          public void run(){
              try{
                  URL url = new URL(eText.getText().toString());
                  URLConnection conn = url.openConnection();
                  BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                  String line;
                  int i = 0;
                  while(((line = rd.readLine()) != null) && i < 30){
                      MainActivity.this.h.post(new updateUIThread(line));
                      i++;
                  }
              } catch (MalformedURLException e) {
                  e.printStackTrace();
                  eText.setText("");
                  tView.setText(R.string.wrong_url);
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
        };
        tr.start();
    }

    class updateUIThread implements Runnable{
        private String msg;
        final TextView tView = findViewById(R.id.textView);

        public updateUIThread(String str){
            this.msg = str;
        }

        @Override
        public void run(){
            tView.append(msg);
        }
    }

    public void onDownloadImage(View view) {
        final EditText eText = findViewById(R.id.editText);
        final TextView tView = findViewById(R.id.textView);
        final ImageView iView = findViewById(R.id.image_holder);

        final Thread tr = new Thread(){
            public void run(){
                try{
                    URL url = new URL(eText.getText().toString());
                    URLConnection conn = url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream input = conn.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);

                    MainActivity.this.h.post(new updateUIThreadImage(myBitmap));

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    eText.setText("");
                    tView.setText(R.string.wrong_url);
                    iView.setImageBitmap(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        tr.start();
    }

    class updateUIThreadImage implements Runnable{
        private Bitmap image;
        final ImageView iView = findViewById(R.id.image_holder);

        public updateUIThreadImage(Bitmap bitmap){
            this.image = bitmap;
        }

        @Override
        public void run(){
            iView.setImageBitmap(image);
        }
    }

    public void onExampleUrl(View view) {
        final EditText eText = findViewById(R.id.editText);

        eText.setText(getText(R.string.example_url));
    }
}
