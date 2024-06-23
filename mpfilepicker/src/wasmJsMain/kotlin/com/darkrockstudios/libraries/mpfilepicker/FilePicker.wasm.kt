package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.browser.document
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.w3c.dom.Document
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.asList
import org.w3c.files.File
import org.w3c.files.FileReader
import kotlin.coroutines.*

public actual data class PlatformFile(
	val file: File,
)

@Composable
public actual fun FilePicker(
	show: Boolean,
	initialDirectory: String?,
	fileExtensions: List<String>,
	title: String?,
	onFileSelected: FileSelected,
) {
	LaunchedEffect(show) {
		if (show) {
			val fixedExtensions = fileExtensions.map { ".$it" }
			val file: List<File> = document.selectFilesFromDisk(fixedExtensions.joinToString(","), false)
			val platformFile = PlatformFile(file.first())
			val platformFileAsByteArray = readFileAsByteArray(platformFile.file)
			onFileSelected(platformFileAsByteArray)
		}
	}
}

@Composable
public actual fun MultipleFilePicker(
	show: Boolean,
	initialDirectory: String?,
	fileExtensions: List<String>,
	title: String?,
	onFileSelected: FilesSelected
) {
	LaunchedEffect(show) {
		if (show) {
			val fixedExtensions = fileExtensions.map { ".$it" }
			val files: List<File> = document.selectFilesFromDisk(fixedExtensions.joinToString(","), true)
			val webFiles = files.map { PlatformFile(it) }
			onFileSelected(webFiles)
		}
	}
}

@Composable
public actual fun DirectoryPicker(
	show: Boolean,
	initialDirectory: String?,
	title: String?,
	onFileSelected: (String?) -> Unit,
) {
	// in a browser we can not pick directories
	throw NotImplementedError("DirectoryPicker is not supported on the web")
}

private suspend fun Document.selectFilesFromDisk(
	accept: String,
	isMultiple: Boolean
): List<File> = suspendCoroutine {
	val tempInput = (createElement("input") as HTMLInputElement).apply {
		type = "file"
		style.display = "none"
		this.accept = accept
		multiple = isMultiple
	}

	tempInput.onchange = { changeEvt ->
		try {
			val inputElement = changeEvt.target as HTMLInputElement
			val files = inputElement.files?.asList() ?: emptyList()
			it.resume(files)
		} catch (e: Throwable) {
			it.resumeWithException(e)
		}
	}

	body!!.append(tempInput)
	tempInput.click()
	tempInput.remove()
}

public suspend fun readFileAsText(file: File): String = suspendCoroutine {
	val reader = FileReader()
	reader.onload = { loadEvt ->
		try {
			val eventFileReader = loadEvt.target?.let { it as FileReader }
			val content = eventFileReader!!.result?.unsafeCast<JsString>()!!
			it.resume(content.toString())
		} catch (e: Throwable) {
			it.resumeWithException(e)
		}
	}
	reader.readAsText(file, "UTF-8")
}

public suspend fun readFileAsByteArray(file: File): ByteArray = suspendCoroutine {
	val reader = FileReader()
	reader.onload = {loadEvt ->
		try {
			val eventFileReader = loadEvt.target?.let { it as FileReader }!!
			val content = eventFileReader.result as ArrayBuffer
			val array = Uint8Array(content)

			val fileByteArray = ByteArray(array.length)
			for (i in 0 until array.length) {
				fileByteArray[i] = array[i]
			}
			it.resumeWith(Result.success(fileByteArray))
		} catch (e: Throwable) {
			it.resumeWithException(e)
		}
	}
	reader.readAsArrayBuffer(file)
}
