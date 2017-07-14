package com.example.avelino.usandocamera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button btnNovaFoto, btnExportar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNovaFoto = (Button) findViewById(R.id.btnNovaFoto);
        btnNovaFoto.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {

                //VERIFICA SE O USUARIO DEU PERMISSAO PARA O USO DA CAMERA. ATENDE API > 22
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
                } else {
                    dispararIntentCamera();
                }
                return;
            }
        });
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    static String mCurrentPhotoPath;
    String caminhoDaFoto;

    private void dispararIntentCamera() {

        //chamaIntentCameraParaVersao22();
        chamaIntentCameraParaVersaoMenorOuIgualAPI_22();
        //chamaIntentCameraParaVersaoAcima23();
    }

    private void chamaIntentCameraParaVersaoAcima23() {

        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        caminhoDaFoto = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
        File arquivoFoto = new File(caminhoDaFoto);
        Uri fotoURI = FileProvider.getUriForFile(MainActivity.this, "com.example.avelino.usandocamera.fileprovider", arquivoFoto);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, fotoURI);
        if (intentCamera.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intentCamera, REQUEST_IMAGE_CAPTURE);
        }

    }

    private void chamaIntentCameraParaVersaoMenorOuIgualAPI_22() {

        Intent tirarFotoCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //TODO NOTA: getExternalFilesDir e responsavel em armazernar em um pasta propria da app a imagem
        caminhoDaFoto = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
        //caminhoDaFoto = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + System.currentTimeMillis() + ".jpg";
        File arquivoFoto = new File(caminhoDaFoto);
        tirarFotoCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(arquivoFoto));

        startActivityForResult(tirarFotoCameraIntent, REQUEST_TAKE_PHOTO);
    }

    /**
     * Metodo que funciona para API 22
     */
    private void chamaIntentCameraParaVersao22() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.fillInStackTrace();
                ex.printStackTrace();
                Toast.makeText(getApplicationContext(), "Erro ao tirar a foto", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("PHOTOAPP", ".jpg", storageDir);
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            //Bitmap bm1 = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(foto))); // TODO AV TESTAR ESSA FORMA

            ImageView foto = (ImageView) findViewById(R.id.imageView);
            Bitmap bitmap = BitmapFactory.decodeFile(caminhoDaFoto);
            foto.setImageBitmap(bitmap);
            adicionarImagemNaGaleria();
        }
    }

    private void adicionarImagemNaGaleria() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(caminhoDaFoto);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
