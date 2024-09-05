package com.example.kittipobandroid

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

class AddHouse : AppCompatActivity() {

    private lateinit var editAreaSize: EditText
    private lateinit var editBedroom: EditText
    private lateinit var editBathrooms: EditText
    private lateinit var editPrice: EditText
    private lateinit var editCondition: EditText
    private lateinit var editHouseType: EditText
    private lateinit var editYearBuilt: EditText
    private lateinit var editParkingSpaces: EditText
    private lateinit var editAddress: EditText
    private lateinit var saveButton: Button
    private lateinit var selectImageButton: Button
    private lateinit var imageView: ImageView

    private val client = OkHttpClient()
    private val gson = Gson()

    private var selectedImageUri: Uri? = null

    private val selectImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                selectedImageUri = data?.data
                updateImageView()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_house) // Ensure this matches your actual layout file name

        // Initialize views
        editAreaSize = findViewById(R.id.areaSizeEditText)
        editBedroom = findViewById(R.id.bedroomsEditText)
        editBathrooms = findViewById(R.id.bathroomsEditText)
        editPrice = findViewById(R.id.priceEditText)
        editCondition = findViewById(R.id.houseConditionEditText)
        editHouseType = findViewById(R.id.houseTypeEditText)
        editYearBuilt = findViewById(R.id.yearBuiltEditText)
        editParkingSpaces = findViewById(R.id.parkingSpacesEditText)
        editAddress = findViewById(R.id.addressEditText)
        saveButton = findViewById(R.id.saveButton)
        selectImageButton = findViewById(R.id.selectImageButton) // Ensure this ID is in XML
        imageView = findViewById(R.id.imageView)

        // Set button listeners
        selectImageButton.setOnClickListener {
            selectImage()
        }

        saveButton.setOnClickListener {
            saveHouse() // Method name changed to reflect the new context
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectImageLauncher.launch(intent)
    }

    private fun updateImageView() {
        selectedImageUri?.let { uri ->
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                imageView.setImageBitmap(bitmap)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }
    private fun saveHouse() {
        val areaSize = editAreaSize.text.toString().trim()
        val bedroom = editBedroom.text.toString().toIntOrNull() ?: 0
        val bathrooms = editBathrooms.text.toString().toIntOrNull() ?: 0
        val price = editPrice.text.toString().toDoubleOrNull() ?: 0.0
        val condition = editCondition.text.toString().trim()
        val houseType = editHouseType.text.toString().trim()
        val yearBuilt = editYearBuilt.text.toString().toIntOrNull() ?: 0
        val parkingSpaces = editParkingSpaces.text.toString().toIntOrNull() ?: 0
        val address = editAddress.text.toString().trim()

        if (areaSize.isEmpty() || condition.isEmpty() || houseType.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_LONG).show()
            return
        }

        val file = selectedImageUri?.let { uri ->
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(cacheDir, "image.jpg")
            file.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            file
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("AreaSize", areaSize)
            .addFormDataPart("Bedrooms", bedroom.toString())
            .addFormDataPart("Bathrooms", bathrooms.toString())
            .addFormDataPart("Price", price.toString())
            .addFormDataPart("HouseCondition", condition)
            .addFormDataPart("HouseType", houseType)
            .addFormDataPart("YearBuilt", yearBuilt.toString())
            .addFormDataPart("ParkingSpaces", parkingSpaces.toString())
            .addFormDataPart("Address", address)
            .apply {
                file?.let {
                    addFormDataPart(
                        "HouseImage",
                        it.name,
                        it.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    )
                }
            }
            .build()


        val request = Request.Builder()
            .url("http://10.13.4.106:3000/add/houses") // Update URL to reflect the new context
            .post(requestBody)
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.newCall(request).execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddHouse,
                            "House added successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@AddHouse,
                            "Server Error: ${response.code}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@AddHouse,
                        "Error Adding House: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
