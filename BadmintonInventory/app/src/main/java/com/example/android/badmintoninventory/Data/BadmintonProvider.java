package com.example.android.badmintoninventory.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.android.badmintoninventory.Data.BadmintonContract.BadmintonEntry;

import static android.R.attr.data;
import static com.example.android.badmintoninventory.R.id.price;

/**
 * Created by yuxia on 10/27/16.
 */

public class BadmintonProvider extends ContentProvider {
    public static final String LOG_TAG = BadmintonProvider.class.getSimpleName();
    private BadmintonDbHelper mDbHelper;
    private static final int BADMINTON = 100;
    private static final int BADMINTON_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(BadmintonContract.CONTENT_AUTHORITY, BadmintonContract.PATH_ITEMS, BADMINTON);
        sUriMatcher.addURI(BadmintonContract.CONTENT_AUTHORITY, BadmintonContract.PATH_ITEMS +"/#", BADMINTON_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new BadmintonDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch(match){
            case BADMINTON:
                cursor = database.query(BadmintonEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case BADMINTON_ID:
                selection = BadmintonEntry._ID +"=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BadmintonEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BADMINTON:
                return BadmintonEntry.CONTENT_LIST_TYPE;
            case BADMINTON_ID:
                return BadmintonEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BADMINTON:
                return insertItem(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    private Uri insertItem(Uri uri, ContentValues values) {
        String name = values.getAsString(BadmintonEntry.COLUMN_PRODUCT_NAME);
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Item require a name");
        }
        Integer price = values.getAsInteger(BadmintonEntry.COLUMN_PRODUCT_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Item requires valid price");
        }
        Integer quantity = values.getAsInteger(BadmintonEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Item requires valid quantity");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(BadmintonEntry.TABLE_NAME, null, values);
        if(id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);


    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowDeleted;
        switch (match) {
            case BADMINTON:
                rowDeleted = database.delete(BadmintonEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BADMINTON_ID:
                selection = BadmintonEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowDeleted = database.delete(BadmintonEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BADMINTON:
                return updateItem(uri, values, selection, selectionArgs);
            case BADMINTON_ID:
                selection = BadmintonEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    public int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //check the name is not null;
        if (values.containsKey(BadmintonEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(BadmintonEntry.COLUMN_PRODUCT_NAME);
            if(name == null || name.isEmpty()) {
                Toast.makeText(getContext(), "Name is required", Toast.LENGTH_SHORT).show();
                throw new IllegalArgumentException("Item requires a name");
            }
        }
        //check the price value is valid
        if (values.containsKey(BadmintonEntry.COLUMN_PRODUCT_PRICE)){
            Double price =values.getAsDouble(BadmintonEntry.COLUMN_PRODUCT_PRICE);
            if(price != null && price < 0) {
                Toast.makeText(getContext(), "Price is required", Toast.LENGTH_SHORT).show();
                throw new IllegalArgumentException("Item requires a valid price");
            }
        }
        //check the quantity value is valid
        if (values.containsKey(BadmintonEntry.COLUMN_PRODUCT_QUANTITY)){
            Integer quantity = values.getAsInteger(BadmintonEntry.COLUMN_PRODUCT_QUANTITY);
            if(quantity != null && quantity < 0) {
                Toast.makeText(getContext(), "quantity is required", Toast.LENGTH_SHORT).show();
                throw new IllegalArgumentException("Item requires a valid quantity");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowUpdated = database.update(BadmintonEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowUpdated;
    }
}
