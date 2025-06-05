package com.gen.SweetSpot

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

private const val MARKER_TAG_TEMP = "temp_pin"
private const val MARKER_TAG_PERMANENT = "permanent_pin"
private const val LOCATION_PERMISSION_REQUEST_CODE = 1001

class MainActivity : AppCompatActivity(),
    OnMapReadyCallback,
    OnMapLongClickListener,
    OnInfoWindowClickListener,
    OnMapClickListener
{

    private lateinit var map: GoogleMap
    private var tempMarker: Marker? = null

    private var currentSelectedImageUri: Uri? = null
    private lateinit var pinImageView: ImageView

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var btnOpenDrawer: ImageButton
    private lateinit var drawerPinsRecyclerView: RecyclerView
    private lateinit var drawerPinAdapter: DrawerPinAdapter
    private val userPinsList = mutableListOf<UserPin>()

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
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        btnOpenDrawer = findViewById(R.id.btn_open_drawer)
        drawerPinsRecyclerView = findViewById(R.id.drawer_pins_recyclerview)
        fabMyLocation = findViewById(R.id.fab_my_location)

        btnOpenDrawer.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        setupDrawerRecyclerView()
        loadUserPinsForDrawer()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        fabMyLocation.setOnClickListener {
            moveToCurrentLocation()
        }

        val navMap = findViewById<LinearLayout>(R.id.nav_map)
        val navBoard = findViewById<LinearLayout>(R.id.nav_board)
        val navProfile = findViewById<LinearLayout>(R.id.nav_profile)

        navMap.setBackgroundResource(R.drawable.nav_active_background)
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

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun setupDrawerRecyclerView() {
        drawerPinAdapter = DrawerPinAdapter(userPinsList) { clickedPin ->
            val pinLocation = LatLng(clickedPin.latitude, clickedPin.longitude)
            if(::map.isInitialized) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(pinLocation, 15f))
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            Toast.makeText(this, "${clickedPin.label} 위치로 이동", Toast.LENGTH_SHORT).show()
        }
        drawerPinsRecyclerView.layoutManager = LinearLayoutManager(this)
        drawerPinsRecyclerView.adapter = drawerPinAdapter
    }

    private fun loadUserPinsForDrawer() {
        val currentUserId = 101L
        val dummyPins = listOf(
            UserPin(1L, "맛집", 36.6350, 127.4520),
            UserPin(2L, "카페", 36.6300, 127.4600),
            UserPin(3L, "명소", 36.6400, 127.4550),
            UserPin(4L, "동물", 36.6250, 127.4650)
        )

        userPinsList.clear()
        userPinsList.addAll(dummyPins)
        if(::drawerPinAdapter.isInitialized) {
            drawerPinAdapter.updateData(userPinsList)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent?.flags?.and(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) != 0) {
        }
    }

    override fun onResume() {
        super.onResume()
        val navMap = findViewById<LinearLayout>(R.id.nav_map)
        val navBoard = findViewById<LinearLayout>(R.id.nav_board)
        val navProfile = findViewById<LinearLayout>(R.id.nav_profile)
        navMap.setBackgroundResource(R.drawable.nav_active_background)
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
            moveToCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun moveToCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(!::map.isInitialized) return
            map.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                } else {
                    Toast.makeText(this, "현재 위치를 가져올 수 없습니다. 위치 서비스가 켜져 있는지 확인하세요.", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener{
                Toast.makeText(this, "위치 정보 접근에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if(::map.isInitialized) {
                        map.isMyLocationEnabled = true
                    }
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
            .snippet("")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))

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
                    0 -> {
                        dialog.dismiss()
                        showEditPermanentPinDialog(marker)
                    }
                    1 -> {
                        dialog.dismiss()
                        marker.remove()
                        Toast.makeText(this, "핀이 제거되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                    2 -> {
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
        pinImageView = dialogView.findViewById(R.id.imageViewPin)

        currentSelectedImageUri = null
        pinImageView.setImageResource(0)


        pinImageView.setOnClickListener {
            openImagePicker()
        }

        AlertDialog.Builder(this)
            .setTitle("새 핀 정보 입력")
            .setView(dialogView)
            .setPositiveButton("확인") { dialog, which ->
                val pinName = editTextName.text.toString().trim()
                val pinMemo = editTextMemo.text.toString().trim()

                val customMarkerIcon = createCustomMarkerIcon(pinName, currentSelectedImageUri)

                val permanentMarkerOptions = MarkerOptions()
                    .position(tempMarker.position)
                    .title(pinName)
                    .snippet(pinMemo)
                    .icon(customMarkerIcon)
                    .anchor(0.5f, 1.0f)

                val permanentMarker = map.addMarker(permanentMarkerOptions)
                permanentMarker?.tag = MARKER_TAG_PERMANENT

                this.tempMarker?.remove()
                this.tempMarker = null
                currentSelectedImageUri = null

                Toast.makeText(this, "핀이 추가되었습니다.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, which ->
                currentSelectedImageUri = null
                dialog.dismiss()
            }
            .show()
    }

    private fun showEditPermanentPinDialog(marker: Marker) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_pin, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextPinName)
        val editTextMemo = dialogView.findViewById<EditText>(R.id.editTextPinMemo)
        pinImageView = dialogView.findViewById(R.id.imageViewPin)

        editTextName.setText(marker.title)
        editTextMemo.setText(marker.snippet)
        currentSelectedImageUri = null
        pinImageView.setImageResource(0)

        pinImageView.setOnClickListener {
            openImagePicker()
        }

        AlertDialog.Builder(this)
            .setTitle("핀 정보 수정")
            .setView(dialogView)
            .setPositiveButton("확인") { dialog, which ->
                val newPinName = editTextName.text.toString().trim()
                val newPinMemo = editTextMemo.text.toString().trim()

                val customMarkerIcon = createCustomMarkerIcon(newPinName, currentSelectedImageUri)

                marker.title = newPinName
                marker.snippet = newPinMemo
                marker.setIcon(customMarkerIcon)

                marker.hideInfoWindow()
                marker.showInfoWindow()

                currentSelectedImageUri = null

                Toast.makeText(this, "핀이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, which ->
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

        if (title != null && title.isNotEmpty()) {
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