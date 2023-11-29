package com.github.snailman.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


public class Notify {

    private static final Map<String, Notify> notifyMap = new HashMap<>();

    private final NotificationGroup NOTIFICATION_GROUP = NotificationGroup.findRegisteredGroup("SnailMan.Notify");

    private final Project project;

    public void info(@NotNull String content) {
        notify(content, NotificationType.INFORMATION);
    }

    private void notify(@NotNull String content, @NotNull NotificationType type) {
        final Notification notification = NOTIFICATION_GROUP.createNotification(content, type);
        notification.notify(project);
    }

    public static Notify getInstance(Project project) {
        String projectKey = CommonUtil.getProjectKey(project);
        Notify notify = notifyMap.get(projectKey);
        if (notify == null) {
            synchronized (notifyMap) {
                notify = notifyMap.get(projectKey);
                if (notify != null) {
                    return notify;
                }
                notify = new Notify(project);
                notifyMap.put(projectKey, notify);
            }
        }
        return notify;
    }

    private Notify(Project project) {
        this.project = project;
    }

}
