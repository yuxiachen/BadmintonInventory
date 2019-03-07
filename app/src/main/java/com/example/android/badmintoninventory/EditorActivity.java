package com.example.android.badmintoninventory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.badmintoninventory.Data.BadmintonContract;
import com.example.android.badmintoninventory.Data.BadmintonContract.BadmintonEntry;

import java.io.FileDescriptor;
import java.io.IOException;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.provider.LiveFolders.INTENT;
import static android.view.View.GONE;
import static com.example.android.badmintoninventory.Data.BadmintonProvider.LOG_TAG;
import static com.example.android.badmintoninventory.R.id.price;
import static com.example.android.badmintoninventory.R.id.quantity;
import static com.example.android.badmintoninventory.R.id.sale;

/**
 * Created by yuxia on 10/27/16.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private EditText mNameText;
    private EditText mDescriptionText;
    private EditText mPriceText;
    private EditText mQuantityText;
    private EditText mManufacturerText;
    private ImageView mPictureView;
    private Uri mCurrentImageUri = null;
    private static final int EXISTING_ITEM_LOADER = 0;

    private Uri mCurrentItemUri;
    private boolean itemHasChanged = false;
    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Button saleButton = (Button)findViewById(R.id.sale_button);
        Button receiveButton = (Button)findViewById(R.id.receive_button);
        Button OrderButton = (Button)findViewById(R.id.order_more);
        final TextView saleNumberText = (TextView)findViewById(R.id.sale_number);
        final TextView receiveNumberText = (TextView)findViewById(R.id.receive_number);
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        if(mCurrentItemUri == null) {
            setTitle(getString(R.string.editor_title_add_a_item));
            saleButton.setVisibility(GONE);
            receiveButton.setVisibility(GONE);
            saleNumberText.setVisibility(GONE);
            receiveNumberText.setVisibility(GONE);
        } else {
            setTitle(getString(R.string.editor_title_edit_item));
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        mNameText = (EditText)findViewById(R.id.name_edit_text);
        mDescriptionText = (EditText)findViewById(R.id.description_edit_text);
        mPriceText = (EditText)findViewById(R.id.price_edit_text);
        mQuantityText = (EditText)findViewById(R.id.quantity_edit_text);
        mManufacturerText = (EditText)findViewById(R.id.manufacturer_edit_text);
        mPictureView = (ImageView) findViewById(R.id.product_picture);

        //set the touch listener to all the views
        mNameText.setOnTouchListener(mTouchListener);
        mDescriptionText.setOnTouchListener(mTouchListener);
        mPriceText.setOnTouchListener(mTouchListener);
        mQuantityText.setOnTouchListener(mTouchListener);
        mManufacturerText.setOnTouchListener(mTouchListener);
        mPictureView.setOnTouchListener(mTouchListener);

        //set on click listener on the ImageView to start insert an image
        mPictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                }
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        //Implement the sale button
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(mQuantityText.getText().toString());
                //set the default increase number
                int saleNumber = 1;
                String mSaleNumberText =  saleNumberText.getText().toString();
                //parse the string only when it's not empty.
                if(mSaleNumberText != null && !mSaleNumberText.isEmpty() ){
                    saleNumber = Integer.parseInt(mSaleNumberText);}

                if(quantity >= saleNumber) {
                    quantity = quantity - saleNumber;
                    ContentValues values = new ContentValues();
                    values.put(BadmintonEntry.COLUMN_PRODUCT_QUANTITY, quantity);
                    int rowUpdated = getContentResolver().update(mCurrentItemUri, values, null, null);
                    if (rowUpdated != 0) {
                        mQuantityText.setText(Integer.toString(quantity));
                    }
                }
            }
        });
        //Implement the receive button
        receiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(mQuantityText.getText().toString());
                //set the default increase number
                int receiveNumber = 1;
                String mReceiveNumber = receiveNumberText.getText().toString();
                //parse the string only when it's not empty.
                if(mReceiveNumber != null && !mReceiveNumber.isEmpty() ){
                receiveNumber = Integer.parseInt(mReceiveNumber);}
                quantity =  quantity + receiveNumber;
                ContentValues values = new ContentValues();
                values.put(BadmintonEntry.COLUMN_PRODUCT_QUANTITY, quantity);
                int rowUpdated = getContentResolver().update(mCurrentItemUri, values, null, null);
                if (rowUpdated != 0) {
                    mQuantityText.setText(Integer.toString(quantity));
                }
            }
        });
        //Implement the order button
        OrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mNameText.getText().toString();
                String orderMessage = "Need to order " + name + " \nThank You!";
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_TEXT, orderMessage);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Badminton Product Order");
                if (intent.resolveActivity(getPackageManager())!= null) {
                    startActivity(intent);
                }

            }
        });
    }

    //select an image


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                mCurrentImageUri = data.getData();
                mPictureView.setImageBitmap(getBitmapFromUri(mCurrentImageUri));
            }
        }
    }
    private Bitmap getBitmapFromUri(Uri uri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally{
            try{
                if(parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Error with closing ParcelFile Descriptor");
            }
        }

    }

    //call the alert dialog when the back button is press.
    @Override
    public void onBackPressed() {
        if (!itemHasChanged){
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
             finish();
            }
        };
        showSaveConfirmationDialog(discardButtonClick);
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            itemHasChanged = true;
            return false;
        }
    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_save:
                if(!itemHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                //check that the name is not null.
                String name = mNameText.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
                    return false;
                }
                //check that the price is not empty and its value is valid.
                String price = mPriceText.getText().toString().trim();
                if (price.isEmpty() || Double.parseDouble(price) <= 0) {
                    Toast.makeText(this, "Item needs a valid price", Toast.LENGTH_SHORT).show();
                    return false;
                }

                //check that the quantity is not empty and its value is valid.
                String quantity = mQuantityText.getText().toString().trim();
                if (quantity.isEmpty() || Integer.parseInt(quantity) < 0) {
                    Toast.makeText(this, "Item needs a valid quantity", Toast.LENGTH_SHORT).show();
                    return false;
                }
                saveItem();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                if(!itemHasChanged){
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                showSaveConfirmationDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void saveItem(){
        String name = mNameText.getText().toString().trim();
        String description = mDescriptionText.getText().toString().trim();
        String mPrice = mPriceText.getText().toString().trim();
        String mQuantity = mQuantityText.getText().toString().trim();
        String manufacturer = mManufacturerText.getText().toString().trim();
        String imageUri = null;

        Double price = 0.0;
        if (!TextUtils.isEmpty(mPrice)) {
            price = Double.parseDouble(mPrice);
        }
        int quantity = 0;
        if (!TextUtils.isEmpty(mQuantity)) {
            quantity = Integer.parseInt(mQuantity);
        }

        if (mCurrentImageUri != null) {
            imageUri = mCurrentImageUri.toString();
        }

        if (mCurrentItemUri == null && TextUtils.isEmpty(name) && TextUtils.isEmpty(description)
                && TextUtils.isEmpty(mPrice) && TextUtils.isEmpty(mQuantity) &&
                TextUtils.isEmpty(manufacturer) && TextUtils.isEmpty(imageUri)) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(BadmintonEntry.COLUMN_PRODUCT_NAME, name);
        values.put(BadmintonEntry.COLUMN_PRODUCT_DESCRIPTION, description);
        values.put(BadmintonEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(BadmintonEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(BadmintonEntry.COLUMN_PRODUCT_MANUFACTURER, manufacturer);
        values.put(BadmintonEntry.COLUMN_PRODUCT_IMAGE_URI, imageUri);

        if (mCurrentItemUri == null) {
            Uri newUri = getContentResolver().insert(BadmintonEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_item_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_item_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowUpdated = getContentResolver().update(mCurrentItemUri, values, null, null);
            if (rowUpdated == 0) {
                Toast.makeText(this, getString(R.string.editor_update_item_failed), Toast.LENGTH_SHORT).show();
            } else Toast.makeText(this, getString(R.string.editor_update_item_successful), Toast.LENGTH_SHORT).show();
        }
    }
    private void showSaveConfirmationDialog(DialogInterface.OnClickListener discardButtonClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteItem() {
        if (mCurrentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_item_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_item_successful), Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    private void showDeleteConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this item?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BadmintonEntry.COLUMN_PRODUCT_NAME,
                BadmintonEntry.COLUMN_PRODUCT_DESCRIPTION,
                BadmintonEntry.COLUMN_PRODUCT_PRICE,
                BadmintonEntry.COLUMN_PRODUCT_QUANTITY,
                BadmintonEntry.COLUMN_PRODUCT_MANUFACTURER,
                BadmintonEntry.COLUMN_PRODUCT_IMAGE_URI};
        return new CursorLoader(this,
                mCurrentItemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(BadmintonEntry.COLUMN_PRODUCT_NAME);
            int descriptionColumnIndex = cursor.getColumnIndex(BadmintonEntry.COLUMN_PRODUCT_DESCRIPTION);
            int priceColumnIndex = cursor.getColumnIndex(BadmintonEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BadmintonEntry.COLUMN_PRODUCT_QUANTITY);
            int manufacturerColumnIndex = cursor.getColumnIndex(BadmintonEntry.COLUMN_PRODUCT_MANUFACTURER);
            int imageUriColumnIndex = cursor.getColumnIndex(BadmintonEntry.COLUMN_PRODUCT_IMAGE_URI);

            String name = cursor.getString(nameColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);
            Double price = cursor.getDouble(priceColumnIndex);
            Integer quantity = cursor.getInt(quantityColumnIndex);
            String manufacturer = cursor.getString(manufacturerColumnIndex);
            String imageUri = cursor.getString(imageUriColumnIndex);

            mNameText.setText(name);
            mDescriptionText.setText(description);
            mPriceText.setText(Double.toString(price));
            mQuantityText.setText(Integer.toString(quantity));
            mManufacturerText.setText(manufacturer);
            if (imageUri != null && !imageUri.isEmpty()) {
                mPictureView.setImageBitmap(getBitmapFromUri(Uri.parse(imageUri)));
            } else {
                mPictureView.setImageResource(R.drawable.ic_add_a_photo);
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
