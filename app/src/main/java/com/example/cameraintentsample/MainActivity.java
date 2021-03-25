package com.example.cameraintentsample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    /* 保存された画像のURL */
    private Uri _imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // カメラアプリとの連携からの戻りで、かつ撮影成功の場合
        if (requestCode == 200 && resultCode == RESULT_OK) {
            ImageView ivCamera = findViewById(R.id.ivCamera);
            ivCamera.setImageURI(_imageUri);
        }
    }

    public void onCameraImageClick(View view) {
        // パーミッションチェック
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, 2000);
            return;
        }

        // 日時データを「yyyyMMddHHmmss」の形式に整形するフォーマッタ
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        // 現在日時
        Date now = new Date(System.currentTimeMillis());
        String nowStr = dateFormat.format(now);
        // ストレージに格納する画像のファイル名生成. タイムスタンプを利用して一意のファイル名にする
        String fileName = "UseCameraActivityPhoto_" + nowStr + ".jpg";

        // ContentValuesオブジェクト生成
        ContentValues values = new ContentValues();
        // 画像ファイル名設定
        values.put(MediaStore.Images.Media.TITLE, fileName);
        // 画像ファイルの種類を設定
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // ContentResolverオブジェクト生成
        ContentResolver resolver = getContentResolver();
        // ContentResolverを使ってURIオブジェクトを生成。新しいデータの格納先を確保し、それが表すUriオブジェクトを生成
        _imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        // Intentオブジェクト
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Extra情報として_imageUriを設定.
        // keyとしてEXTRA_OUTPUTを指定することで、撮影した画像ファイルをこのURLが表すストレージに保存される
        intent.putExtra(MediaStore.EXTRA_OUTPUT, _imageUri);

        startActivityForResult(intent, 200);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2000 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ImageView ivCamera = findViewById(R.id.ivCamera);
            onCameraImageClick(ivCamera);
        }
    }
}