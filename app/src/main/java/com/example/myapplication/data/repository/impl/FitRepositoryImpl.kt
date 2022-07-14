package jp.co.sgaas.data.repository.impl

import android.util.Log
import com.google.android.gms.fitness.HistoryClient
import com.google.android.gms.fitness.RecordingClient
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.tasks.OnSuccessListener
import com.example.myapplication.data.repository.FitRepository
import javax.inject.Inject

class FitRepositoryImpl @Inject constructor(
    private val historyClient: HistoryClient,
    private val recordingClient: RecordingClient
) : FitRepository {
    val TAG = "StepsRepository"
    override fun subscribe(listener: OnSuccessListener<DataSet>) {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        recordingClient
            .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(
                        TAG,
                        "Successfully subscribed!"
                    )
                    readData(listener)
                } else {
                    Log.w(
                        TAG,
                        "There was a problem subscribing.",
                        task.exception
                    )
                }
            }
    }

    override fun readData(listener: OnSuccessListener<DataSet>) {
        historyClient
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener(listener)
            .addOnFailureListener { e ->
                Log.w(
                    TAG,
                    "There was a problem getting the step count.",
                    e
                )
            }
    }

//    override fun setDataPointListener(listener: OnDataPointListener) {
//        TODO("Not yet implemented")
//    }
}