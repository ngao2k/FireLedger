package com.example.fireledger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class BillingDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "BillingApp.db";
    private static final int DATABASE_VERSION = 3; // Updated database version

    private static final String TABLE_BILLINGS = "billings";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_UUID = "uuid";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_DESCRIPTION = "description";

    private static final String TAG = "BillingDatabaseHelper";

    public BillingDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create the billings table
        String createTable = "CREATE TABLE " + TABLE_BILLINGS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_UUID + " TEXT UNIQUE NOT NULL, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_TYPE + " TEXT NOT NULL, " +
                COLUMN_AMOUNT + " TEXT NOT NULL, " +
                COLUMN_DESCRIPTION + " TEXT)";
        db.execSQL(createTable);
        Log.d(TAG, "Database table created: " + TABLE_BILLINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            // Add UUID column if upgrading from a previous version without it
            db.execSQL("ALTER TABLE " + TABLE_BILLINGS + " ADD COLUMN " + COLUMN_UUID + " TEXT UNIQUE");
            Log.d(TAG, "Database upgraded to version 3: Added 'uuid' column");
        }
    }

    /**
     * Adds a new billing record to the database.
     *
     * @param date        The date of the billing in "YYYY-MM-DD" format.
     * @param type        The type of the billing.
     * @param amount      The amount associated with the billing.
     * @param description A description of the billing.
     */
    public void addBilling(String date, String type, String amount, String description) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            // Ensure date is in "YYYY-MM-DD" format
            String formattedDate = formatDate(date);

            // Generate unique UUID
            String uuid = java.util.UUID.randomUUID().toString();

            values.put(COLUMN_UUID, uuid);
            values.put(COLUMN_DATE, formattedDate);
            values.put(COLUMN_TYPE, type);
            values.put(COLUMN_AMOUNT, amount);
            values.put(COLUMN_DESCRIPTION, description);

            long result = db.insert(TABLE_BILLINGS, null, values);
            if (result == -1) {
                Log.e(TAG, "Failed to insert billing data");
            } else {
                Log.d(TAG, "Billing data inserted successfully, ID: " + result + ", UUID: " + uuid);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding billing record", e);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * Retrieves all billing records from the database.
     *
     * @return A list of BillingItem objects representing all billing records.
     */
    public List<BillingItem> getAllBillings() {
        List<BillingItem> billingList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_BILLINGS, null, null, null, null, null, COLUMN_ID + " DESC");

            if (cursor.moveToFirst()) {
                do {
                    BillingItem item = new BillingItem(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UUID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                    );
                    billingList.add(item);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving billing records", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return billingList;
    }

    /**
     * Deletes a specific billing record from the database based on its UUID.
     *
     * @param item The BillingItem object representing the record to be deleted.
     */
    public void deleteBilling(BillingItem item) {
        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            db.beginTransaction();

            int rowsAffected = db.delete(TABLE_BILLINGS, COLUMN_UUID + "=?", new String[]{item.getUuid()});

            if (rowsAffected > 0) {
                db.setTransactionSuccessful();
                Log.d(TAG, "Billing record deleted successfully, UUID: " + item.getUuid());
            } else {
                Log.w(TAG, "No billing record found to delete with UUID: " + item.getUuid());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error deleting billing record", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    /**
     * Formats a given date string to ensure it's in "YYYY-MM-DD" format.
     *
     * @param inputDate The date string to be formatted.
     * @return The formatted date string or the original string if formatting fails.
     */
    private String formatDate(String inputDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(inputDate);
            return inputFormat.format(date); // Ensure consistent format
        } catch (Exception e) {
            Log.e(TAG, "Error formatting date: " + inputDate, e);
            return inputDate; // Return original date if parsing fails
        }
    }

    /**
     * Retrieves a list of distinct months for which there are billing records.
     *
     * @return A list of strings representing available months in "YYYY-MM" format.
     */
    public List<String> getAvailableMonths() {
        List<String> months = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();

            // Query the billings table for distinct months
            cursor = db.rawQuery(
                    "SELECT DISTINCT strftime('%Y-%m', " + COLUMN_DATE + ") AS year_month " +
                            "FROM " + TABLE_BILLINGS + " ORDER BY year_month DESC", null);

            if (cursor.moveToFirst()) {
                do {
                    String yearMonth = cursor.getString(cursor.getColumnIndexOrThrow("year_month"));
                    months.add(yearMonth);
                } while (cursor.moveToNext());
            } else {
                Log.w(TAG, "No available months found in the database.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving available months", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return months;
    }

    /**
     * Retrieves all billing records for a specific month.
     *
     * @param month The month for which to retrieve billing records in "YYYY-MM" format.
     * @return A list of BillingItem objects representing the billing records for the specified month.
     */
    public List<BillingItem> getBillingsByMonth(String month) {
        List<BillingItem> billingList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();

            // Query billing records for the specified month
            cursor = db.rawQuery(
                    "SELECT * FROM " + TABLE_BILLINGS +
                            " WHERE strftime('%Y-%m', " + COLUMN_DATE + ") = ? " +
                            "ORDER BY " + COLUMN_DATE + " ASC",
                    new String[]{month});

            if (cursor.moveToFirst()) {
                do {
                    BillingItem item = new BillingItem(
                            cursor.getString(cursor.getColumnIndexOrThrow("uuid")),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                            cursor.getString(cursor.getColumnIndexOrThrow("type")),
                            cursor.getString(cursor.getColumnIndexOrThrow("amount")),
                            cursor.getString(cursor.getColumnIndexOrThrow("description"))
                    );
                    billingList.add(item);
                } while (cursor.moveToNext());
            } else {
                Log.w(TAG, "No billing records found for month: " + month);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving billings by month", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return billingList;
    }
}