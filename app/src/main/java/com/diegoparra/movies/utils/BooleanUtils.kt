package com.diegoparra.movies.utils

inline fun Boolean.runIfTrue(block: () -> Unit) {
    if (this) {
        block.invoke()
    }
}