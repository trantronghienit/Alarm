package com.example.alarmpig.callback;

public interface OnImportTokenListener {
    void onImportTokenSuccess();

    void onImportTokenFailed(String message);
}
