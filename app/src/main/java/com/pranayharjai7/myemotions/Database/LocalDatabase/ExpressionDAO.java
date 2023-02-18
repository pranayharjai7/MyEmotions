package com.pranayharjai7.myemotions.Database.LocalDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ExpressionDAO {

    @Insert
    void insertNewExpression(Expression expression);

    @Query("SELECT * FROM Expression")
    LiveData<List<Expression>> getAllExpression();

    @Query("DELETE FROM Expression")
    void clearData();
}
