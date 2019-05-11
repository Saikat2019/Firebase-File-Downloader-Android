package com.kajal.firebasedownloader;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MainActivity extends AppCompatActivity {

    Button Down;
    Button btn_viewPdf;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    StorageReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("XXXMainActivity", "1");

        Down = findViewById(R.id.down);
        btn_viewPdf = findViewById(R.id.btn_pdf);

        Down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download();
            }
        });

        btn_viewPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("XXXMainActivity", "2");
                Intent intent = new Intent(MainActivity.this,PdfViewActivity.class);
                Log.d("XXXMainActivity", "3");
                startActivity(intent);
                Log.d("XXXMainActivity", "4");
            }
        });
    }

    public void download(){
        storageReference = firebaseStorage.getInstance().getReference();
        ref = storageReference.child("2019.pdf");

        Log.d("XXXMainActivity", "5");
        final long ONE_MB = 1024 * 1024;
        final long FIFTY_MB = 50 *1024 *1024;
        Log.d("XXXMainActivity", "6");
/*comment from here to*/
        ref.getBytes(FIFTY_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.d("XXXMainActivity", "7");
                try {
                    FileOutputStream fileOutputStream = openFileOutput("2019.pdf",Context.MODE_PRIVATE);
                    fileOutputStream.write(bytes);
                    fileOutputStream.close();
                    Log.d("XXXMainActivity", "8");
                    Toast.makeText(MainActivity.this,"Download successful",Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    Log.d("XXXMainActivity", "9");
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,"File not found",Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Log.d("XXXMainActivity", "10");
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,"IOException",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("XXXMainActivity", "11");
                Toast.makeText(MainActivity.this,"onFailure - some error occured",Toast.LENGTH_SHORT).show();
            }//
        });//
/*upto here*/

        Log.d("XXXMainActivity", "12");

//        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                String url = uri.toString();
//                downloadFiles(MainActivity.this,"2019",".pdf",url);
//                Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_SHORT).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(MainActivity.this,"Failed : some error occured",Toast.LENGTH_SHORT).show();
//            }
//        });
    }


    public void downloadFiles(Context context,String fileName,String fileExtension, String url){
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        Log.d("XXXMainActivity", "13");
        DownloadManager.Request request = new DownloadManager.Request(uri);
        Log.d("XXXMainActivity", "14");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        Log.d("XXXMainActivity", "15");
        request.setDestinationInExternalFilesDir(context, Environment.getDataDirectory().toString(),fileName+fileExtension);
        Log.d("XXXMainActivity", "16");
        downloadManager.enqueue(request);
        Log.d("XXXMainActivity", "17");
    }
}
