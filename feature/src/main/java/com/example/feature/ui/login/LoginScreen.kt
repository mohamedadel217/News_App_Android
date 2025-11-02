package com.example.feature.ui.login

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import androidx.compose.ui.platform.LocalContext
import com.example.common.BiometricHelper
import com.example.feature.ui.utils.previewPreviewTheme

@Composable
fun LoginScreen(
    onNavigate: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if (BiometricHelper.checkBiometricAvailability(context)) {
            BiometricHelper.showBiometricDialog(
                activity = context as FragmentActivity,
                onFailed = { onClose() },
                onSuccess = { onNavigate() }
            )
        } else {
            onNavigate()
        }
    }
    // Placeholder UI
    Text("Authenticating…")
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    previewPreviewTheme {
        Text("Authenticating…")
    }
}
