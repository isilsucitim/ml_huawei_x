package com.huawei.mlkitproject

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_face_landmark.*

class FaceLandmarkActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_landmark)

        btn_take_photo.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "xx"), PIC_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == PIC_IMAGE) {
            mMLKitManager.getLandmarkRecognition(this, data, getLandmarkRecognition = {
                img_result.setImageBitmap(it.responseBitmap)
            })
        }
    }
}