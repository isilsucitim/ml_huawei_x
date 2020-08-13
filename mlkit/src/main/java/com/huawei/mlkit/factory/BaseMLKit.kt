package com.huawei.mlkit.factory

import android.content.Context
import android.content.Intent
import com.huawei.mlkit.common.TypeAliasLandMark
import com.huawei.mlkit.common.TypeAliasString
import com.huawei.mlkit.model.LandMarkModel

open class BaseMLKit : MLKit {

    override fun translate(text: String, getTranslatedTextResult: TypeAliasString) {
        println("translate")
    }

    override fun textRecognition(
        context: Context,
        imageData: Intent?,
        getTextRecognitionResult: TypeAliasString
    ) {
        println("textRecognition")
    }

    override fun documentRecognition(imageData: Intent?, getRecognitionDocumentResult: TypeAliasString) {
        println("documentRecognition")
    }

    override fun productVisualSearch(imageData: Intent?, getProductVisualSearchResult: TypeAliasString) {
        println("productVisualSearch")
    }

    override fun objectDetection(context: Context, imageData: Intent?, getTextResponse: TypeAliasString) {
        println("objectDetection")
    }

    override fun objectSegmentation(context: Context, imageData: Intent?, getTextResponse: TypeAliasString) {
        println("objectSegmentation")
    }

    override fun imageDetection(context: Context, imageData: Intent?, getTextResponse: TypeAliasString) {
        println("imageDetection")
    }

    override fun languageIdentification(text: String, getLanguageIdentificationResult: TypeAliasString) {
        println("languageIdentification")
    }

    override fun landmarkRecognition(
        context: Context,
        imageData: Intent?,
        getLandmarkRecognition: TypeAliasLandMark
    ) {
        println("landmarkRecognition")
    }

    override fun faceLandmarkRecognition(
        context: Context,
        imageData: Intent?,
        getFaceLandmarkRecognition: TypeAliasLandMark
    ) {
        println("faceLandmarkRecognition")
    }

    override fun scanBarcode(context: Context, imageData: Intent?, getScanBarcodeResult: TypeAliasString) {
        println("scanBarcode")
    }

    override fun autoVisionEdge(
        context: Context,
        imageData: Intent?,
        getAutoVisionEdgeResult: TypeAliasString
    ) {
        println("autoVisionEdge")
    }

}