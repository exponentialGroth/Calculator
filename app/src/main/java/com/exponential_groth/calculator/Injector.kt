package com.exponential_groth.calculator

import android.content.Context
import com.exponential_groth.calculator.data.Repository
import com.exponential_groth.calculator.data.StorageDB

object Injector {
    fun provideMainViewModelFactory(context: Context): MainViewModel.MainViewModelFactory {
        return MainViewModel.MainViewModelFactory(getRepo(context))
    }

    private fun getRepo(context: Context): Repository {
        val db = StorageDB.getInstance(context)
        return Repository.getInstance(db.getVariablesDao())
    }

}