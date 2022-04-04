package com.paguelofacil.posfacil.util.Constantes

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Coroutines base class for proving utility methods for Coroutines work scope and thread to be run upon
 *
 * @constructor Create empty Coroutines base
 */
object CoroutinesBase {

    /**
     * run a coroutine on Main thread with CoroutineScope
     *
     * @param work
     * @receiver coroutine with [CoroutineScope] and [Dispatchers.Main] thread
     */
    fun main(work: suspend (() -> Unit)) =
        CoroutineScope(Dispatchers.Main).launch {
            work()
        }



    /**
     * run a coroutine on IO thread with CoroutineScope
     *
     * @param work
     * @receiver coroutine with [CoroutineScope] and [Dispatchers.IO] thread
     */
    fun io(work: suspend (() -> Unit)) =
        CoroutineScope(Dispatchers.IO).launch {
            work()
        }

    /**
     * run a coroutine on Default thread with CoroutineScope
     *
     * @param work
     * @receiver coroutine with [CoroutineScope] and [Dispatchers.Default] thread
     */
    fun default(work: suspend (() -> Unit)) =
        CoroutineScope(Dispatchers.Default).launch {
            work()
        }

    /*fun doAsync(work1: suspend () -> GoogleBaseResponse) =
        CoroutineScope(Dispatchers.Main).async {
            work1()
        }*/

}