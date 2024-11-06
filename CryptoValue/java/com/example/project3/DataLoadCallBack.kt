package com.example.project3

interface DataLoadCallback{
    fun onDataLoaded()
    fun onFailed(error: String)
}