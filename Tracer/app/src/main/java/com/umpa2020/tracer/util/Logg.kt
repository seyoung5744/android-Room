package com.umpa2020.tracer.util

import android.util.Log

/**
 * 로그 표시
 */
object Logg {
    private fun tag(): String {
        val trace = Thread.currentThread().stackTrace[4]
        val fileName = trace.fileName
        val classPath = trace.className
        val className = classPath.substring(classPath.lastIndexOf(".") + 1)
        val methodName = trace.methodName
        val lineNumber = trace.lineNumber
        return "App# $className.$methodName($fileName:$lineNumber)"
    }

    fun v(msg: String?) {
        Log.v(tag(), "" + msg)
    }

    fun d(msg: String?) {
        Log.d(tag(), "" + msg)
    }

    fun i(msg: String?) {
        Log.i(tag(), "" + msg)
    }

    fun w(msg: String?) {
        Log.w(tag(), "" + msg)
    }

    fun w(e: Throwable) {
        Log.w(tag(), "" + e.localizedMessage)
    }

    fun w(e: Exception) {
        Log.w(tag(), "" + e.localizedMessage)
    }

    fun e(msg: String?) {
        Log.e(tag(), "" + msg)
    }
}