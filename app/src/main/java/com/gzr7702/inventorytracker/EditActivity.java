package com.gzr7702.inventorytracker;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gzr7702.inventorytracker.data.InventoryContract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import static com.gzr7702.inventorytracker.data.InventoryProvider.LOG_TAG;

/*
    This activity is used to add a new inventory item
 */

public class EditActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "EditActivity";
    private static final int EXISTING_INVENTORY_LOADER = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    String mCurrentPhotoPath;
    private Uri mCurrentItemUri;

    private EditText mCompanyEditText;
    private EditText mPhoneEditText;
    private EditText mItemEditText;
    private EditText mQuantityEditText;
    private EditText mPriceEditText;
    private ImageView mPictureView;

    String mCompanyName = "";
    String mPhoneNumber = "";
    String mItem = "";
    int mQuantity = 0;
    double mPrice = 0;
    String mPhotoPath = "";
    Uri mPhotoUri;

    // Boolean flag that keeps track of whether the item has been edited
    private boolean mItemHasChanged = false;

     // OnTouchListener that listens for any user touches on a View, implying that they are modifying
     // the view,
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_toolbar);
        setSupportActionBar(toolbar);

        // check if this is a new item, or an existing one
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        // Find all relevant views that we will need to read user input from
        mCompanyEditText = (EditText) findViewById(R.id.company_edit_text);
        mPhoneEditText = (EditText) findViewById(R.id.phone_edit_text);
        mItemEditText = (EditText) findViewById(R.id.name_edit_text);
        mQuantityEditText = (EditText) findViewById(R.id.quantity_edit_text);
        mPriceEditText = (EditText) findViewById(R.id.price_edit_text);
        mPictureView = (ImageView) findViewById(R.id.photo_view);
        Button incrementButton = (Button) findViewById(R.id.increment_button);
        Button decrementButton = (Button) findViewById(R.id.decrement_button);
        Button addPhotoButton = (Button) findViewById(R.id.photo_button);
        Button orderButton = (Button) findViewById(R.id.order_button);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mCompanyEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);
        mItemEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);

        // increment and decrement button callbacks
        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                mQuantity++;
                mQuantityEditText.setText(Integer.toString(mQuantity));
            }
        });

        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                mQuantity--;
                mQuantityEditText.setText(Integer.toString(mQuantity));
            }
        });

        // Photo button callback
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                // Check for camera, show toast if there is none, launch intent if there is
                PackageManager pm = getBaseContext().getPackageManager();
                if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    String message = "Sorry, you need a camaera on your device to add a photo.";
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                } else {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                            Toast.makeText(getBaseContext(),
                                    "Something is awry, you cannot take a photo", Toast.LENGTH_LONG).show();
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            mPhotoUri = FileProvider.getUriForFile(getBaseContext(),
                                    "com.example.android.fileprovider",
                                    photoFile);
                            mPhotoPath = photoFile.getPath();
                            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                        }
                    }

                }
            }
        });

        // Order more button that takes us to the phone w/proper phone number
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mPhoneNumber));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        // Here's where we determine if this is a new item, or an edited item
        // If the intent DOES NOT contain an item content URI, then we know that we are
        // creating a new one.
        if (mCurrentItemUri == null) {
            Log.v(TAG, "New Item");
            setTitle(getString(R.string.add_new_item_header));

            // "Delete" menu option can be hidden.
            invalidateOptionsMenu();
        } else {
            // Edit current items, show proper title and order button
            Log.v(TAG, "Edit Item");
            setTitle(getString(R.string.edit_new_item_header));
            orderButton.setVisibility(View.VISIBLE);
            // start loader
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }
    }

    /*
        Create the file that is an image
     */
    private File createImageFile() throws IOException {
        // Create an image file name using date stamp to be unique
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mPhotoPath = image.getAbsolutePath();
        mPhotoUri = Uri.fromFile(new File(mPhotoPath));

        return image;
    }

    /*
       Get the results of taking a photo
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "in onActivityResult");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null) {
                mPhotoUri = data.getData();
                mPictureView.setImageBitmap(getBitmapFromUri(mPhotoUri));
            }
        }
    }

    public Bitmap getBitmapFromUri(Uri uri) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetWidth = mPictureView.getWidth();
        int targetHeight = mPictureView.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bitmapOptions);
            input.close();

            int photoWidth = bitmapOptions.outWidth;
            int photoHeight = bitmapOptions.outHeight;

            // Determine how much to scale down the image
            //TODO: division by zero
            int scaleFactor = Math.min(photoWidth/targetWidth, photoHeight/targetHeight);

            // Decode the image file into a Bitmap sized to fill the View
            bitmapOptions.inJustDecodeBounds = false;
            bitmapOptions.inSampleSize = scaleFactor;
            bitmapOptions.inPurgeable = true;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "Photo not found.", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }

    /**
     * Validate that data in the form is good
     * @return "valid" if data is valid, or invalid field if not
     */
    private String checkData() {

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        mCompanyName = mCompanyEditText.getText().toString().trim();
        mPhoneNumber = mPhoneEditText.getText().toString().trim();
        mItem = mItemEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String originalPriceString = mPriceEditText.getText().toString().trim();
        StringBuilder sb = new StringBuilder(originalPriceString);
        String priceString = originalPriceString;
        // Check if dollar sign is in the price field
        if (sb.charAt(0) == '$') {
            priceString = sb.deleteCharAt(0).toString();
        }

        if (!Pattern.matches("\\d{3}-\\d{4}", mPhoneNumber)) {
            return "Phone Number";
        } else if (!Pattern.matches("\\d+(?:\\.\\d+)?", quantityString)) {
            return "Quantity";
        } else if(Integer.parseInt(quantityString) < 0) {
            // check for negative
            return "Quantity";
        } else if (!Pattern.matches("[0-9]+([,.][0-9]{1,2})?", priceString)) {
            return "Price";
        } else if(Float.parseFloat(priceString) < 0) {
            // check for negative
            return "Price";
        } else if (mPhotoPath.isEmpty()) {
            return "Photo";
        }
        mPrice = Double.parseDouble(priceString);

        return "valid";

    }

    /**
     * Get user input from editor and save into database.
     */
    private void saveItem() {

        // Check if this is supposed to be a new item
        // and check if all the fields in the editor are blank
        String companyNameString = mCompanyEditText.getText().toString().trim();
        String phoneNumberString = mPhoneEditText.getText().toString().trim();
        String itemString = mItemEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();

        if (mCurrentItemUri == null &&
            TextUtils.isEmpty(companyNameString) && TextUtils.isEmpty(phoneNumberString) &&
            TextUtils.isEmpty(itemString) && TextUtils.isEmpty(quantityString) &&
            TextUtils.isEmpty(priceString) && mPhotoPath.isEmpty()) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_COMPANY_NAME, mCompanyName);
        values.put(InventoryContract.InventoryEntry.COLUMN_PHONE_NUMBER, mPhoneNumber);
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME, mItem);
        values.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, mQuantity);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRICE, mPrice);
        values.put(InventoryContract.InventoryEntry.COLUMN_PHOTO, mPhotoPath);


        // check if this is a new or existing item to update
        if (mCurrentItemUri == null) {
            // This is a new item
            Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an existing item, so update the item with content URI: mCurrentItemUri
            // and pass in the new ContentValues.
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.update_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new item , hide the "Delete" menu item.
        if (mCurrentItemUri== null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                String dataField = checkData();
                if (dataField != "valid") {
                    DialogInterface.OnClickListener invalidDataClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // User clicked "Ok" button, go back to our activity.
                                }
                            };

                    // Show a dialog that notifies the user that they entered bad data
                    showInvalidDataDialog(dataField, invalidDataClickListener);
                    return true;
                } else {
                    saveItem();
                    finish();
                    return true;
                }
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }

                // If there are unsaved changes, setup a dialog to warn the user.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        // If there are unsaved changes, setup a dialog to warn the user.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_COMPANY_NAME,
                InventoryContract.InventoryEntry.COLUMN_PHONE_NUMBER,
                InventoryContract.InventoryEntry.COLUMN_ITEM_NAME,
                InventoryContract.InventoryEntry.COLUMN_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_PRICE,
                InventoryContract.InventoryEntry.COLUMN_PHOTO};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentItemUri,         // Query the content URI for the current item
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of item attributes that we're interested in
            int companyColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_COMPANY_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PHONE_NUMBER);
            int itemColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRICE);
            int photoColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PHOTO);

            // Extract out the value from the Cursor for the given column index
            String companyName = cursor.getString(companyColumnIndex);
            mPhoneNumber = cursor.getString(phoneColumnIndex);
            String itemName = cursor.getString(itemColumnIndex);
            mQuantity = cursor.getInt(quantityColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);
            mPhotoPath = cursor.getString(photoColumnIndex);

            // Update the views on the screen with the values from the database
            mCompanyEditText.setText(companyName);
            mPhoneEditText.setText(mPhoneNumber);
            mItemEditText.setText(itemName);
            mQuantityEditText.setText(Integer.toString(mQuantity));
            mPriceEditText.setText("$" + Double.toString(price));
            mPhotoUri = Uri.fromFile(new File(mPhotoPath));

            ViewTreeObserver viewTreeObserver = mPictureView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mPictureView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mPictureView.setImageBitmap(getBitmapFromUri(mPhotoUri));
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mCompanyEditText.setText("");
        mPhoneEditText.setText("");
        mItemEditText.setText("");
        mQuantityEditText.setText("");
        mPriceEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_message);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showInvalidDataDialog(String field, DialogInterface.OnClickListener invalidDataClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String message = "The " + field + " field is invalid";
        builder.setMessage(message);
        builder.setNeutralButton(R.string.dialog_button_string, invalidDataClickListener);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
                Toast.makeText(this, getString(R.string.delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

}
