package com.yanyushkin.roadsignrecognition.extensions

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Observable<T>.sendRequestInBackground() = subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())