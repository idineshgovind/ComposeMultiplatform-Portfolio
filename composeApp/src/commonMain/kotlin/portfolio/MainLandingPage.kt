package portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import dev.shreyaspatil.ai.client.generativeai.type.asTextOrNull
import dev.shreyaspatil.ai.client.generativeai.type.content
import dev.shreyaspatil.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun MainLandingPage() {
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    val responses = remember { mutableStateOf("") }
    var imageByteArray by remember { mutableStateOf<ByteArray?>(null) }
    var showFilePicker by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }

    val fileType = listOf("jpg", "png")

    MaterialTheme(colors = darkColors()) {
        val model = GenerativeModel(
            "gemini-1.5-flash",
            // Retrieve API key as an environmental variable defined in a Build Configuration
            // see https://github.com/google/secrets-gradle-plugin for further instructions
            "api-key",
            generationConfig = generationConfig {
                temperature = 1f
                topK = 64
                topP = 0.95f
                maxOutputTokens = 8192
            },
        )

        FilePicker(show = showFilePicker, fileExtensions = fileType) { platformFileByteArray ->
            showFilePicker = false
            imageByteArray = platformFileByteArray
            if (imageByteArray != null) {
                isLoading = true
                coroutineScope.launch {
                    val response = model.generateContent(
                        content {
                            image(imageByteArray!!)
                            text("input: Act as an Android app developer. For the image provided, use Jetpack Compose to build the screen so that the Compose Preview is as close to this image as possible. Also make sure to include imports and use Material3.\n")
                        }
                    )

                    responses.value =
                        response.candidates.first().content.parts.first().asTextOrNull().toString()
                            .replace("```kotlin", "")
                            .replace("```", "")
                            .trim()
                    isLoading = false
                }
            }
        }


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
                .padding(16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(onClick = { showFilePicker = true }) {
                        Text("Pick a file")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (showSnackbar) {
                    Snackbar(
                        action = {
                            Button(onClick = { showSnackbar = false }) {
                                Text("Dismiss")
                            }
                        },
                        modifier = Modifier.padding(8.dp)
                    ) { Text("Code copied to clipboard") }
                }
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    if (responses.value.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E1E1E))
                                .padding(8.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "kotlin",
                                        color = Color.White,
                                        modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                                    )
                                    Button(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(responses.value))
                                            showSnackbar = true
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = Color(0xFF1E1E1E)
                                        ),
                                        modifier = Modifier.padding(end = 8.dp, top = 8.dp)
                                    ) {
                                        Text("Copy code", color = Color.White)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = responses.value,
                                    color = Color.White,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .background(Color(0xFF2D2D2D))
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}




