package io.schiar.fridgnet.model.datasource.retriever

import io.schiar.fridgnet.model.Image
import kotlinx.coroutines.flow.Flow

/**
 * Interface representing an image retriever that fetches images from a list of URIs.
 */
interface ImageRetriever {
    /**
     * Asynchronously retrieves images from a list of URIs. This method returns a stream ([Flow]) of
     * [Image] objects. The Flow will emit an [Image] object whenever an image is successfully
     * retrieved from a URI in the provided list. In case of errors during retrieval (e.g., network
     * issues, invalid URI), the behavior depends on the specific implementation. It might throw
     * exceptions or emit error signals in the Flow. Please refer to the implementation
     * documentation for details on error handling.
     *
     * @param uris a list of image URIs (strings) to be retrieved.
     * @return     a Flow of [Image] objects retrieved from the provided URIs.
     */
    suspend fun retrieve(uris: List<String>): Flow<Image>
}