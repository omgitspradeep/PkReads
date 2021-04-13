package com.example.pkreads;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pkreads.utils.MyPreferences;
import com.example.pkreads.utils.ResourceManager;
import com.folioreader.FolioReader;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {



    ListView lv_pdf;
    public static ArrayList<File> fileList = new ArrayList<File>();
    PDFAdapter obj_adapter;
    public static int REQUEST_PERMISSIONS = 1;
    boolean boolean_permission;
    File dir;
    String TAG="PdfActivity";
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.e(TAG, MyPreferences.getIntPrefrences("lastPage",this)+1+"");
        Log.e(TAG, MyPreferences.getStringPrefrences("lastBook",this));

        if(ResourceManager.getAllFiles().isEmpty()){
            Log.e(TAG,"ALL FILES EMPTY" );
        }else{
            Log.e(TAG,"All files Not empty" );

        }

        init();




    }

    private void init() {

        lv_pdf = (ListView) findViewById(R.id.lv_pdf);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        fn_permission();


        lv_pdf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ResourceManager.setAllFiles(fileList);
                ResourceManager.setSelectedFile(fileList.get(i));


                if(!ResourceManager.isItEPUBFile(fileList.get(i).getName())){

                    Intent intent = new Intent(getApplicationContext(), PDFReader.class);
                    startActivity(intent);

                    Log.e(TAG, i + "");
                    Log.e(TAG,  fileList.get(i).getAbsolutePath()+ "");
                }else{

                    MyPreferences.setStringPrefrences("lastBook",ResourceManager.getFile().getName(),MainActivity.this);

                // RUN this code if file is EPUB

                  //  Intent intent = new Intent(getApplicationContext(), EPUBReader.class);
                  //  startActivity(intent);
                    try{
                        FolioReader folioReader = FolioReader.get();
                        folioReader.openBook(ResourceManager.getFile().getAbsolutePath());
                        Toast.makeText(MainActivity.this, "If you start, finish it. Otherwise, don't start", Toast.LENGTH_LONG).show();
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this,"File too short to open or it is a zip file.",Toast.LENGTH_LONG).show();
                    }



                 //   wb.getSettings().setJavaScriptEnabled(true);


                }

            }
        });
        progressBar.setVisibility(View.GONE);

    }


    // Looks for all directories, stores all pdf file in array "fileList" and returns it.
    public ArrayList<File> getfile(File dir) {

        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {
                String filename = listFile[i].getName();

                if (listFile[i].isDirectory()) {
                    getfile(listFile[i]);

                } else {

                    boolean pdfOrEpub = false;
                    if (filename.endsWith(".pdf") || filename.endsWith(".epub")) {

                        for (int j = 0; j < fileList.size(); j++) {
                            if (fileList.get(j).getName().equals(filename)) {
                                pdfOrEpub = true;
                            } else {}
                        }

                        if (pdfOrEpub) {
                            pdfOrEpub = false;
                        } else {

                            if(listFile[i].getName().equals(MyPreferences.getStringPrefrences("lastBook",this))){
                                fileList.add(0,listFile[i]);
                            }else{
                                fileList.add(listFile[i]);
                            }

                        }
                    }
                }
            }
        }
        return fileList;
    }




    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
            }
        } else {
            boolean_permission = true;

            if(ResourceManager.getAllFiles().isEmpty()){
                getfile(dir);
            }else{
                fileList= ResourceManager.getAllFiles();
            }

            if(!fileList.isEmpty()){
                obj_adapter = new PDFAdapter(getApplicationContext(), fileList);
                lv_pdf.setAdapter(obj_adapter);
            }else{
                Toast.makeText(this, "No PDF or EPUB files to list ", Toast.LENGTH_LONG).show();
            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                boolean_permission = true;

                if(ResourceManager.getAllFiles().isEmpty()){
                    getfile(dir);
                }else{
                    fileList= ResourceManager.getAllFiles();
                }

                if(!fileList.isEmpty()){
                    obj_adapter = new PDFAdapter(getApplicationContext(), fileList);
                    lv_pdf.setAdapter(obj_adapter);
                }else{
                    Toast.makeText(this, "No PDF or EPUB files to list ", Toast.LENGTH_LONG).show();
                }

            } else {

                Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("TESTING","I a onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("TESTING","I a onStart");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("TESTING","I a onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("TESTING","I a onDestroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("TESTING","I a onStop");
    }

    protected void onRestart() {
        super.onRestart();
        Log.e("TESTING","I a onRestart");
    }
}