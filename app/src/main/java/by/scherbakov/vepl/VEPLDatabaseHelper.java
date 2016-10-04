package by.scherbakov.vepl;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by User on 10.04.2016.
 */
public class VEPLDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "VEPL";
    private static final int DB_VERSION = 2;

    VEPLDatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion){
        if (oldVersion < 1){
            db.execSQL("CREATE TABLE TEAMS (_id INTEGER PRIMARY KEY, "
                + "NAME TEXT, "
                + "IMAGE_RESOURCE_ID TEXT); ");
            insertDrink(db, 1, "Manchester United", "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/m/man-utd/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png");
            insertDrink(db, 3, "Arsenal", "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/a/arsenal/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png");
            insertDrink(db, 4, "Newcastle United", "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/n/newcastle/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png");
            insertDrink(db, 6, "Tottenham Hotspur", "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/s/spurs/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png");
            insertDrink(db, 7, "Aston Villa", "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/a/aston-villa/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png");
            insertDrink(db, 8, "Chelsea", "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/c/chelsea/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png");
            insertDrink(db, 11, "Everton", "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/e/everton/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png");
            insertDrink(db, 13, "Leicester City", "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/l/leicester/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png");
            insertDrink(db, 14, "Liverpool", "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/l/liverpool/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png");
            insertDrink(db, 20, "Southampton", "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/s/southampton/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png");
            insertDrink(db, 21, "West Ham United", "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/w/west-ham/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png");
            insertDrink(db, 31, "Crystal Palace", "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/c/crystal-palace/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png");
            insertDrink(db, 35, "West Bromwich Albion", "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/w/west-brom/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png");
            insertDrink(db, 43, "Manchester City", "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/m/man-city/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png");
            insertDrink(db, 45, "Norwich City", "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/n/norwich/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png");
            insertDrink(db, 56, "Sunderland", "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/s/sunderland/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png");
            insertDrink(db, 57, "Watford", "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/w/watford/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png");
            insertDrink(db, 80, "Swansea City", "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/s/swansea/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png");
            insertDrink(db, 91, "Bournemouth", "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/b/bournemouth/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png");
            insertDrink(db, 110, "Stoke City", "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/s/stoke/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png");

        }

    }

    private static void insertDrink(SQLiteDatabase db,int ID, String name, String resourceId){
        ContentValues drinkValues = new ContentValues();
        drinkValues.put("_id", ID);
        drinkValues.put("NAME", name);
        drinkValues.put("IMAGE_RESOURCE_ID", resourceId);
        db.insert("TEAMS", null, drinkValues);
    }
}
