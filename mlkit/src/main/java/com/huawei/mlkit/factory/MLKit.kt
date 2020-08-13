package com.huawei.mlkit.factory

import android.content.Context
import android.content.Intent
import com.huawei.mlkit.common.TypeAliasLandMark
import com.huawei.mlkit.common.TypeAliasString

interface MLKit {

    fun translate(text: String, getTranslatedTextResult: TypeAliasString)

    fun objectDetection(context: Context, imageData: Intent?, getTextResponse: TypeAliasString)

    fun objectSegmentation(context: Context, imageData: Intent?, getObjectSegmentationResult: TypeAliasString)

    fun imageDetection(context: Context, imageData: Intent?, getImageDetectionResult: TypeAliasString)

    fun languageIdentification(text: String, getLanguageIdentificationResult: TypeAliasString)

    fun landmarkRecognition(
        context: Context,
        imageData: Intent?,
        getLandmarkRecognition: TypeAliasLandMark
    )

    fun faceLandmarkRecognition(
        context: Context,
        imageData: Intent?,
        getLandmarkRecognition: TypeAliasLandMark
    )

    fun scanBarcode(context: Context, imageData: Intent?, getScanBarcodeResult: TypeAliasString)

    fun autoVisionEdge(context: Context, imageData: Intent?, getAutoVisionEdgeResult: TypeAliasString)

    fun textRecognition(
        context: Context,
        imageData: Intent?,
        getTextRecognitionResult: TypeAliasString
    )

    fun documentRecognition(
        imageData: Intent?,
        getRecognitionDocumentResult: TypeAliasString
    )

    fun productVisualSearch(imageData: Intent?, getProductVisualSearchResult: TypeAliasString)
}