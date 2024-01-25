package com.example.garage.workers

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.garage.MainActivity
import com.example.garage.R
import com.example.garage.database.BaseApplication
import kotlin.random.Random


class CarServiceWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {


    val notificationId = 17

    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
            val intent = Intent(applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent: PendingIntent = PendingIntent
                .getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val carName = inputData.getString(nameKey)

            val builder = NotificationCompat.Builder(applicationContext, BaseApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_car)
                .setContentTitle(applicationContext.getString(R.string.notification_title))
                .setContentText(applicationContext.getString(R.string.notification_body,carName))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)


        with(NotificationManagerCompat.from(applicationContext)) {
            notify(Random.nextInt(), builder.build())
        }
            return Result.success()
    }

    companion object {
        const val nameKey = "NAME"
    }
}
