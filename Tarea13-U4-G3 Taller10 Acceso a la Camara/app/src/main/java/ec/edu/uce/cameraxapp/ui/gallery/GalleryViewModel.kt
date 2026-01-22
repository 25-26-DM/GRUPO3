package ec.edu.uce.cameraxapp.ui.gallery

import android.app.Application
import android.content.ContentUris
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    private val _media = MutableStateFlow<List<MediaItem>>(emptyList())
    val media = _media.asStateFlow()

    fun loadMedia() {
        viewModelScope.launch {
            val mediaList = mutableListOf<MediaItem>()
            val contentResolver = getApplication<Application>().contentResolver

            // Cargar imágenes
            val imageProjection = arrayOf(MediaStore.Images.Media._ID)
            val imageSelection = "${MediaStore.Images.Media.DATA} like ?"
            val imageSelectionArgs = arrayOf("%CameraXApp%")
            val imageSortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                imageProjection,
                imageSelection,
                imageSelectionArgs,
                imageSortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    mediaList.add(MediaItem(contentUri, isVideo = false))
                }
            }

            // Cargar videos
            val videoProjection = arrayOf(MediaStore.Video.Media._ID)
            val videoSelection = "${MediaStore.Video.Media.DATA} like ?"
            val videoSelectionArgs = arrayOf("%CameraX-Video%")
            val videoSortOrder = "${MediaStore.Video.Media.DATE_TAKEN} DESC"

            contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                videoProjection,
                videoSelection,
                videoSelectionArgs,
                videoSortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                    mediaList.add(MediaItem(contentUri, isVideo = true))
                }
            }

            _media.value = mediaList.sortedByDescending { it.uri.toString() }
        }
    }
}
