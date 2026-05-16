package org.leonardorat.chat.ui.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.leonardorat.chat.auth.AuthManager

@Composable
fun AuthScreen(
    authManager: AuthManager,
    onAuthSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var error by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK || result.data == null) {
            error = "Authorization cancelled"
            return@rememberLauncherForActivityResult
        }

        scope.launch {
            try {
                authManager.handleAuthorizationResponse(result.data!!)
                onAuthSuccess()
            } catch (e: Exception) {
                error = e.message ?: "Authorization failed"
            }
        }
    }

    Column(
        modifier = Modifier.padding(24.dp)
    ) {
        Text("Connect Gmail")

        Button(
            modifier = Modifier.padding(top = 16.dp),
            onClick = {
                error = null
                launcher.launch(authManager.createSignInIntent())
            }
        ) {
            Text("Choose Gmail account")
        }

        error?.let {
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = it
            )
        }
    }
}