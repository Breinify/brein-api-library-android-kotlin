package com.brein.domain

import android.app.PendingIntent

/**
 *
 * define the different types of notifications
 *
 */
sealed class BreinNotificationModel(
    val channelId: String,
    val notificationId: Int,
    val title: String,
    val content: String,
    val notificationIcon: String?,
    val priority: Int,
    val actions: MutableList<NotificationAction> = mutableListOf()
)

class BasicNotification(
    channelId: String,
    notificationId: Int,
    title: String,
    content: String,
    notificationIcon: String?,
    priority: Int
) : BreinNotificationModel(channelId, notificationId, title, content, notificationIcon, priority)

class TextExpandableNotification(
    channelId: String,
    notificationId: Int,
    title: String,
    content: String,
    notificationIcon: String?,
    priority: Int,
    val longText: String
) : BreinNotificationModel(channelId, notificationId, title, content, notificationIcon, priority)

class PictureExpandableNotification(
    channelId: String,
    notificationId: Int,
    title: String,
    content: String,
    notificationIcon: String?,
    priority: Int,
    val bigContentTitle: String,
    val picture: String?
) : BreinNotificationModel(channelId, notificationId, title, content, notificationIcon, priority)

class PictureActionExpandableNotification(
    channelId: String,
    notificationId: Int,
    title: String,
    content: String,
    notificationIcon: String?,
    priority: Int,
    val bigContentTitle: String,
    val picture: String?,
    actions: MutableList<NotificationAction>
) : BreinNotificationModel(
    channelId,
    notificationId,
    title,
    content,
    notificationIcon,
    priority,
    actions
)

class InboxNotification(
    channelId: String,
    notificationId: Int,
    title: String,
    content: String,
    notificationIcon: String?,
    priority: Int,
    val lines: List<String>
) : BreinNotificationModel(channelId, notificationId, title, content, notificationIcon, priority)

data class NotificationAction(
    val iconId: Int,
    val title: String,
    val actionIntent: PendingIntent?
)