package com.hms.readinghabittracker.utils.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.ui.MainActivity
import com.hms.readinghabittracker.ui.goals.GoalItemFragment
import com.hms.readinghabittracker.utils.Constant.INITIAL_FRAGMENT
import com.hms.readinghabittracker.utils.Constant.NOTIFICATIONS_CHANNEL_ID
import com.hms.readinghabittracker.utils.Constant.TAG_GOAL_ID
import com.hms.readinghabittracker.utils.Constant.TAG_GOAL_NAME

class GoalsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val goalName: String = intent.getStringExtra(TAG_GOAL_NAME).toString()
        val goalId: Int = intent.getIntExtra(TAG_GOAL_ID, 0)

        val activityIntent = Intent(context, MainActivity::class.java)
        activityIntent.putExtra(INITIAL_FRAGMENT, GoalItemFragment.TAG)
        activityIntent.putExtra(GoalItemFragment.ID, goalId)
        val notificationManager = NotificationManagerCompat.from(context)

        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(activityIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notification = NotificationCompat.Builder(context, NOTIFICATIONS_CHANNEL_ID)
            .setContentTitle("Reading Time")
            .setContentText(goalName)
            .setSmallIcon(R.drawable.ic_my_books)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify(goalId, notification)
    }
}