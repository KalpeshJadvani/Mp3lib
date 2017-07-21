package com.example.kalpesh.mp3listlib.Libutility;

/**
 * Created by omsai on 4/24/2017.
 */

public class ObjectArrary {
      private SongDetail songDetail;
      private int ObjNo;

      public ObjectArrary (SongDetail Detail,int no){
      this.songDetail=Detail;
      this.ObjNo=no;
      }

     public SongDetail getSongDerail(){
         return songDetail;
     }

    public int getObjNo(){
        return ObjNo;
    }


}
