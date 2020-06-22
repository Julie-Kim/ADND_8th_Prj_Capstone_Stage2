package com.example.purchasenoti.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.purchasenoti.model.PurchaseItem;

import java.util.List;

@Dao
public interface PurchaseItemDao {

    @Query("SELECT * FROM purchase_item ORDER BY _id")
    LiveData<List<PurchaseItem>> loadAllPurchaseItems();

    @Insert
    void insertPurchaseItem(PurchaseItem purchaseItem);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updatePurchaseItem(PurchaseItem purchaseItem);

    @Delete
    void deletePurchaseItem(PurchaseItem purchaseItem);

    @Query("DELETE FROM purchase_item WHERE _id = :id")
    void deleteById(int id);

    @Query("SELECT * FROM purchase_item WHERE _id = :id")
    PurchaseItem loadPurchaseItemById(int id);

    @Query("UPDATE purchase_item SET item_name = :name, purchase_term_year=:year, " +
            "purchase_term_month=:month, purchase_term_day=:day, last_purchased_date=:date WHERE _id = :id")
    void updatePurchaseItem(int id, String name, int year, int month, int day, String date);
}
