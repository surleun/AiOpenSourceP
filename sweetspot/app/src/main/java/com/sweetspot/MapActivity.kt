package com.sweetspot

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sweetspot.api.pin.DTO.PinDTO

private const val MARKER_TAG_TEMP = "temp_pin"
private const val LOCATION_PERMISSION_REQUEST_CODE = 1001

// PinDTO와 함께 이미지 Uri를 관리하기 위한 데이터 클래스 추가
data class MapPinData(
    val pinDto: PinDTO,
    var imageUri: Uri? = null
)

class MapActivity : AppCompatActivity(),
    OnMapReadyCallback,
    GoogleMap.OnMapLongClickListener,
    GoogleMap.OnInfoWindowClickListener,
    GoogleMap.OnMapClickListener {

    private lateinit var map: GoogleMap
    private var tempMarker: Marker? = null
    private var currentSelectedImageUri: Uri? = null

    private lateinit var pinImageView: ImageView
    // mapPinsList의 타입을 MapPinData로 변경
    private val mapPinsList = mutableListOf<MapPinData>()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var fabMyLocation: FloatingActionButton

    private val pickImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    currentSelectedImageUri = uri
                    pinImageView.setImageURI(uri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        fabMyLocation = findViewById(R.id.fab_my_location)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupBottomNavigation()
        setupOnBackPressedCallback()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        fabMyLocation.setOnClickListener {
            moveToCurrentLocation()
        }
    }

    private fun setupBottomNavigation() {
        val navMap = findViewById<LinearLayout>(R.id.nav_map)
        val navBoard = findViewById<LinearLayout>(R.id.nav_board)
        val navProfile = findViewById<LinearLayout>(R.id.nav_profile)

        navBoard.setBackgroundResource(R.drawable.nav_inactive_background)
        navProfile.setBackgroundResource(R.drawable.nav_inactive_background)

        navMap.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
        navBoard.setOnClickListener {
            val intent = Intent(this, BoardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
        navProfile.setOnClickListener {
            val intent = Intent(this, MypageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
    }

    private fun setupOnBackPressedCallback() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@MapActivity, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        val navMap = findViewById<LinearLayout>(R.id.nav_map)
        val navBoard = findViewById<LinearLayout>(R.id.nav_board)
        val navProfile = findViewById<LinearLayout>(R.id.nav_profile)

        navMap.setBackgroundResource(R.drawable.nav_inactive_background)
        navBoard.setBackgroundResource(R.drawable.nav_inactive_background)
        navProfile.setBackgroundResource(R.drawable.nav_inactive_background)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isMyLocationButtonEnabled = false

        map.setOnMapLongClickListener(this)
        map.setInfoWindowAdapter(CustomInfoWindowAdapter(this))
        map.setOnInfoWindowClickListener(this)
        map.setOnMapClickListener(this)

        val chungbukNationalUniv = LatLng(36.6290, 127.4563)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(chungbukNationalUniv, 15f))

        checkLocationPermissionAndEnableMyLocationDot()
    }

    private fun checkLocationPermissionAndEnableMyLocationDot() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun moveToCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        if (!::map.isInitialized) return
        map.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            } else {
                Toast.makeText(this, "현재 위치를 가져올 수 없습니다. 위치 서비스가 켜져 있는지 확인하세요.", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "위치 정보 접근에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (::map.isInitialized) {
                    checkLocationPermissionAndEnableMyLocationDot()
                    moveToCurrentLocation()
                }
            } else {
                Toast.makeText(this, "위치 권한이 거부되었습니다. 내 위치 기능을 사용할 수 없습니다.", Toast.LENGTH_LONG).show()
            }
        }
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
            .snippet("정보를 입력하려면 여기를 클릭하세요.")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        tempMarker = map.addMarker(markerOptions)
        tempMarker?.tag = MARKER_TAG_TEMP
        tempMarker?.showInfoWindow()
    }

    override fun onInfoWindowClick(marker: Marker) {
        // marker.tag가 PinDTO가 아닌 MapPinData 인스턴스인지 확인
        when (marker.tag) {
            is MapPinData -> showPinOptionsDialog(marker) // MapPinData로 변경
            MARKER_TAG_TEMP -> showAddPermanentPinDialog(marker)
        }
    }

    private fun showPinOptionsDialog(marker: Marker) {
        val options = arrayOf("핀 수정", "핀 제거", "핀 게시하기")
        AlertDialog.Builder(this)
            .setTitle("핀 옵션")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> showEditPermanentPinDialog(marker)
                    1 -> showDeleteConfirmationDialog(marker)
                    2 -> {
                    }
                }
                dialog.dismiss()
            }
            .show()
    }

    private fun showDeleteConfirmationDialog(marker: Marker) {
        // marker.tag가 MapPinData 인스턴스인지 확인
        val mapPin = marker.tag as? MapPinData ?: return
        AlertDialog.Builder(this)
            .setTitle("핀 제거 확인")
            .setMessage("이 핀을 정말 삭제하시겠습니까?")
            .setPositiveButton("예") { dialog, _ ->
                // pinDto 내부의 pinId에 접근하여 비교
                val pinIndex = mapPinsList.indexOfFirst { it.pinDto.pinId == mapPin.pinDto.pinId }
                if (pinIndex != -1) {
                    mapPinsList.removeAt(pinIndex)
                }
                marker.remove()
                Toast.makeText(this, "핀이 제거되었습니다.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("아니오") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showAddPermanentPinDialog(tempMarker: Marker) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_pin, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextPinName)
        val editTextMemo = dialogView.findViewById<EditText>(R.id.editTextPinMemo)
        pinImageView = dialogView.findViewById<ImageView>(R.id.imageViewPin)
        currentSelectedImageUri = null
        pinImageView.setImageResource(0)

        pinImageView.setOnClickListener { openImagePicker() }

        AlertDialog.Builder(this)
            .setTitle("새 핀 정보 입력")
            .setView(dialogView)
            .setPositiveButton("확인") { dialog, _ ->
                val pinName = editTextName.text.toString().trim()
                val pinMemo = editTextMemo.text.toString().trim()

                if (pinName.isEmpty()) {
                    Toast.makeText(this, "핀 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val customMarkerIcon = createCustomMarkerIcon(pinName, currentSelectedImageUri)
                val permanentMarkerOptions = MarkerOptions()
                    .position(tempMarker.position)
                    .title(pinName)
                    .snippet(pinMemo)
                    .icon(customMarkerIcon)
                    .anchor(0.5f, 1.0f)

                val permanentMarker = map.addMarker(permanentMarkerOptions)

                // PinDTO 객체 생성
                val newPinDto = PinDTO(
                    pinId = -System.currentTimeMillis(), // 임시 ID, 실제 서버 연동 시에는 서버에서 부여받은 ID 사용
                    userId = 1L, // 실제 사용자 ID로 변경
                    categoryId = 1L, // 실제 카테고리 ID로 변경
                    latitude = permanentMarker!!.position.latitude,
                    longitude = permanentMarker.position.longitude,
                    title = pinName,
                    description = pinMemo
                )
                // MapPinData 객체 생성 후 마커 태그 및 리스트에 추가
                val newMapPinData = MapPinData(pinDto = newPinDto, imageUri = currentSelectedImageUri)

                permanentMarker.tag = newMapPinData // MapPinData 객체를 태그로 설정
                mapPinsList.add(newMapPinData) // MapPinData 객체를 리스트에 추가

                this.tempMarker?.remove()
                this.tempMarker = null
                currentSelectedImageUri = null

                Toast.makeText(this, "핀이 추가되었습니다.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                currentSelectedImageUri = null
                dialog.dismiss()
            }
            .show()
    }

    private fun showEditPermanentPinDialog(marker: Marker) {
        // marker.tag가 MapPinData 인스턴스인지 확인
        val mapPinToEdit = marker.tag as? MapPinData ?: return
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_pin, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextPinName)
        val editTextMemo = dialogView.findViewById<EditText>(R.id.editTextPinMemo)
        pinImageView = dialogView.findViewById<ImageView>(R.id.imageViewPin)

        // MapPinData 객체 내부의 PinDTO 속성에 접근
        editTextName.setText(mapPinToEdit.pinDto.title)
        editTextMemo.setText(mapPinToEdit.pinDto.description)
        currentSelectedImageUri = mapPinToEdit.imageUri
        mapPinToEdit.imageUri?.let { pinImageView.setImageURI(it) } ?: pinImageView.setImageResource(0)

        pinImageView.setOnClickListener { openImagePicker() }

        AlertDialog.Builder(this)
            .setTitle("핀 정보 수정")
            .setView(dialogView)
            .setPositiveButton("확인") { dialog, _ ->
                val newPinName = editTextName.text.toString().trim()
                val newPinMemo = editTextMemo.text.toString().trim()

                val customMarkerIcon = createCustomMarkerIcon(newPinName, currentSelectedImageUri)
                marker.setIcon(customMarkerIcon)
                marker.title = newPinName
                marker.snippet = newPinMemo

                // MapPinData 객체 내부의 PinDTO 속성 및 imageUri 수정
                mapPinToEdit.pinDto.title = newPinName
                mapPinToEdit.pinDto.description = newPinMemo
                mapPinToEdit.imageUri = currentSelectedImageUri

                marker.hideInfoWindow()
                marker.showInfoWindow()
                currentSelectedImageUri = null

                Toast.makeText(this, "핀이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                currentSelectedImageUri = null
                dialog.dismiss()
            }
            .show()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }

    private fun createCustomMarkerIcon(title: String?, imageUri: Uri?): BitmapDescriptor {
        val markerView = LayoutInflater.from(this).inflate(R.layout.custom_marker_layout, null)
        val pinIcon = markerView.findViewById<ImageView>(R.id.marker_pin_icon)
        val thumbnailImage = markerView.findViewById<ImageView>(R.id.marker_thumbnail_image)
        val markerTitle = markerView.findViewById<TextView>(R.id.marker_title)

        pinIcon.setImageResource(R.drawable.ic_map_pin)

        if (imageUri != null) {
            try {
                thumbnailImage.setImageURI(imageUri)
                thumbnailImage.visibility = View.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
                thumbnailImage.visibility = View.GONE
            }
        } else {
            thumbnailImage.visibility = View.GONE
        }

        if (!title.isNullOrEmpty()) {
            markerTitle.text = title
            markerTitle.visibility = View.VISIBLE
        } else {
            markerTitle.visibility = View.GONE
        }

        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        markerView.layout(0, 0, markerView.measuredWidth, markerView.measuredHeight)

        val bitmap = Bitmap.createBitmap(markerView.measuredWidth, markerView.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        markerView.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}