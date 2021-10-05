package com.diegoparra.movies.utils

import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.load
import com.diegoparra.movies.R

fun ImageView.loadImage(
    uri: String?,
    @DrawableRes placeholder: Int = R.drawable.loading_animation,
    @DrawableRes error: Int = R.drawable.ic_broken_image,
) {
    this.load(uri, builder = {
        placeholder(placeholder)
        error(error)
        crossfade(true)
    })
}