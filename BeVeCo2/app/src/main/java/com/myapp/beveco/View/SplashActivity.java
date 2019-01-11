package com.myapp.beveco.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.myapp.beveco.Main.MainActivity;


public class SplashActivity extends Activity {
    //앱 시작 화면

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        try{
            //앱의 시작 화면을 2.5초동안 유지
            Thread.sleep(2500);
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
