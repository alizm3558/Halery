package com.example.halery;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {
// not: Splash ekranını AndroidManifest.xmlden ilk açılan aktivity olarak ayarla. //
// Daha sonrasında gerçek anasayfayı Splash aktivitysinden handler ile zaman vererek intent et.
    // Handler=>androis.os olan
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // anasayfa yapıldı
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() { // 4 saniye sonra main aktivity'i açıyor

                Intent intent=new Intent(Splash.this,MainActivity.class);
                startActivity(intent);
            }
        },4000);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() { // 6 saniye sonra da splash ekranını kapatıyorum :)

            finish();
            }
        },6000);



    }
}
