package com.example.android.badmintoninventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.badmintoninventory.Data.BadmintonContract.BadmintonEntry;

import static android.R.attr.id;
import static android.R.attr.y;

/**
 * Created by yuxia on 10/27/16.
 */

public class BadmintonCursorAdapter extends CursorAdapter {


    public BadmintonCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(BadmintonEntry._ID));
        TextView nameText = (TextView) view.findViewById(R.id.name);
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        TextView priceText = (TextView) view.findViewById(R.id.price);
        String price = cursor.getString(cursor.getColumnIndexOrThrow("price"));
        TextView quantityText = (TextView) view.findViewById(R.id.quantity);
        String quantity = cursor.getString(cursor.getColumnIndexOrThrow("quantity"));

        nameText.setText(name);
        priceText.setText(price);
        quantityText.setText(quantity);

        final int itemQty = Integer.parseInt(quantity);


        Button saleButton = (Button) view.findViewById(R.id.sale);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                if (itemQty > 0) {
                    int mItemQty = itemQty - 1;
                    values.put(BadmintonEntry.COLUMN_PRODUCT_QUANTITY, mItemQty);
                    Uri uri = ContentUris.withAppendedId(BadmintonEntry.CONTENT_URI, itemId);
                    context.getContentResolver().update(uri, values, null, null);
                } else if (itemQty == 0) {
                    Toast.makeText(context, "There is no more item to sale.", Toast.LENGTH_SHORT).show();
                }
                context.getContentResolver().notifyChange(BadmintonEntry.CONTENT_URI, null);
            }
        });
    }

}
