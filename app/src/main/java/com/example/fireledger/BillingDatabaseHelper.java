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
    private static final int DATABASE_VERSION = 2; // Updated database version

    private static final String TABLE_BILLINGS = "billings";
    private static final String COLUMN_ID = "id";
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
        String createTable = "CREATE TABLE " + TABLE_BILLINGS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_TYPE + " TEXT NOT NULL, " +
                COLUMN_AMOUNT + " TEXT NOT NULL, " +
                COLUMN_DESCRIPTION + " TEXT)";
        db.execSQL(createTable);
        Log.d(TAG, "Database table created: " + TABLE_BILLINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add description column if it doesn't exist
            db.execSQL("ALTER TABLE " + TABLE_BILLINGS + " ADD COLUMN " + COLUMN_DESCRIPTION + " TEXT");
            Log.d(TAG, "Database upgraded to version 2: Added 'description' column");
        }
    }

    public void addBilling(String date, String type, String amount, String description) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            // Ensure date is in "YYYY-MM-DD" format
            String formattedDate = formatDate(date);

            values.put(COLUMN_DATE, formattedDate);
            values.put(COLUMN_TYPE, type);
            values.put(COLUMN_AMOUNT, amount);
            values.put(COLUMN_DESCRIPTION, description);

            long result = db.insert(TABLE_BILLINGS, null, values);
            if (result == -1) {
                Log.e(TAG, "Failed to insert billing data");
            } else {
                Log.d(TAG, "Billing data inserted successfully, ID: " + result);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding billing record", e);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

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

    public List<BillingItem> getBillingsByMonth(String month) {
        List<BillingItem> billingList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();

            // Query records matching the given month in "YYYY-MM" format
            cursor = db.rawQuery(
                    "SELECT * FROM " + TABLE_BILLINGS + " WHERE strftime('%Y-%m', " + COLUMN_DATE + ") = ? ORDER BY " + COLUMN_DATE + " ASC",
                    new String[]{month});

            if (cursor.moveToFirst()) {
                do {
                    BillingItem item = new BillingItem(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
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

    public void deleteBilling(BillingItem item) {
        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            int rowsAffected = db.delete(TABLE_BILLINGS,
                    COLUMN_DATE + "=? AND " + COLUMN_TYPE + "=? AND " + COLUMN_AMOUNT + "=? AND " + COLUMN_DESCRIPTION + "=?",
                    new String[]{item.getDate(), item.getType(), item.getAmount(), item.getDescription()});

            if (rowsAffected > 0) {
                Log.d(TAG, "Billing record deleted successfully");
            } else {
                Log.w(TAG, "No billing record found to delete");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error deleting billing record", e);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public List<String> getAvailableMonths() {
        List<String> months = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();

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

    // Helper method to format date to "YYYY-MM-DD"
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
}
