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
import com.example.garage.preferences.SharedPreferencesHelper


class CarServiceWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

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

        val notificationId = SharedPreferencesHelper.getNotificationIdCounter(applicationContext)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(notificationId, builder.build())
        }

        SharedPreferencesHelper.incrementAndSaveNotificationIdCounter(applicationContext)

            return Result.success()

    }

    companion object {
        const val nameKey = "NAME"
    }
}
