package com.pranayharjai7.myemotions.Database.LocalDatabase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Expression")
public class Expression {

    @PrimaryKey(autoGenerate = true)
    private int key;
    private String dateTime;
    private String Expression;

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getExpression() {
        return Expression;
    }

    public void setExpression(String expression) {
        Expression = expression;
    }
}
