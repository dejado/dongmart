package kr.co.softcampus.camera;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    String[] permsission_list = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    //이미지가 저장될 경로를 가지고 있는 변수
    String dir_path;
    //저장된 이미지에 접근할 수 있는 uri
    Uri contentUri;
    //이미지 주소값
    ImageView image1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image1 = (ImageView) findViewById(R.id.imageView);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permsission_list, 0);
        } else {
            init();
        }
    }

    public void getImageBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int a1 : grantResults) {
            if (a1 == PackageManager.PERMISSION_DENIED) {
                return;
            }
        }
        init();
    }

    public void init() {
        String temp_path = Environment.getExternalStorageDirectory().getAbsolutePath();
        dir_path = temp_path + "/android/data/" + getPackageName();

        File file = new File(dir_path);
        if (file.exists() == false) {
            file.mkdir();
        }
    }

    public void startCameraBtn(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String file_name = "/temp_" + System.currentTimeMillis() + ".jpg";
        String pic_path = dir_path + file_name;

        File file = new File(pic_path);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            contentUri = FileProvider.getUriForFile(this, "kr.co.softcampus.cameraoriginal.file_provider", file);
        } else {
            contentUri = Uri.fromFile(file);
        }
        contentUri = Uri.fromFile(file);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
                if (resultCode ==1) {
                    Uri uri = data.getData();
                    ContentResolver resolver = getContentResolver();
                    Cursor cursor = resolver.query(uri, null, null, null, null);
                    cursor.moveToNext();

                    int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    String source = cursor.getString(index);

                    Bitmap bitmap = (BitmapFactory.decodeFile(source));
                    image1.setImageBitmap(bitmap);
                }
                else if(requestCode==2){
                    Bitmap bitmap2 = (BitmapFactory.decodeFile(contentUri.getPath()));
                    image1.setImageBitmap(bitmap2);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



