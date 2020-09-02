package com.womp.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

public class FullView extends AppCompatActivity {

    Bitmap bit_img;
    File caminho;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_view);

        PhotoView imageView = findViewById(R.id.imgfull);


        Bundle extras = getIntent().getExtras();
        if(extras != null){
            caminho = (File) extras.get("caminho");
            bit_img = BitmapFactory.decodeFile(caminho.getAbsolutePath());
            float graus = 90;
            Matrix matrix = new Matrix();
            matrix.setRotate(graus);
            Bitmap newBitmapRotate = Bitmap.createBitmap(bit_img, 0,0, bit_img.getWidth(),bit_img.getHeight(),matrix,true);
            imageView.setImageBitmap(newBitmapRotate);
        }
    }
}