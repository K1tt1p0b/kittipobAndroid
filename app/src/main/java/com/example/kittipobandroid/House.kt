package com.example.kittipobandroid

data class House(
    val id: Int,                   // ไอดีของบ้าน
    val AreaSize: Double,          // ขนาดพื้นที่ของบ้าน
    val Bedrooms: Int,             // จำนวนห้องนอน
    val Bathrooms: Int,            // จำนวนห้องน้ำ
    val Price: Double,             // ราคาของบ้าน
    val HouseCondition: String,    // สภาพของบ้าน (เช่น "ใหม่", "มือสอง")
    val HouseType: String,         // ประเภทของบ้าน (เช่น "ทาวน์เฮาส์", "บ้านเดี่ยว")
    val YearBuilt: Int,            // ปีที่สร้างบ้าน
    val ParkingSpaces: Int,        // จำนวนที่จอดรถ
    val Address: String,           // ที่อยู่ของบ้าน
    val HouseImage: String         // URL ของรูปภาพบ้าน
)
