package com.fsdm.wisd.smartdoor;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.List;

public class PersonGroupData {
    public Context context;
    public String PersonGroupId,PersonGroupName,PersonName;
    public List<Bitmap> bitmaps;

    PersonGroupData(Context _context,String _PersonGroupId, String _PersonGroupName, String _PersonName,List<Bitmap> _bitmaps){
        context = _context;
        PersonGroupId = _PersonGroupId;
        PersonGroupName = _PersonGroupName;
        PersonName = _PersonName;
        bitmaps = _bitmaps;
    }
}

