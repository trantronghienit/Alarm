package com.example.alarmpig.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.example.alarmpig.util.Constants.FRI;
import static com.example.alarmpig.util.Constants.MON;
import static com.example.alarmpig.util.Constants.SAT;
import static com.example.alarmpig.util.Constants.SUN;
import static com.example.alarmpig.util.Constants.THURS;
import static com.example.alarmpig.util.Constants.TUES;
import static com.example.alarmpig.util.Constants.WED;

@Retention(RetentionPolicy.SOURCE)
@IntDef({MON, TUES, WED, THURS, FRI, SAT, SUN})
public @interface Days {
}
