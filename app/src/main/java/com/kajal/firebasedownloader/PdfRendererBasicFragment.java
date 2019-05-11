package com.kajal.firebasedownloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class PdfRendererBasicFragment extends Fragment implements View.OnClickListener{

    private static final String STATE_CURRENT_PAGE_INDEX = "current_page_index";
    private static final String FILENAME = "2019.pdf";

    private ParcelFileDescriptor mFileDescriptor;

    private PdfRenderer mPdfRenderer;

    private PdfRenderer.Page mCurrentPage;

    private ImageView mImageView;

    private Button mButtonPrevious;

    private Button mButtonNext;

    private int mPageIndex;

    public PdfRendererBasicFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pdf_renderer_basic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mImageView = view.findViewById(R.id.image);
        mButtonPrevious = view.findViewById(R.id.previous);
        mButtonNext = view.findViewById(R.id.next);

        mButtonPrevious.setOnClickListener(this);
        mButtonNext.setOnClickListener(this);

        mPageIndex = 0;

        if(null != savedInstanceState){
            mPageIndex = savedInstanceState.getInt(STATE_CURRENT_PAGE_INDEX,0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            openRenderer(getActivity());
            showPage(mPageIndex);
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(getActivity(),"Error! "+ e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop() {
        try {
            closeRenderer();
        }catch (IOException e){
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mCurrentPage){
            outState.putInt(STATE_CURRENT_PAGE_INDEX,mCurrentPage.getIndex());
        }
    }

    private void openRenderer(Context context) throws IOException{
        File file = new File(context.getFilesDir(),FILENAME);
        if(!file.exists()){
            InputStream asset = context.openFileInput(FILENAME);
            FileOutputStream output = new FileOutputStream(file);
            final  byte[] buffer = new byte[1024];
            int size;
            while ((size = asset.read(buffer)) != -1){
                output.write(buffer,0,size);
            }
            asset.close();
            output.close();
        }
        mFileDescriptor = ParcelFileDescriptor.open(file,ParcelFileDescriptor.MODE_READ_ONLY);
        if(mFileDescriptor != null){
            mPdfRenderer = new PdfRenderer(mFileDescriptor);
        }
    }

    private void closeRenderer() throws IOException {
        if(null != mCurrentPage){
            mCurrentPage.close();
        }
        mPdfRenderer.close();
        mFileDescriptor.close();
    }

    private void showPage(int index){
        if(mPdfRenderer.getPageCount() <= index){
            return;
        }
        if(null != mCurrentPage){
            mCurrentPage.close();
        }
        mCurrentPage = mPdfRenderer.openPage(index);

        Bitmap bitmap = Bitmap.createBitmap(mCurrentPage.getWidth(),
                mCurrentPage.getHeight(),
                Bitmap.Config.ARGB_8888);
        mCurrentPage.render(bitmap,null,null,
                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        mImageView.setImageBitmap(bitmap);
        updateUi();
    }

    private void updateUi(){
        int index = mCurrentPage.getIndex();
        int pageCount = mPdfRenderer.getPageCount();
        mButtonPrevious.setEnabled(0 != index);
        mButtonNext.setEnabled(index+1 < pageCount);
        getActivity().setTitle(getString(R.string.app_name_with_index,index+1,pageCount));
    }

    private int getPageCount(){
        return  mPdfRenderer.getPageCount();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.previous : {
                showPage(mCurrentPage.getIndex() - 1);
                break;
            }
            case R.id.next : {
                showPage(mCurrentPage.getIndex() + 1);
                break;
            }
        }
    }

}
