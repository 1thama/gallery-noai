package com.tama.gallerynoai.data.model

enum class MediaType {
    IMAGES, VIDEOS, GIFS, RAWS, SVGS, PORTRAITS
}

enum class SearchSortOrder {
    FILE_NAME, DIRECTORY_PATH, FILE_SIZE, LAST_MODIFIED, DATE_TAKEN, RANDOM
}

enum class GroupBy {
    TIME_DAILY, TIME_MONTHLY, TIME_YEARLY,
    EXTENSION, MEDIA_TYPE,
    LOCATION
}

data class SearchOptions(
    val mediaTypes: Set<MediaType> = emptySet(),
    val sortOrder: SearchSortOrder = SearchSortOrder.LAST_MODIFIED,
    val groupBy: GroupBy? = null,
    val searchAllFiles: Boolean = true,
    val currentDirectory: String? = null
)

