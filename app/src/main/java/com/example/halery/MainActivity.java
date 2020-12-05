package com.example.halery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
SQLiteDatabase database;
    ArrayAdapter arrayAdapter;
    ListView listView;
    ArrayList<String> titleArray;
    ArrayList<Integer> idArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView=(ListView)findViewById(R.id.listview);
        titleArray=new ArrayList<>();
        idArray=new ArrayList<>();
        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,titleArray); // sadece Stringleri gösterir/ tittleArray içerisindeki verileri gösterir.

        veriGetir();
        listView.setAdapter(arrayAdapter); // adapter sayesinde veriler listview'de gözükecektir.

 listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
     @Override
     public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


         Intent intent= new Intent(MainActivity.this,Main2Activity.class);
         intent.putExtra("titleId",idArray.get(position)); // intent.putExtra( "artId",idArray.get(position));
         intent.putExtra("info","old"); // ikinci aktivity'e info değeri gönderildi.
         startActivity(intent);
     }
 });
    }





    public void veriGetir(){
        try {
            database=this.openOrCreateDatabase("halery",MODE_PRIVATE,null);


            Cursor cursor=database.rawQuery("select * from imageTable",null);
            int titleId=cursor.getColumnIndex("titles");
            int idId=cursor.getColumnIndex("id");

            while (cursor.moveToNext()){

                titleArray.add(cursor.getString(titleId));
                idArray.add(cursor.getInt(idId));
            }
            arrayAdapter.notifyDataSetChanged();
            cursor.close();

        }
        catch (Exception e){

            Toast.makeText(getApplicationContext(),"Veriler yüklenemedi!",Toast.LENGTH_LONG).show();
        }
    }

    // menu 'yü tanıtma
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // hangi menüyü göstereceğiz. Menüyü aktivitye bağlıyoruz
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.ekle,menu); // tanıtıldı

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // itemı seçerse ne yapacağımızı velirleriz
        if(item.getItemId()==R.id.ekle_item){ // ekle_item'a tıklandıysa
            Intent intent=new Intent(MainActivity.this,Main2Activity.class);

            intent.putExtra("info","new"); // yeni veri kaydedilen sayfa açılsın
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }
    // menu kısmı biter.
}
