package com.example.myapplication.utils

import androidx.annotation.Size
import androidx.fragment.app.Fragment
import pub.devrel.easypermissions.EasyPermissions

object Permission {
    fun requestPermissions(
        fragment: Fragment,
        rationale: String,
        requestCode: Int,
        @Size(min = 1) vararg permission: String
    ) {
        EasyPermissions.requestPermissions(fragment, rationale, requestCode, permission[0])
    }
}