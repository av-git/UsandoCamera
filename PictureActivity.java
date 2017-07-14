package com.example.avelino.usandocamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;

public class PictureActivity extends AppCompatActivity {


    String foto;
    ImageView imagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        Bundle bundle = getIntent().getExtras();
        foto = bundle.getString("foto");

        imagem = (ImageView)findViewById(R.id.imageView);

        ProcessingBitmap();
    }

    private void ProcessingBitmap(){
        try {
            Bitmap bm1 = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(foto)));
            imagem.setImageBitmap(bm1);
        }catch(FileNotFoundException fnex){
            Toast.makeText(getApplicationContext(), "Foto não encontrada!", Toast.LENGTH_LONG).show();
        }
    }
}

