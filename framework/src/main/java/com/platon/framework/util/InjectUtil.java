package com.platon.framework.util;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Field;

public class InjectUtil {
    public static int getContentLayoutId(Object object) {
        ContentView contentView = object.getClass().getAnnotation(ContentView.class);
        return contentView == null ? 0 : contentView.value();
    }

    public static void injectViews(Object injectedSource, View sourceView) {
        injectViewsFromClass(injectedSource, injectedSource.getClass(), sourceView);
        injectViewsFromClass(injectedSource, injectedSource.getClass().getSuperclass(), sourceView);
    }

    public static void injectViewsFromClass(Object injectedSource, Class<?> clazz, View sourceView) {
        Field[] fields = clazz.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    InjectView viewInject = field.getAnnotation(InjectView.class);
                    if (viewInject != null) {
                        int viewId = viewInject.value();
                        field.set(injectedSource, sourceView.findViewById(viewId));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void injectViews(Object injectedSource, Activity activity) {
        injectViewsFromClass(injectedSource, injectedSource.getClass(), activity);
        injectViewsFromClass(injectedSource, injectedSource.getClass().getSuperclass(), activity);
    }

    private static void injectViewsFromClass(Object injectedSource, Class<?> clazz, Activity activity) {
        Field[] fields = clazz.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    InjectView viewInject = field.getAnnotation(InjectView.class);
                    if (viewInject != null) {
                        int viewId = viewInject.value();
                        field.set(injectedSource, activity.findViewById(viewId));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
