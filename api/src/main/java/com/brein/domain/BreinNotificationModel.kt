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
    val largeContent: String?,
    val actions: MutableList<NotificationAction> = mutableListOf()
)

class BasicNotification(
    channelId: String,
    notificationId: Int,
    title: String,
    content: String,
    notificationIcon: String?,
    priority: Int,
    largeContent: String?
) : BreinNotificationModel(channelId, notificationId, title, content, notificationIcon, priority,
    largeContent
)

class TextExpandableNotification(
    channelId: String,
    notificationId: Int,
    title: String,
    content: String,
    notificationIcon: String?,
    priority: Int,
    val longText: String,
    largeContent: String?
) : BreinNotificationModel(channelId, notificationId, title, content, notificationIcon, priority,
    largeContent
)

class PictureExpandableNotification(
    channelId: String,
    notificationId: Int,
    title: String,
    content: String,
    notificationIcon: String?,
    priority: Int,
    val picture: String?,
    largeContent: String?
) : BreinNotificationModel(channelId, notificationId, title, content, notificationIcon, priority,
    largeContent
)

class PictureActionExpandableNotification(
    channelId: String,
    notificationId: Int,
    title: String,
    content: String,
    notificationIcon: String?,
    priority: Int,
    val picture: String?,
    largeContent: String?,
    actions: MutableList<NotificationAction>
) : BreinNotificationModel(
    channelId,
    notificationId,
    title,
    content,
    notificationIcon,
    priority,
    largeContent,
    actions
)

class InboxNotification(
    channelId: String,
    notificationId: Int,
    title: String,
    content: String,
    notificationIcon: String?,
    priority: Int,
    val lines: List<String>,
    largeContent: String?
) : BreinNotificationModel(channelId, notificationId, title, content, notificationIcon, priority,
    largeContent
)

data class NotificationAction(
    val iconId: Int,
    val title: String,
    val actionIntent: PendingIntent?
)