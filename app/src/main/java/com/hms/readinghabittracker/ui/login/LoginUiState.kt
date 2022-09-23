package com.hms.readinghabittracker.ui.login

data class LoginUiState(
    val loading: Boolean,
    val isUSerSigned: List<Boolean>,
    val error: List<String>
) {
    companion object {
        fun initial() = LoginUiState(
            loading = false,
            isUSerSigned = emptyList(),
            error = emptyList()
        )
    }
}