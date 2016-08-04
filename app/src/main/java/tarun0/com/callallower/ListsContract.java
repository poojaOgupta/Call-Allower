package tarun0.com.callallower;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Tarun on 04/08/2016.
 */
public class ListsContract {

    public static final String CONTENT_AUTHORITY = "tarun0.com.callallower";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Tables
    public static final String PATH_BLACKLIST = "block";

    public static final class BlackListEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BLACKLIST).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BLACKLIST;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BLACKLIST;

        //Table Columns
        public static final String TABLE_NAME = "blacklist";
        public static final String COLUMN_NAME = "contactName";
        public static final String COLUMN_NUMBER = "contactNumber";

        //Function to build Uri to find contact by its number
        public static Uri buildBlockedNumberUri (long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
