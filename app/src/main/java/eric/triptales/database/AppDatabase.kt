package eric.triptales.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * The main database for the TripTales app.
 *
 * This database holds two entities: [PlaceEntity] and [StoryEntity], and provides DAOs
 * for interacting with their respective tables. It uses Room to handle database operations
 * and includes type converters for custom data types.
 *
 * @property placeDao Provides access to place-related database operations.
 * @property storyDao Provides access to story-related database operations.
 */
@Database(entities = [PlaceEntity::class, StoryEntity::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class) // Handles custom type conversions for Room
abstract class AppDatabase : RoomDatabase() {

    /**
     * Access object for place-related operations in the database.
     */
    abstract fun placeDao(): PlaceDao

    /**
     * Access object for story-related operations in the database.
     */
    abstract fun storyDao(): StoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Provides a singleton instance of the database.
         *
         * This method ensures that only one instance of the database is created,
         * even in a multithreaded environment. It uses Room's database builder to
         * initialize the database and applies destructive migration for schema changes.
         *
         * @param context The application context used to create the database instance.
         * @return A singleton instance of [AppDatabase].
         */
        fun getDatabase(context: Context): AppDatabase {
            // Return the existing instance if it exists; otherwise, create a new instance
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "places_database" // Database file name
                )
                    .fallbackToDestructiveMigration() // Automatically handles schema migrations by clearing data
                    .build()
                INSTANCE = instance // Save the new instance
                instance
            }
        }
    }
}
