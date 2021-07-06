package com.dark.picker

import java.util.concurrent.Callable

interface CustomCallable<R> : Callable<R> {
    fun setDataAfterLoading(result: R)
    fun setUiForLoading()
}