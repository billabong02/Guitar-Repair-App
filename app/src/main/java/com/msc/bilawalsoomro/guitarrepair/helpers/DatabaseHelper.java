package com.msc.bilawalsoomro.guitarrepair.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
 * External code was taken and modified in order to implement a pre-loaded SQLite database self-contained within the guitar repair app.  The code was taken from an online tutorial posted as blog post by Juan-Manel Fluxa that can be found at:
 * URL: http://blog.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/
 * The tutorial shows how to create and use a pre-loaded database in android applications in contrast to using a database that is populated during runtime (Fluxa, 2009).
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.msc.bilawalsoomro.guitarrepair/databases/";

    private static String DB_NAME = "guitarapp.db";

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DatabaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDatabase() throws IOException {

        boolean dbExist = checkDatabase();

        if(dbExist){
            //do nothing - database already exist
        }else{

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDatabase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDatabase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDatabase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDatabase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor listArticlesCursor(String id) {
        myDataBase = this.getReadableDatabase();
        return myDataBase.rawQuery("SELECT * FROM article_components LEFT JOIN articles ON article_components.article_id = articles._id WHERE article_components.component_id = " + Integer.parseInt(id), null);
    }

    public Cursor listComponentsByTypeCursor(String type) {
        myDataBase = this.getReadableDatabase();
        return myDataBase.query("components", null, "guitar_type=?", new String[]{type}, null, null, null);
    }

    public Cursor listComponentsCursor() {
        myDataBase = this.getReadableDatabase();
        return myDataBase.query("components", null, null, null, null, null, null);
    }

    public Cursor viewArticleCursor(String id) {
        myDataBase = this.getReadableDatabase();
        return myDataBase.query("articles", null, "_id=?", new String[]{id}, null, null, null);
    }

    public void addBookmark(String id) {
        ContentValues insertValues = new ContentValues();
        insertValues.put("article_id", id);
        myDataBase.insert("bookmarks", null, insertValues);
    }

    public boolean removeBookmark(String id) {
        return myDataBase.delete("bookmarks", "article_id" + "=" + id, null) > 0;
    }

    public Cursor checkBookmark(String id) {
        return myDataBase.rawQuery("select * from bookmarks where article_id = " + Integer.parseInt(id), null);
    }

    public Cursor getBookmarks() {
        myDataBase = this.getReadableDatabase();
        return myDataBase.rawQuery("Select articles._id, articles.title from articles join bookmarks where bookmarks.article_id = articles._id", null);
    }

    public Cursor getComponent(String id) {
        myDataBase = this.getReadableDatabase();
        return myDataBase.rawQuery("Select * from components where _id=" + id, null);
    }

    public Cursor getImagesForArticle(String id) {
        myDataBase = this.getReadableDatabase();
        return myDataBase.query("article_image_list", null, "article_id=?", new String[]{id}, null, null, null);
    }
}