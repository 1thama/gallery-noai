package com.tama.gallerynoai.ui.navigation

object NavRoutes {
    const val PHOTOS = "photos"
    const val SEARCH = "search"
    const val ALBUMS = "albums"
    const val SETTINGS = "settings"
    const val TRASH = "trash"
    
    fun albumDetail(albumId: String, albumName: String) = "album_detail/$albumId/$albumName"
    const val ALBUM_DETAIL = "album_detail/{albumId}/{albumName}"
    
    fun albumItemDetail(albumId: String, mediaId: Long) = "album_item_detail/$albumId/$mediaId"
    const val ALBUM_ITEM_DETAIL = "album_item_detail/{albumId}/{mediaId}"
    
    fun quickAccess(type: String) = "quick_access/$type"
    const val QUICK_ACCESS = "quick_access/{type}"
    
    fun quickAccessDetail(type: String, mediaId: Long) = "quick_access_detail/$type/$mediaId"
    const val QUICK_ACCESS_DETAIL = "quick_access_detail/{type}/{mediaId}"
    
    fun detail(mediaId: Long) = "detail/$mediaId"
    const val DETAIL = "detail/{mediaId}"
    
    fun externalDetail(uri: String, mimeType: String?) = "external_detail?uri=$uri&mimeType=$mimeType"
    const val EXTERNAL_DETAIL = "external_detail?uri={uri}&mimeType={mimeType}"
}
