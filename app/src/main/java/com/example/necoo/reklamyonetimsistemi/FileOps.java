package com.example.necoo.reklamyonetimsistemi;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * Created by NECOO on 10.04.2019.
 */

public class FileOps {
    public void write(Context context, String fileName, String data){
        if(context.getFileStreamPath(fileName).exists()){
            System.out.println("Dosya vardı silindi");
            File file = context.getFileStreamPath(fileName);
            file.delete();
        }
        try{
            System.out.println("Dosya yoktu oluşturuldu");
            FileOutputStream fos=context.openFileOutput(fileName,Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String read(Context context,String fileName){
        String sonuc="";
        String satir;
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr=new InputStreamReader(fis);
            BufferedReader br=new BufferedReader(isr);
            while((satir=br.readLine())!=null){
                sonuc = sonuc + satir;
            }
            fis.close();
        }catch (Exception e){
            e.printStackTrace();
           // return null;
        }
        return sonuc;
    }
}
