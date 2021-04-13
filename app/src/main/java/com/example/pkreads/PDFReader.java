package com.example.pkreads;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.example.pkreads.utils.MyPreferences;
import com.example.pkreads.utils.ResourceManager;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.util.List;

public class PDFReader extends AppCompatActivity implements OnPageChangeListener{

    PDFView pdfView;
    String TAG="PdfActivity";
    File file;
    String filename;
    int pageRead=0;
    int defaultPage=0;
    boolean swipeLeftToRight=true; // true :left to right, false: up to down

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader);
        file= ResourceManager.selectedFile;
        filename = (file.getName().length() > 22) ? file.getName().substring(0,22)+"..": file.getName();


        //getAllTheContentData();

        if(MyPreferences.getStringPrefrences("lastBook",this).equals(file.getName())){
            defaultPage =MyPreferences.getIntPrefrences("lastPage",this);
        }

        pdfView = findViewById(R.id.pdfView);
        pdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(true)
                .enableDoubletap(true)
                .defaultPage(defaultPage)
                .enableAnnotationRendering(false)
                .password(null)
                .onPageChange(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .enableAntialiasing(true)
                .spacing(0)
                .pageFitPolicy(FitPolicy.WIDTH)
                .load();



    }


    @Override
    public void onPageChanged(int page, int pageCount) {
        setTitle(String.format("%s %s / %s", filename, page + 1, pageCount));
        pageRead=page;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyPreferences.setIntPrefrences("lastPage", pageRead,this);
        MyPreferences.setStringPrefrences("lastBook",ResourceManager.getFile().getName(),PDFReader.this);

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        swipeLeftToRight=!swipeLeftToRight;
        changeReadingStyle();
        Log.e(TAG, " IS IT LEFT TO RIGHT ? :"+swipeLeftToRight);

        return super.onKeyUp(keyCode, event);

    }


    private void changeReadingStyle() {
        pdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(swipeLeftToRight)
                .enableDoubletap(true)
                .defaultPage(pageRead)
                .enableAnnotationRendering(false)
                .password(null)
                .onPageChange(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .enableAntialiasing(true)
                .spacing(0)
                .pageFitPolicy(FitPolicy.WIDTH)
                .load();
    }


    // This method gives all the information within pdf files like... Bold headers, Chapters, Glossary, Indexes etc.
    private void getAllTheContentData() {
        printBookmarksTree(pdfView.getTableOfContents(),"-");
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, page %d", sep, b.getTitle(), b.getPageIdx())); // EX: Chapter 1: Introduction, page 15

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

}

