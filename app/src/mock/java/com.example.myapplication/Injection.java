package com.example.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.myapplication.data.FakeMedicineLocalDataSource;
import com.example.myapplication.data.source.MedicineRepository;
import com.example.myapplication.data.source.local.MedicinesLocalDataSource;


public class Injection {

    public static MedicineRepository provideMedicineRepository(@NonNull Context context) {
        return MedicineRepository.getInstance(MedicinesLocalDataSource.getInstance(context));
    }
}
