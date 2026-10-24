package com.example.mobiledev.viewmodel

import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    var currentUsername: String? = null
    var currentPassword: String? = null
    var currentProfilePictureResId: Int? = null
}