package com.example.pkreads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pkreads.utils.ResourceManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class PDFAdapter extends ArrayAdapter<File> {
    Context context;
    ViewHolder viewHolder;
    ArrayList<File> al_pdf;
    String pdfPages="";
    ParcelFileDescriptor parcelFileDescriptor = null;

    public PDFAdapter(Context context, ArrayList<File> al_pdf) {
        super(context, R.layout.adapter_pdf, al_pdf);
        this.context = context;
        this.al_pdf = al_pdf;

    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }



    @Override
    public int getViewTypeCount() {
        if (al_pdf.size() > 0) {
            return al_pdf.size();
        } else {
            return 1;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_pdf, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.fileImage=(ImageView) view.findViewById(R.id.iv_image);
            viewHolder.tv_filename = (TextView) view.findViewById(R.id.tv_name);
            viewHolder.tv_totalPages = (TextView) view.findViewById(R.id.tv_pages);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();

        }



        // Inflating views with data.

        viewHolder.tv_filename.setText(al_pdf.get(position).getName());

        if(ResourceManager.isItEPUBFile(al_pdf.get(position).getName())){
            viewHolder.fileImage.setImageResource(R.drawable.epub);
        }else{
            try {
                parcelFileDescriptor = ParcelFileDescriptor.open(al_pdf.get(position), ParcelFileDescriptor.MODE_READ_ONLY);
                pdfPages= String.valueOf(new PdfRenderer(parcelFileDescriptor).getPageCount());

            } catch (IOException e) {
                e.printStackTrace();
            }

            viewHolder.tv_totalPages.setText(Math.floor(al_pdf.get(position).length()*0.000001)+"MB" +"        "+pdfPages+" pages");
        }


        return view;

    }


    public class ViewHolder {
        TextView tv_filename,tv_totalPages;
        ImageView fileImage;
    }


}