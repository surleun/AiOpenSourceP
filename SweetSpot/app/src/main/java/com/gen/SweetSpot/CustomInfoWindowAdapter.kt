package com.gen.SweetSpot

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

private const val MARKER_TAG_TEMP = "temp_pin"
private const val MARKER_TAG_PERMANENT = "permanent_pin"


class CustomInfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {

    private val infoWindowView: View = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null)

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        render(marker, infoWindowView)
        return infoWindowView
    }

    private fun render(marker: Marker, view: View) {
        val titleTextView = view.findViewById<TextView>(R.id.info_window_title)
        val snippetTextView = view.findViewById<TextView>(R.id.info_window_snippet)
        val imageView = view.findViewById<ImageView>(R.id.info_window_image)

        imageView.setImageURI(null) // 기존 이미지 제거
        imageView.visibility = View.GONE // 기본적으로 숨김

        when (marker.tag) {
            MARKER_TAG_TEMP -> {
                titleTextView.text = marker.title ?: "새 핀 추가"
                snippetTextView.text = "여기를 눌러 핀 추가"
                snippetTextView.visibility = View.VISIBLE
            }
            MARKER_TAG_PERMANENT -> {
                titleTextView.text = marker.title ?: "영구 핀"

                // 스니펫에서 이미지 URI 분리
                val rawSnippet = marker.snippet ?: ""
                val parts = rawSnippet.split("|")
                val memo = parts.getOrNull(0) ?: ""
                val imageUriString = parts.getOrNull(1)

                if (memo.isNotEmpty()) {
                    snippetTextView.text = memo
                    snippetTextView.visibility = View.VISIBLE
                } else {
                    snippetTextView.visibility = View.GONE
                }

                if (imageUriString != null && imageUriString.isNotEmpty()) {
                    try {
                        val imageUri = Uri.parse(imageUriString)
                        imageView.setImageURI(imageUri)
                        imageView.visibility = View.VISIBLE
                    } catch (e: Exception) {
                        e.printStackTrace()
                        imageView.visibility = View.GONE
                    }
                } else {
                    imageView.visibility = View.GONE
                }
            }
            else -> {
                titleTextView.text = marker.title ?: ""
                snippetTextView.text = marker.snippet ?: ""
                snippetTextView.visibility = View.VISIBLE
            }
        }
    }
}