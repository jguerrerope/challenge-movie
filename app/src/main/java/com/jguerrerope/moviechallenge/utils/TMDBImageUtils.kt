package com.jguerrerope.moviechallenge.utils

/**
 * Class to crate final url image from TMDB api
 */
object TMDBImageUtils {

    private const val BASE_IMAGE_URL_FORMAT = "https://image.tmdb.org/t/p/%1\$s%2\$s"

    /**
     *  TMDB provive with each Movie two image properties (backdropPath & posterPath),
     * those properties are the file and we have to crate the ulr for final image
     * the sizes: "w92", "w154", "w185", "w342", "w500", "w780"
     */

    fun formatUrlImageWithW500(file: String): String {
        return BASE_IMAGE_URL_FORMAT.format("w500", file)
    }

}