package com.huawei.mlkit.factory

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import com.huawei.hmf.tasks.Task
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.classification.MLImageClassificationAnalyzer
import com.huawei.hms.mlsdk.common.MLException
import com.huawei.hms.mlsdk.common.MLFrame
import com.huawei.hms.mlsdk.document.MLDocumentAnalyzer
import com.huawei.hms.mlsdk.document.MLDocumentSetting
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmark
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmarkAnalyzerSetting
import com.huawei.hms.mlsdk.langdetect.MLLangDetectorFactory
import com.huawei.hms.mlsdk.langdetect.cloud.MLRemoteLangDetector
import com.huawei.hms.mlsdk.objects.MLObjectAnalyzerSetting
import com.huawei.hms.mlsdk.productvisionsearch.cloud.MLRemoteProductVisionSearchAnalyzerSetting
import com.huawei.hms.mlsdk.text.MLRemoteTextSetting
import com.huawei.hms.mlsdk.text.MLTextAnalyzer
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslateSetting
import com.huawei.mlkit.common.TypeAliasLandMark
import com.huawei.mlkit.common.TypeAliasString
import com.huawei.mlkit.model.LandMarkModel
import java.io.IOException
import java.text.DecimalFormat
import java.util.*

class HuaweiMLKitImpl : BaseMLKit() {

    override fun translate(text: String, getTranslatedTextResult: TypeAliasString) {
        super.translate(text, getTranslatedTextResult)
        val sourceLangCode = "en"
        val targetLangCode = "zh"

        val setting =
            MLRemoteTranslateSetting.Factory() // Set the source language code. The ISO 639-1 standard is used.
                // This parameter is optional. If this parameter is not set, the system automatically detects the language.
                .setSourceLangCode(sourceLangCode) // Set the target language code. The ISO 639-1 standard is used.
                .setTargetLangCode(targetLangCode)
                .create()

            val mlRemoteTranslator = MLTranslatorFactory.getInstance().getRemoteTranslator(setting)

            val task = mlRemoteTranslator?.asyncTranslate(text)

            task?.addOnSuccessListener { translatedText ->
            getTranslatedTextResult(translatedText)

        }?.addOnFailureListener { e ->
            getTranslatedTextResult("Fail-2")
        }
    }

    override fun textRecognition(
        context: Context,
        imageData: Intent?,
        getTextRecognitionResult: TypeAliasString
    ) {
        super.textRecognition(context, imageData, getTextRecognitionResult)

        val textAnalyzer: MLTextAnalyzer? = MLAnalyzerFactory.getInstance().localTextAnalyzer

        val bitmapImage = imageData?.extras!!["data"] as Bitmap?
        val mlFrame = MLFrame.Creator().setBitmap(Objects.requireNonNull(bitmapImage)).create()
        val task = textAnalyzer?.asyncAnalyseFrame(mlFrame)

        task?.addOnSuccessListener { mlTextResults ->
            val text = mlTextResults.stringValue
            textAnalyzer.stop()
            Log.i("analyzeText", "analyzeText success: $text")
            getTextRecognitionResult(text)

        }?.addOnFailureListener { e ->
            Log.e("analyzeText", "analyzeText Fail: " + e.message)
            getTextRecognitionResult("Fail-4-1")
        }
    }

    override fun objectDetection(context: Context, imageData: Intent?, getTextResponse: TypeAliasString) {
        super.objectDetection(context, imageData, getTextResponse)
        // Use MLObjectAnalyzerSetting.TYPE_PICTURE for static image detection.
        val setting = MLObjectAnalyzerSetting.Factory()
            .setAnalyzerType(MLObjectAnalyzerSetting.TYPE_PICTURE)
            .allowMultiResults()
            .allowClassification()
            .create()

        val analyzer = MLAnalyzerFactory.getInstance().getLocalObjectAnalyzer(setting)

        // Create an MLFrame object using the bitmap, which is the image data in bitmap format.
        val bitmapImage = imageData?.extras!!["data"] as Bitmap?
        val frame = MLFrame.fromBitmap(bitmapImage)

        // Create a task to process the result returned by the object detector.
        val task = analyzer.asyncAnalyseFrame(frame)
        // Asynchronously process the result returned by the object detector.
        task.addOnSuccessListener {
            // Detection success.
            Log.d("MLKit", "staticObjectDetection: " + it[0].typeIdentity.toString())
            getTextResponse(it[0].typeIdentity.toString())
        }.addOnFailureListener {
            // Detection failure.
            getTextResponse("Faill-6")
        }
    }

    override fun objectSegmentation(
        context: Context,
        imageData: Intent?,
        getObjectSegmentationResult: TypeAliasString
    ) {
        super.objectSegmentation(context, imageData, getObjectSegmentationResult)
        val analyzer = MLAnalyzerFactory.getInstance().imageSegmentationAnalyzer

        val bitmapImage = imageData?.extras!!["data"] as Bitmap?
        val frame = MLFrame.fromBitmap(bitmapImage)

        // Create a task to process the result returned by the image segmentation analyzer.
        val task = analyzer.asyncAnalyseFrame(frame)
        // Asynchronously process the result returned by the image segmentation analyzer.
        task.addOnSuccessListener {
            // Detection success.
            Log.d("MLKit", "staticObjectSegmentation: " + it.getOriginal().toString())
            getObjectSegmentationResult(it.getOriginal().toString())
        }.addOnFailureListener {
            // Detection failure.
            getObjectSegmentationResult("Faill-7")
        }
    }
    override fun productVisualSearch(imageData: Intent?, getProductVisualSearchResult: TypeAliasString) {
        val settings = MLRemoteProductVisionSearchAnalyzerSetting.Factory()
            .setLargestNumOfReturns(5)
            .create()

        val productVisionSearchAnalyzer = MLAnalyzerFactory.getInstance().getRemoteProductVisionSearchAnalyzer(settings)

        val bitmapImage = imageData?.extras!!["data"] as Bitmap?
        val mlFrame = MLFrame.Creator().setBitmap(Objects.requireNonNull(bitmapImage)).create()
        val task = productVisionSearchAnalyzer!!.asyncAnalyseFrame(mlFrame)

        task.addOnSuccessListener { mlSearchResult ->
            val list = mlSearchResult.get(0).productList.size
            Log.i("visualSearch", "visualSearch success: $list")
            getProductVisualSearchResult("visualSearch success: $list")
        }.addOnFailureListener { e ->
            val mlException = e as MLException
            Log.e("visualSearch", "visualSearch Fail: " + mlException.errCode)
            getProductVisualSearchResult("Fail-5")
        }
    }

    override fun imageDetection(
        context: Context,
        imageData: Intent?,
        getImageDetectionResult: TypeAliasString
    ) {
        super.imageDetection(context, imageData, getImageDetectionResult)
        val analyzer: MLImageClassificationAnalyzer =
            MLAnalyzerFactory.getInstance().localImageClassificationAnalyzer

        val bitmapImage = imageData?.extras!!["data"] as Bitmap?
        val frame = MLFrame.fromBitmap(bitmapImage)

        val task = analyzer.asyncAnalyseFrame(frame)
        task.addOnSuccessListener {
            // Recognition success.
            Log.d("MLKit", "staticImageDetection: " + it[0].name.toString())
            getImageDetectionResult(it[0].name.toString())

        }.addOnFailureListener {
            // Recognition failure.
            getImageDetectionResult("Faill-8")
        }

        try {
            analyzer.stop()
        } catch (e: IOException) {
            Log.d("MLKit", "staticImageDetection: ${e.message.toString()}")
        }
    }

    override fun languageIdentification(text: String, getLanguageIdentificationResult: TypeAliasString) {
        super.languageIdentification(text, getLanguageIdentificationResult)
        val mlRemoteLangDetect: MLRemoteLangDetector = MLLangDetectorFactory.getInstance()
            .remoteLangDetector
        val firstBestDetectTask: Task<String> =
            mlRemoteLangDetect.firstBestDetect(text)
        firstBestDetectTask.addOnSuccessListener {
            // Processing logic for detection success.
            getLanguageIdentificationResult(it)
        }.addOnFailureListener {
            // Processing logic for detection failure.
            getLanguageIdentificationResult("Fail-4-1")
        }
    }

    override fun landmarkRecognition(context: Context, imageData: Intent?, getLandmarkRecognition: TypeAliasLandMark) {
        super.landmarkRecognition(context, imageData, getLandmarkRecognition)
        val settings = MLRemoteLandmarkAnalyzerSetting.Factory()
            .setLargestNumOfReturns(1)
            .setPatternType(MLRemoteLandmarkAnalyzerSetting.STEADY_PATTERN)
            .create()
        val analyzer = MLAnalyzerFactory.getInstance()
            .getRemoteLandmarkAnalyzer(settings)
        // Create an MLFrame by using android.graphics.Bitmap. Recommended image size: large than 640*640.
        val imageBitmap = imageData?.extras!!["data"] as Bitmap?
        if (imageBitmap != null) {
            val mlFrame = MLFrame.Creator()
                .setBitmap(Objects.requireNonNull(imageBitmap))
                .create()

            val landMarkModel = LandMarkModel()

            val task = analyzer!!.asyncAnalyseFrame(mlFrame)

            task.addOnSuccessListener { landmarkResults ->
                val landmark: MLRemoteLandmark = landmarkResults[0]
                landMarkModel.responseText = getSuccessText(landmark)
                getLandmarkRecognition(landMarkModel)
                // Processing logic for recognition success.

            }.addOnFailureListener {
                // Processing logic for recognition failure
                landMarkModel.responseText = getFailureText()
                getLandmarkRecognition(landMarkModel)
            }
        }
    }
    override fun documentRecognition(imageData: Intent?, getRecognitionDocumentResult: TypeAliasString) {
        super.documentRecognition(imageData, getRecognitionDocumentResult)
        val languageList = listOf("zh", "en")
        val setting = MLDocumentSetting.Factory()
            .setLanguageList(languageList)
            .setBorderType(MLRemoteTextSetting.ARC)
            .create()

        val documentAnalyzer: MLDocumentAnalyzer = MLAnalyzerFactory.getInstance().getRemoteDocumentAnalyzer(setting)
        val bitmapImage = imageData?.extras!!["data"] as Bitmap?
        val mlFrame = MLFrame.Creator().setBitmap(Objects.requireNonNull(bitmapImage)).create()
        val task = documentAnalyzer.asyncAnalyseFrame(mlFrame)
        task.addOnSuccessListener { mlDocumentResult ->
            val document = mlDocumentResult.stringValue
            Log.i("analyzeDocument", "analyzeDocument success: $document")
            getRecognitionDocumentResult(document)
            documentAnalyzer.close()
        }.addOnFailureListener { e ->
            Log.e("analyzeDocument", "analyzeDocument Fail: " + e.localizedMessage)
            getRecognitionDocumentResult("Fail-4-1")
        }
    }






    // THE END :)





























    override fun faceLandmarkRecognition(
        context: Context,
        imageData: Intent?,
        getFaceLandmarkRecognition: TypeAliasLandMark
    ) {
        super.faceLandmarkRecognition(context, imageData, getFaceLandmarkRecognition)
        //
    }

    override fun scanBarcode(context: Context, imageData: Intent?, getScanBarcodeResult: TypeAliasString) {
        super.scanBarcode(context, imageData, getScanBarcodeResult)
        val hmsScan: HmsScan? = imageData?.getParcelableExtra(ScanUtil.RESULT)
        getScanBarcodeResult("${hmsScan?.getOriginalValue()}")
    }

    override fun autoVisionEdge(
        context: Context,
        imageData: Intent?,
        getAutoVisionEdgeResult: TypeAliasString
    ) {
        //
    }

    private fun getSuccessText(landmark: MLRemoteLandmark): String {
        val text: String

        if (landmark.landmark.contains("retCode") ||
            landmark.landmark.contains("retMsg") ||
            landmark.landmark.contains(
                "fail"
            )
        ) {
            text = "The landmark was not recognized."
        } else {
            var longitude = 0.0
            var latitude = 0.0
            var possibility = ""
            var landmarkName = ""
            var result = StringBuilder()
            if (landmark.landmark != null) {
                result = StringBuilder("Landmark information\n" + landmark.landmark)
                landmarkName = landmark.landmark
            }
            if (landmark.positionInfos != null) {
                for (coordinate in landmark.positionInfos) { //           setText(Html.fromHtml("<b>" + myText + "</b>");
                    result.append("\nLatitude: ").append(coordinate.lat)
                    result.append("\nLongitude: ").append(coordinate.lng)
                    result.append("\nPossibility: %")
                        .append(DecimalFormat("##.##").format(landmark.possibility * 100.toDouble()))
                    longitude = coordinate.lng
                    latitude = coordinate.lat
                    possibility = DecimalFormat("##.##")
                        .format(landmark.possibility * 100.toDouble())
                }
            }
            text =
                Html.fromHtml("<big><b>Landmark Information</b></big> <br><big><b>$landmarkName</b></big><br><b>Latitude: </b>$latitude<br><b>Longitude: </b>$longitude<br><b>Possibility: </b>%$possibility")
                    .toString()
        }

        return text
    }

    private fun getFailureText() = "Failure"

}