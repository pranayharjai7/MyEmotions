package com.pranayharjai7.myemotions.Database.LocalDatabase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Expression.class}, version = 1, exportSchema = false)
public abstract class ExpressionDatabase extends RoomDatabase {
    public abstract ExpressionDAO expressionDAO();
}
