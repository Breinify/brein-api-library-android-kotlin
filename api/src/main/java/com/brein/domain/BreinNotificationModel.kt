package com.brein.domain

import android.app.PendingIntent

/**
 *
 * define the different types of notifications
 *
 */
sealed class BreinNotificationModel(
    val channelId: String,
    val title: String,
    val content: String,
    val priority: Int,
    val actions: MutableList<NotificationAction> = mutableListOf()
)

class BasicNotification(
    channelId: String,
    title: String,
    content: String,
    priority: Int
) : BreinNotificationModel(channelId, title, content, priority)

class TextExpandableNotification(
    channelId: String,
    title: String,
    content: String,
    priority: Int,
    val longText: String
) : BreinNotificationModel(channelId, title, content, priority)

class PictureExpandableNotification(
    channelId: String,
    title: String,
    content: String,
    priority: Int,
    val bigContentTitle: String,
    val picture: String?
) : BreinNotificationModel(channelId, title, content, priority)

class PictureActionExpandableNotification(
    channelId: String,
    title: String,
    content: String,
    priority: Int,
    val bigContentTitle: String,
    val picture: String?,
    actions: MutableList<NotificationAction>
) : BreinNotificationModel(channelId, title, content, priority, actions)

class InboxNotification(
    channelId: String,
    title: String,
    content: String,
    priority: Int,
    val lines: List<String>
) : BreinNotificationModel(channelId, title, content, priority)

data class NotificationAction(
    val iconId: Int,
    val title: String,
    val actionIntent: PendingIntent
)