package com.jguerrerope.moviechanllenge

object Configuration {
    const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"

    /**
     * the default item per page that api provide
     * @see <a href="https://www.themoviedb.org/talk/587bea71c3a36846c300ff73">Number of results per page</a>
     */
    const val NUMBER_OF_ITEMS_PER_PAGE = 20
    const val PREFETCH_DISTANCE = 4
}