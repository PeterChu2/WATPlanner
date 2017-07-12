package com.example.peterchu.watplanner.Database;

/**
 * Created by Timothy Tong on 6/11/17.
 */

public interface DBHandlerCallback {
    void onFinishTransaction(DatabaseHandler dbHandler);
    void onTransactionFailed(Exception e);
}
