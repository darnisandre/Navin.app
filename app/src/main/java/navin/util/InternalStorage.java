package navin.util;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class InternalStorage{
 
   private InternalStorage() {}
 
   public static void writeObject(Context context, String key, Object object)  {
       try {
           FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
           ObjectOutputStream oos = new ObjectOutputStream(fos);
           oos.writeObject(object);
           oos.close();
           fos.close();
       }catch (Exception e){

       }
   }
   public static void deleteObject(Context context, String key)  {
       context.deleteFile(key);
   }

   public static Object readObject(Context context, String key) {
      try {
         FileInputStream fis = context.openFileInput(key);
         ObjectInputStream ois = new ObjectInputStream(fis);
         Object object = ois.readObject();
         return object;
      }catch (Exception e){
         return null;
      }
   }
}