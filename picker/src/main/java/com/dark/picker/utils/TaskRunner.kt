package com.dark.picker.utils

import android.os.Handler
import android.os.Looper
import com.dark.picker.CustomCallable
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class TaskRunner {
    private val handler: Handler = Handler(Looper.getMainLooper())
    private val executor: Executor = Executors.newCachedThreadPool()
    fun <R> executeAsync(callable: CustomCallable<R?>) {
        try {
            callable.setUiForLoading()
            executor.execute(RunnableTask<R?>(handler, callable))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class RunnableTask<R>(
        private val handler: Handler,
        private val callable: CustomCallable<R?>
    ) : Runnable {
        override fun run() {
            try {
                val result: R? = callable.call()
                handler.post(RunnableTaskForHandler<R?>(callable, result))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    class RunnableTaskForHandler<R>(
        private val callable: CustomCallable<R?>,
        private val result: R?
    ) : Runnable {
        override fun run() {
            callable.setDataAfterLoading(result)
        }

    }
}