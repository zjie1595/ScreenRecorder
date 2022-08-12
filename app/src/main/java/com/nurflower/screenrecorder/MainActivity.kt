package com.nurflower.screenrecorder

import android.Manifest
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var projectionManager: MediaProjectionManager? = null

    private lateinit var screenRecordLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        screenRecordLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                it.data?.let { recordIntent ->
                    RecordService.start(this, recordIntent)
                }
            }

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) {
                    startMediaProjectionRequest()
                } else {
                    Toast.makeText(this, "权限获取失败", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun startRecord(view: View) {
        startCapturing()
    }

    fun stopRecord(view: View) {
        stopCapturing()
    }

    private fun stopCapturing() {
        RecordService.stop(this) { videoPath ->
            val videoView = findViewById<VideoView>(R.id.video_view)
            videoView.setVideoPath(videoPath)
            videoView.start()
        }
    }


    private fun startMediaProjectionRequest() {
        projectionManager =
            applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        screenRecordLauncher.launch(projectionManager?.createScreenCaptureIntent())
    }

    private fun startCapturing() {
        permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

}