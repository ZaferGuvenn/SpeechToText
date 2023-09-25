package com.lafimsize.speechtotext

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lafimsize.speechtotext.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechIntent:Intent

    private lateinit var recognitionListener: RecognitionListener

    private lateinit var binding: ActivityMainBinding

    private var recordingStatus=false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(this)
        setRecognitionListener()
        setIntent()

        binding.recorderStartStop.setOnClickListener {
            if (recordingStatus){
                stopRecorder()
            }else{
                startRecorder()
            }
        }

        requestPermission()


    }

    private fun requestPermission(){
        // Mikrofon izni kontrolü
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }
    }

    private fun setRecognitionListener(){
        recognitionListener=object :RecognitionListener{
            override fun onReadyForSpeech(params: Bundle?) {
                Log.e("Voice Results:::","Ready")
            }

            override fun onBeginningOfSpeech() {
                Log.e("Voice Results:::","Beginning")
            }

            override fun onRmsChanged(rmsdB: Float) {
                Log.e("Voice Results:::","Rms")
            }

            override fun onBufferReceived(buffer: ByteArray?) {

                Log.e("Voice Results:::","BufferReceived")
            }

            override fun onEndOfSpeech() {
                Log.e("Voice Results:::","Finished")
                stopRecorder()
            }

            override fun onError(error: Int) {
                Log.e("Voice Results:::","$error")
            }

            override fun onResults(results: Bundle?) {
                val voiceResults=results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                if (voiceResults != null && voiceResults.size > 0) {
                    val recognizedText: String = voiceResults[0]
                    Log.e("Voice Results:::",recognizedText)
                    binding.text.text=recognizedText
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                Log.e("Voice Results:::","PartialResults")
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.e("Voice Results:::","Events")
            }
        }

        speechRecognizer.setRecognitionListener(recognitionListener)

    }

    private fun setIntent(){
        speechIntent=Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    }

    private fun startRecorder(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            recordingStatus=true
            speechRecognizer.startListening(speechIntent)
            binding.recorderStartStop.text=getString(R.string.stopp_recording_btn_text)
        }else{
            Toast.makeText(this,getString(R.string.audio_permission_required_toast_msg),Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopRecorder(){


        recordingStatus=false
        binding.recorderStartStop.text=getString(R.string.start_recording_btn_text)
        speechRecognizer.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRecorder()
    }
}