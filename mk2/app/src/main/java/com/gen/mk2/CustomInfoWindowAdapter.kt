package com.gen.mk2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
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

        when (marker.tag) {
            MARKER_TAG_TEMP -> {
                titleTextView.text = marker.title ?: "새 핀 추가"
                snippetTextView.text = "여기를 눌러 핀 추가"
            }
            MARKER_TAG_PERMANENT -> {
                titleTextView.text = marker.title ?: "영구 핀"
                snippetTextView.text = marker.snippet ?: "메모 없음"
            }
            else -> {
                titleTextView.text = marker.title ?: ""
                snippetTextView.text = marker.snippet ?: ""
            }
        }
    }
}