package com.huawei.mlkit.factory

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import com.google.firebase.ml.common.FirebaseMLException
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerLocalModel
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.MLFrame
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmark
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmarkAnalyzerSetting
import com.huawei.mlkit.model.LandMarkModel
import com.huawei.mlkit.utils.ImageClassifier
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.DecimalFormat
import java.util.*

class FirebaseMLKitImpl : BaseMLKit() {

    override fun translate(text: String, getTranslatedTextResult: (translatedText: String) -> Unit) {
        super.translate(text, getTranslatedTextResult)
        // Create an English-German translator:
        val options: FirebaseTranslatorOptions = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(FirebaseTranslateLanguage.EN)
            .setTargetLanguage(FirebaseTranslateLanguage.DE)
            .build()

        val translator: FirebaseTranslator =
            FirebaseNaturalLanguage.getInstance().getTranslator(options)

        val conditions: FirebaseModelDownloadConditions = FirebaseModelDownloadConditions.Builder()
            .requireWifi()
            .build()

        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                translator
                    .translate(text)
                    .addOnSuccessListener { translatedText -> // Translation successful.
                        getTranslatedTextResult(translatedText)
                    }
                    .addOnFailureListener {
                        getTranslatedTextResult("Fail-1")
                    }
            }
            .addOnFailureListener { // Model couldn’t be downloaded or other internal error.
                getTranslatedTextResult("Fail-11")
            }
    }

    override fun textRecognition(
        context: Context,
        imageData: Intent?,
        getTextRecognitionResult: (recognitionText: String) -> Unit
    ) {
        super.textRecognition(context, imageData, getTextRecognitionResult)

        try {
            val image = FirebaseVisionImage.fromFilePath(context, imageData?.data!!)
            val textRecognizer: FirebaseVisionTextRecognizer = FirebaseVision.getInstance().onDeviceTextRecognizer

            textRecognizer.processImage(image)
                .addOnSuccessListener { result -> // Task completed successfully
                    // ...
                    // resultTv.setText(result.getText());
                    val resultText = result.text
                    val stringBuilder: StringBuilder = StringBuilder()
                    for (block in result.textBlocks) {
                        val blockText = block.text
                        val blockConfidence = block.confidence
                        val blockLanguages = block.recognizedLanguages
                        val blockCornerPoints = block.cornerPoints
                        val blockFrame = block.boundingBox

                        for (line in block.lines) {
                            val lineText = line.text
                            val lineConfidence = line.confidence
                            val lineLanguages =
                                line.recognizedLanguages
                            val lineCornerPoints = line.cornerPoints
                            val lineFrame = line.boundingBox

                            for (element in line.elements) {
                                stringBuilder.append(
                                    """
                                    ${element.text}
                                    
                                    """.trimIndent()
                                )
                                val elementText = element.text
                                val elementConfidence = element.confidence
                                val elementLanguages =
                                    element.recognizedLanguages
                                val elementCornerPoints =
                                    element.cornerPoints
                                val elementFrame = element.boundingBox
                            }
                            stringBuilder.append("\n\n")
                        }
                        stringBuilder.append("\n\n")
                    }

                    getTextRecognitionResult(stringBuilder.toString())
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    // ...
                    getTextRecognitionResult("Fail-3-1")
                }
        } catch (e: Exception) {
            getTextRecognitionResult("Fail-3-2")
        }
    }

    override fun productVisualSearch(
        context: Context,
        imageData: Intent?,
        getProductVisualSearchResult: (response: String) -> Unit
    ) {
        super.productVisualSearch(context, imageData, getProductVisualSearchResult)
        //
    }

    override fun documentRecognition(
        context: Context,
        imageData: Intent?,
        getRecognitionDocumentResult: (response: String) -> Unit
    ) {
        super.documentRecognition(context, imageData, getRecognitionDocumentResult)
        //
    }

    override fun objectDetection(context: Context, imageData: Intent?, getTextResponse: (text: String) -> Unit) {
        super.objectDetection(context, imageData, getTextResponse)

        try {
            val image = FirebaseVisionImage.fromFilePath(context, imageData?.data!!)
            val labeler = FirebaseVision.getInstance()
                .onDeviceImageLabeler
            labeler.processImage(image)
                .addOnSuccessListener { labels -> // Task completed successfully
                    // ...
                    var text = ""
                    for (label in labels) {
                        text += "${label.text}\n"
                    }
                    getTextResponse(text)
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    // ...
                    println("it $it")
                }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun objectSegmentation(context: Context, imageData: Intent?, getTextResponse: (response: String) -> Unit) {
        super.objectSegmentation(context, imageData, getTextResponse)
        //
    }

    override fun imageDetection(context: Context, imageData: Intent?, getTextResponse: (response: String) -> Unit) {
        super.imageDetection(context, imageData, getTextResponse)
        //
    }

    override fun languageIdentification(text: String, getLanguageIdentificationResult: (text: String) -> Unit) {
        super.languageIdentification(text, getLanguageIdentificationResult)
        val languageIdentifier =
            FirebaseNaturalLanguage.getInstance().languageIdentification

        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode ->
                getLanguageIdentificationResult(languageCode)
            }
            .addOnFailureListener {
                getLanguageIdentificationResult("Fail-4-1")
            }
    }

    override fun landmarkRecognition(
        context: Context,
        imageData: Intent?,
        getLandmarkRecognition: (response: LandMarkModel) -> Unit
    ) {
        super.landmarkRecognition(context, imageData, getLandmarkRecognition)

        val btm: Bitmap
        try {
            btm = MediaStore.Images.Media.getBitmap(context.contentResolver, imageData?.data)
            val mutBtm = btm.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(mutBtm)
            try {
                val image = FirebaseVisionImage.fromFilePath(
                    context,
                    Objects.requireNonNull(imageData?.data!!)
                )
                val options = FirebaseVisionFaceDetectorOptions.Builder()
                    .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                    .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                    .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                    .build()
                val detector = FirebaseVision.getInstance()
                    .getVisionFaceDetector(options)
                val result =
                    detector.detectInImage(image)
                        .addOnSuccessListener { faces ->
                            val landMarkModel = LandMarkModel()
                            // Task completed successfully
                            // ...
                            for (face in faces) {
                                val bounds = face.boundingBox
                                val p = Paint()
                                p.color = Color.YELLOW
                                p.style = Paint.Style.STROKE
                                canvas.drawRect(bounds, p)

                                landMarkModel.responseBitmap = mutBtm
                                getLandmarkRecognition(landMarkModel)
                                //imageView.setImageBitmap(mutBtm)

                                val rotY =
                                    face.headEulerAngleY // Head is rotated to the right rotY degrees
                                val rotZ =
                                    face.headEulerAngleZ // Head is tilted sideways rotZ degrees

                                // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                                // nose available):
                                val leftEar =
                                    face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR)
                                if (leftEar != null) {
                                    val leftEarPos = leftEar.position
                                    val rect = Rect(
                                        (leftEarPos.x - 20).toInt(),
                                        (leftEarPos.y - 20).toInt(),
                                        (leftEarPos.x + 20).toInt(),
                                        (leftEarPos.y + 20).toInt()
                                    )
                                    canvas.drawRect(rect, p)
                                    //
                                    landMarkModel.responseBitmap = mutBtm
                                    getLandmarkRecognition(landMarkModel)
                                    //imageView.setImageBitmap(mutBtm)
                                }

                                // If contour detection was enabled:
                                val leftEyeContour =
                                    face.getContour(FirebaseVisionFaceContour.LEFT_EYE).points
                                val upperLipBottomContour =
                                    face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).points

                                // If classification was enabled:
                                val p2 = Paint()
                                p2.color = Color.BLACK
                                p2.textSize = 16f
                                if (face.smilingProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                    val smileProb = face.smilingProbability
                                    if (smileProb > 0.5) {
                                        canvas.drawText(
                                            "Smiling for now :D",
                                            bounds.exactCenterX(),
                                            bounds.exactCenterY(),
                                            p2
                                        )
                                        //
                                        landMarkModel.responseBitmap = mutBtm
                                        getLandmarkRecognition(landMarkModel)
                                        //imageView.setImageBitmap(mutBtm)
                                    } else {
                                        canvas.drawText(
                                            "Not Smiling finally :D",
                                            bounds.exactCenterX(),
                                            bounds.exactCenterY(),
                                            p2
                                        )
                                        //
                                        landMarkModel.responseBitmap = mutBtm
                                        getLandmarkRecognition(landMarkModel)
                                        //imageView.setImageBitmap(mutBtm)
                                    }
                                }
                                if (face.rightEyeOpenProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                    val rightEyeOpenProb = face.rightEyeOpenProbability
                                }

                                // If face tracking was enabled:
                                if (face.trackingId != FirebaseVisionFace.INVALID_ID) {
                                    val id = face.trackingId
                                }
                            }
                        }
                        .addOnFailureListener {
                            // Task failed with an exception
                            // ...
                            it.printStackTrace()
                        }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun scanBarcode(context: Context, imageData: Intent?, getScanBarcodeResult: (response: String) -> Unit) {
        super.scanBarcode(context, imageData, getScanBarcodeResult)
        val image: FirebaseVisionImage
        try {
            image = FirebaseVisionImage.fromFilePath(
                context,
                Objects.requireNonNull(imageData?.data!!)
            )

            //  FirebaseVisionBarcodeDetectorOptions options =
            //          new FirebaseVisionBarcodeDetectorOptions.Builder()
            //                  .setBarcodeFormats(
            //                          FirebaseVisionBarcode.FORMAT_QR_CODE,
            //                          FirebaseVisionBarcode.FORMAT_AZTEC)
            //                  .build();
            val detector = FirebaseVision.getInstance()
                .visionBarcodeDetector
            val result =
                detector.detectInImage(image)
                    .addOnSuccessListener { barcodes -> // Task completed successfully
                        // ...
                        for (barcode in barcodes) {
                            val bounds = barcode.boundingBox
                            val corners = barcode.cornerPoints
                            val rawValue = barcode.rawValue
                            val valueType = barcode.valueType
                            when (valueType) {
                                FirebaseVisionBarcode.TYPE_WIFI -> {
                                    val ssid =
                                        Objects.requireNonNull(barcode.wifi)!!.ssid
                                    val password = barcode.wifi!!.password
                                    val type = barcode.wifi!!.encryptionType
                                    val response = "$ssid  $password  $type"
                                    getScanBarcodeResult(response)
                                    //resultTv.setText("$ssid  $password  $type")
                                }
                                FirebaseVisionBarcode.TYPE_URL -> {
                                    val title =
                                        Objects.requireNonNull(barcode.url)!!.title
                                    val url = barcode.url!!.url
                                    val response = "$title  $url"
                                    getScanBarcodeResult(response)
                                    //resultTv.setText("$title  $url")
                                }
                                FirebaseVisionBarcode.TYPE_PHONE -> {
                                    val phone =
                                        Objects.requireNonNull(barcode.phone)!!.number
                                    getScanBarcodeResult(phone ?: "")
                                    //resultTv.setText(phone)
                                }
                                FirebaseVisionBarcode.TYPE_EMAIL -> {
                                    val email = barcode.email!!.address
                                    val subject = barcode.email!!.subject
                                    val email_body = barcode.email!!.body
                                    val response = """
                                            $email
                                            $subject
                                            $email_body
                                            """.trimIndent()
                                    getScanBarcodeResult(response)
                                    //resultTv.setText("""$email$subject$email_body""".trimIndent())
                                }
                            }
                        }
                    }
                    .addOnFailureListener {
                        // Task failed with an exception
                        // ...
                        getScanBarcodeResult("Fail-6-1")
                    }
        } catch (e: IOException) {
            e.printStackTrace()
            getScanBarcodeResult("Fail-6-2")
        }
    }

    override fun autoVisionEdge(
        context: Context,
        imageData: Intent?,
        getAutoVisionEdgeResult: (response: String) -> Unit
    ) {

        FirebaseVisionImage.fromFilePath(context, imageData?.data!!).also {
            // Classify image.
            try {
                val classifier = ImageClassifier(context)
                classifier.classifyFrame(it.bitmap)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        println("kanka-success")
                    } else {
                        println("kanka-failure")
                    }
                }
            } catch (e: FirebaseMLException) {
                e.printStackTrace()
            }
        }
    }
}