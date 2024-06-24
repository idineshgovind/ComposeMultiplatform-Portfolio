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
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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

    var text by remember { mutableStateOf("") }


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
                            text("Act as an experienced Android app developer. For the provided image, use Jetpack Compose to build the screen, ensuring the Compose Preview replicates the image as accurately as possible. Make sure to include all necessary imports and use Material3 components. Ensure text alignment, button alignment, and image size and shape match the provided image exactly. Provide only the code, without any notes or explanations.")
                            text(text)
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
                .background(Color.White)
                .padding(16.dp)
        ) {
            item {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Compose UI Code Generator",
                        style = MaterialTheme.typography.h3,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = buildAnnotatedString {
                            append("Add extra prompts first, then select an image. Tool generates code once an image is selected. ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                                append("Extra prompts are optional.")
                            }
                        },
                        style = MaterialTheme.typography.body1,
                        color = Color.Gray
                    )

                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Extra Prompts & Comments (optional)", color = Color(0xFF6200EE)) },
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF6200EE), // Purple color for the border when focused
                            unfocusedBorderColor = Color.Gray // Gray color for the border when unfocused
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp) // Rounded corners
                    )
                    Button(onClick = {
                        showFilePicker = true
                    }) {
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






