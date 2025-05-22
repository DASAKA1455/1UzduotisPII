package com.example.notes;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class NotesProvider extends ContentProvider {
    public static final String AUTHORITY = "com.example.notes.provider";
    // ContentProvider
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/notes");
    // Bazinis URI
    private NotesDb dbHelper;
    //SQLite DB
    @Override
    public boolean onCreate() {
        //dbHelper klasė
        dbHelper = new NotesDb(getContext());
        return true;
    }
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Įterpia naują įrašą į DB
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(NotesDb.TABLE_NAME, null, values);  //Įterpiami duomenys į lentelę
        //Grąžinamas URI su naujo įrašo ID
        return Uri.withAppendedPath(CONTENT_URI, String.valueOf(id));
    }
    @Override
    public Cursor query(Uri uri, String[] proj, String sel, String[] selArgs, String sort) {
        //Read DB
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query(NotesDb.TABLE_NAME, proj, sel, selArgs, null, null, sort);
    }
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Įrašo naujinimas DB
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.update(NotesDb.TABLE_NAME, values, selection, selectionArgs);
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Pašalina įrašus iš DB
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(NotesDb.TABLE_NAME, selection, selectionArgs);
    }
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
