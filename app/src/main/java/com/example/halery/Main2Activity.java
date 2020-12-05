package com.example.halery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Main2Activity extends AppCompatActivity {
Bitmap selectedImage;
ImageView imageView;
EditText title,note;
Button buton;
SQLiteDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2); // layout bölümündeki hangi xmlin ait olduğunu belirtir.
        tanimla();

        database=this.openOrCreateDatabase("halery",MODE_PRIVATE,null);

        Intent intent=getIntent();
        String info=intent.getStringExtra("info"); // info bilgisinden eski mi yeni mi bilgisi geliyor.


        if(info.matches("old")){

            eski();
        }
        else{
            yeni();
        }


    }

    public void yeni(){

        title.setEnabled(true);
        note.setEnabled(true);
        imageView.setEnabled(true);
        buton.setVisibility(View.VISIBLE);

        title.setText("");
        note.setText("");
    }

    public void eski(){

        Intent intent=getIntent();

        int titleId2=intent.getIntExtra("titleId",1);//
        buton.setVisibility(View.INVISIBLE);

        try {
            database=this.openOrCreateDatabase("halery",MODE_PRIVATE,null);
            Cursor cursor = database.rawQuery("select * from imageTable where id=?", new String[] {String.valueOf(titleId2)});
              int  titleId=cursor.getColumnIndex("titles");
               int noteId=cursor.getColumnIndex("notes");
               int imageId=cursor.getColumnIndex("images");

                while (cursor.moveToNext()){
                    title.setText(cursor.getString(titleId));
                    note.setText(cursor.getString(noteId));

                    byte[] bytes=cursor.getBlob(imageId);
                    Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);// en son kısın datanın uzunluğu
                    imageView.setImageBitmap(bitmap);
                }
            Toast.makeText(getApplicationContext(),"Veriler geldi",Toast.LENGTH_LONG).show();
            cursor.close();

        }
        catch (Exception e){
    Toast.makeText(getApplicationContext(),"Veriler gelmedi ",Toast.LENGTH_LONG).show();
        }
        title.setEnabled(false);
        note.setEnabled(false);
        imageView.setEnabled(false);

    }


public void tanimla(){
        imageView=(ImageView)findViewById(R.id.imageView);
        title=(EditText)findViewById(R.id.edit1);
        note=(EditText)findViewById(R.id.edit2);
        buton=(Button)findViewById(R.id.buton1);
}

////////////////// Görsel için galeri izin alınması ve imageView'e eklenmesi başlatıldı //////////

    // İlk olarak: AndroidManifest.xml dosyasında; <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"></uses-permission> bu eklenti yazılacak.

    public void selectImage(View view){ // galeri için izin isteme fonksiyonu
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){ // izin var mı yok mu kontrol et. izin verilmediyse bakıyor

            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1); // izinleri iste, izinleri istedik

        }
        else{
  // izin var ve galeriyi açıyoruz
            Intent intentToGalery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // beni al galeriye götür. dosya uri alıyor
            startActivityForResult(intentToGalery,2);
        }
    }

    @Override                                                                                  // verilen değerler
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { // izin istenildikten sonra ne olacağı belirlenir.
        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){ // izin verildiyse
                Intent intentToGalery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // beni al galeriye götür. dosya uri alıyor
                startActivityForResult(intentToGalery,2); // kullanıcıyı alıp galeriye götüreceğiz.
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override                                                             // kullanıcı ne seçti(data)
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==2 && resultCode==RESULT_OK && data!=null){ // her şey tamamsa işleme geçebiliriz
           Uri imageData= data.getData();
           // bitmap'a çevireceğiz
            try {
                if(Build.VERSION.SDK_INT>=28){
                    ImageDecoder.Source soure=ImageDecoder.createSource(this.getContentResolver(),imageData); // ImageDecoder daha yeni bir sınıf
                    selectedImage=ImageDecoder.decodeBitmap(soure);
                    imageView.setImageBitmap(selectedImage);
                }
                else {
                    selectedImage=MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageData);
                    imageView.setImageBitmap(selectedImage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

////////////////// galeriden izin alındı ve görsel seçilerek imageView'de gösterildi.//////////////

    public void save(View view){  // veriler sqlite ile kaydedildi.

        String baslik=title.getText().toString();
        String not=note.getText().toString();

        Bitmap kucukGorsel=gorselKucult(selectedImage,300);

        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        kucukGorsel.compress(Bitmap.CompressFormat.PNG,50,outputStream);// görselin formatı,kalitesi,çeviriyoruz.
        byte[] byteArray= outputStream.toByteArray();

        try{
            database=this.openOrCreateDatabase("halery",MODE_PRIVATE,null);
             String sqlTable="Create table if not EXISTS imageTable(id INTEGER PRIMARY KEY AUTOINCREMENT, titles varchar, notes varchar,images BLOB,adminNAme varchar)";
             database.execSQL(sqlTable);
             String addSql="INSERT INTO imageTable(titles,notes,images) VALUES(?,?,?)"; // kullanıcı girişi yaparsam en sona da kullanıcı adı eklemem gerekecek. Ait görselleri çekmek için.
            SQLiteStatement statement=database.compileStatement(addSql);
            statement.bindString(1,baslik);// ilk soru işareti yani titles sütununa baslık editten gelen değeri atıyoruz
            statement.bindString(2,not);
            statement.bindBlob(3,byteArray); // byte dizisinden görseli alıyoruz
            statement.execute();// statment çalıştı.

            Toast.makeText(getApplicationContext(),"Veriler eklendi",Toast.LENGTH_LONG).show();

           /////


        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),"Veriler eklenemedi",Toast.LENGTH_LONG).show();
        }


        Intent intent=new Intent(Main2Activity.this,MainActivity.class);
        startActivity(intent);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() { // 4 saniye sonra main aktivity'i açıyor

               finish();
            }
        },3000);




    } // save biter.

    public Bitmap gorselKucult(Bitmap image,int maxSize){
        // verdiğimiz görseli maksimum değere göre küçüktecek.

        int width=image.getWidth();
        int height=image.getHeight();
            float bitmapRatio=(float)(width/height); // görsel oranını çıkarıyoruz

                                                  //   _______________
         if(bitmapRatio>1){ // görsel yataydır    //   |             |   yatay.
            width=maxSize;                       //    |_____________|
            height=(int)(width/bitmapRatio);
        }
        else{
            height=maxSize;// görsel dikeydir
            width=(int)(height/bitmapRatio);
        }


        return Bitmap.createScaledBitmap(image,width,height,true);// hangi görsel,genişlik,yükseklik,filtre
    }


}
