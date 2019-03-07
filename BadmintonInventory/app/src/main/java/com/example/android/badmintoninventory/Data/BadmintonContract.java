package com.example.android.badmintoninventory.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static android.text.style.TtsSpan.GENDER_FEMALE;
import static android.text.style.TtsSpan.GENDER_MALE;

/**
 * Created by yuxia on 10/27/16.
 */

public class BadmintonContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.badmintoninventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEMS = "badminton";

    public static abstract class BadmintonEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        public static final String TABLE_NAME = "badminton";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_DESCRIPTION = "description";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_MANUFACTURER = "manufacturer";
        public static final String COLUMN_PRODUCT_IMAGE_URI = "image";


        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_ITEMS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_ITEMS;


    }

}