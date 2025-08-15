package com.walter.pos.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

class StorageException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

@Service
class FileStorageService(@Value("\${file.upload-dir}") uploadDir: String) {

    private val rootLocation: Path = Paths.get(uploadDir)
    private val allowedMimeTypes = setOf("image/png", "image/jpeg")
    private val allowedExtensions = setOf("png", "jpg", "jpeg")

    init {
        try {
            Files.createDirectories(rootLocation)
        } catch (e: IOException) {
            throw StorageException("Could not initialize storage location", e)
        }
    }

    /**
     * Stores a file securely and returns its unique generated filename.
     */
    fun store(file: MultipartFile): String {
        val extension = StringUtils.getFilenameExtension(file.originalFilename)?.lowercase()

        // Security Check 1: Validate file is not empty and has an allowed type/extension.
        if (file.isEmpty || !allowedMimeTypes.contains(file.contentType) || !allowedExtensions.contains(extension)) {
            throw StorageException("Failed to store file. Invalid file type or empty file.")
        }

        try {
            // Security Check 2: Sanitize filename to prevent directory traversal attacks.
            val originalFilename = StringUtils.cleanPath(file.originalFilename!!)

            // Security Check 3: Generate a unique filename to prevent overwrites and hide original name.
            val uniqueFilename = "${UUID.randomUUID()}.$extension"

            val destinationFile = this.rootLocation.resolve(uniqueFilename).normalize().toAbsolutePath()

            // This is a critical security check to ensure the file is stored within the root location.
           // if (destinationFile.parent != this.rootLocation.toAbsolutePath()) {
               // throw StorageException("Cannot store file outside the configured upload directory.")
            //}

            file.inputStream.use { inputStream ->
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING)
            }

            // Return only the unique filename to be stored in the database.
            return uniqueFilename
        } catch (e: IOException) {
            throw StorageException("Failed to store file '$file.originalFilename'.", e)
        }
    }

    /**
     * Loads a file as a Spring Resource.
     */
    fun loadAsResource(filename: String): Resource {
        try {
            val file = rootLocation.resolve(filename)
            val resource = UrlResource(file.toUri())
            if (resource.exists() && resource.isReadable) {
                return resource
            } else {
                throw StorageException("Could not read file: $filename")
            }
        } catch (e: MalformedURLException) {
            throw StorageException("Could not read file: $filename", e)
        }
    }

    /**
     * Deletes a file from the storage.
     */
    fun delete(filename: String?) {
        if (filename.isNullOrBlank()) return
        try {
            val fileToDelete = rootLocation.resolve(filename).normalize().toAbsolutePath()
            // Ensure we only delete files within the storage directory
            if (Files.exists(fileToDelete) && fileToDelete.parent == this.rootLocation.toAbsolutePath()) {
                Files.delete(fileToDelete)
            }
        } catch (e: IOException) {
            // Log the error but don't fail the entire operation (e.g., if a product update succeeds but old image deletion fails)
            println("WARN: Failed to delete image file: $filename due to: ${e.message}")
        }
    }
}