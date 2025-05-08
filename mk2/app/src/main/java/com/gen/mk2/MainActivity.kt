package com.gen.mk2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import android.widget.Toast
import androidx.appcompat.app.AlertDialog


private const val MARKER_TAG_TEMP = "temp_pin"
private const val MARKER_TAG_PERMANENT = "permanent_pin"


class MainActivity : AppCompatActivity(),
    OnMapReadyCallback,
    OnMapLongClickListener,
    OnInfoWindowClickListener,
    OnMapClickListener
{

    private lateinit var map: GoogleMap
    private var tempMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.setOnMapLongClickListener(this)
        map.setInfoWindowAdapter(CustomInfoWindowAdapter(this))
        map.setOnInfoWindowClickListener(this)
        map.setOnMapClickListener(this)

        val cheongju = LatLng(36.6359, 127.4913)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(cheongju, 13f))
    }

    override fun onMapClick(latLng: LatLng) {
        tempMarker?.remove()
        tempMarker = null
    }

    override fun onMapLongClick(latLng: LatLng) {
        tempMarker?.remove()

        val markerOptions = MarkerOptions()
            .position(latLng)
            .title("새 핀 추가")
            .snippet("")

        tempMarker = map.addMarker(markerOptions)
        tempMarker?.tag = MARKER_TAG_TEMP


        tempMarker?.showInfoWindow()
    }

    override fun onInfoWindowClick(marker: Marker) {
        when (marker.tag) {
            MARKER_TAG_TEMP -> {
                showAddPermanentPinDialog(marker)
            }
            MARKER_TAG_PERMANENT -> {
                showPinOptionsDialog(marker)
            }
        }
    }

    private fun showPinOptionsDialog(marker: Marker) {
        val options = arrayOf("핀 수정", "핀 제거", "핀 게시하기")

        AlertDialog.Builder(this)
            .setTitle("핀 옵션")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> { // 핀 수정
                        dialog.dismiss()
                        showEditPermanentPinDialog(marker)
                    }
                    1 -> { // 핀 제거
                        dialog.dismiss()
                        showDeleteConfirmationDialog(marker)
                    }
                    2 -> { // 핀 게시하기
                        // 핀 게시하기 기능 넣어야 함
                        Toast.makeText(this, "핀 게시하기 (구현 예정)", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            }
            .show()
    }

    private fun showDeleteConfirmationDialog(marker: Marker) {
        AlertDialog.Builder(this)
            .setTitle("핀 제거 확인")
            .setMessage("정말 삭제하시겠습니까?")
            .setPositiveButton("예") { dialog, which ->
                marker.remove()
                Toast.makeText(this, "핀이 제거되었습니다.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("아니오") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showAddPermanentPinDialog(tempMarker: Marker) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_pin, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextPinName)
        val editTextMemo = dialogView.findViewById<EditText>(R.id.editTextPinMemo)

        AlertDialog.Builder(this)
            .setTitle("새 핀 정보 입력")
            .setView(dialogView)
            .setPositiveButton("확인") { dialog, which ->
                val pinName = editTextName.text.toString().trim()
                val pinMemo = editTextMemo.text.toString().trim()

                val permanentMarkerOptions = MarkerOptions()
                    .position(tempMarker.position)
                    .title(if (pinName.isNotEmpty()) pinName else "이름 없음")
                    .snippet(pinMemo)

                val permanentMarker = map.addMarker(permanentMarkerOptions)
                permanentMarker?.tag = MARKER_TAG_PERMANENT

                tempMarker.remove()
                this.tempMarker = null

                Toast.makeText(this, "핀이 추가되었습니다.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showEditPermanentPinDialog(marker: Marker) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_pin, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextPinName)
        val editTextMemo = dialogView.findViewById<EditText>(R.id.editTextPinMemo)

        editTextName.setText(marker.title)
        editTextMemo.setText(marker.snippet)

        AlertDialog.Builder(this)
            .setTitle("핀 정보 수정")
            .setView(dialogView)
            .setPositiveButton("확인") { dialog, which ->
                val newPinName = editTextName.text.toString().trim()
                val newPinMemo = editTextMemo.text.toString().trim()

                marker.title = if (newPinName.isNotEmpty()) newPinName else "이름 없음"
                marker.snippet = newPinMemo

                marker.hideInfoWindow()
                marker.showInfoWindow()

                Toast.makeText(this, "핀이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }
}