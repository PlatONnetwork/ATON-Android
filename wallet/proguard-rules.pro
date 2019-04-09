# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-useuniqueclassmembernames
-ignorewarnings
-dontwarn org.htmlcleaner.**
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-dontskipnonpubliclibraryclassmembers
-dontskipnonpubliclibraryclasses

-keepattributes InnerClasses

# smack xmpp
-dontwarn org.jivesoftware.smack.**
-keep class com.jcraft.**{*;}
-keep class org.jivesoftware.**{*;}
-keep class org.xmlpull.**{*;}
-keep class org.**{*;}
-keep class com.nostra13.universalimageloader.**{*;}
-keep class com.app.netstatecontrol.**{*;}

# okhttp
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**

# EventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# bugly.qq.com
-keep public class com.tencent.bugly.**{*;}

# weixin pay
-keep class com.tencent.mm.sdk.** {*;}

# webapp
-keep class com.yap.webapp.was.webruntime.JS2JavaProxy{public *;}

# alipay
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{public *;}
-keep class com.alipay.sdk.app.AuthTask{public *;}

# BaiduApi
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}    
-dontwarn com.baidu.**

#XINGGE
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep class com.tencent.android.tpush.**  {* ;}
-keep class com.tencent.mid.**  {* ;}

#umeng
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class com.pagoda.buy.R$*{
   public static final int *;
}
#5.0以上sdk使用
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Android SDK
-dontwarn android.support.**
-keep class android.support.v4.**{*;}
-keep interface android.support.v4.**{*;}
-keep class !android.support.v7.internal.view.menu.**,android.support.** {*;}
-keep class android.annotation.**{*;}
-keep class org.htmlcleaner.**{*;}

-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.**
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.FragmentActivity

-keepclassmembers public class * implements java.io.Serializable {*;}
-keep public class * implements java.io.Serializable {
    public *;
}
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}
-keepclassmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
    public <fields>;
    private <fields>;
}
-keepclassmembers class **.R$* {
    public static <fields>;
}
 -keepclasseswithmembernames class * {
    native <methods>;
}
# Preserve the special static methods that are required in all enumeration classes.
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
}
 -keepnames class * implements android.os.Parcelable {
     public static final android.os.Parcelable$Creator *;
 }

 -keepclassmembers class * {
     public <methods>;
 }

 -dontshrink
 -dontoptimize
 -dontwarn android.webkit.WebView
 -dontwarn com.umeng.**
 -keep public class javax.**
 -keep public class android.webkit.**
 -keepattributes Exceptions,InnerClasses,Signature
 -keepattributes *Annotation*
 -keepattributes Singature
 -keepattributes SourceFile,LineNumberTable
 -keep public interface com.umeng.socialize.**
 -keep public interface com.umeng.socialize.sensor.**
 -keep public interface com.umeng.scrshot.**
 -keep public class com.umeng.socialize.* {*;}
 -keep class com.umeng.scrshot.**
 -keep class com.umeng.socialize.sensor.**
 -keep class com.umeng.socialize.handler.**
 -keep class com.umeng.socialize.handler.*
  -keep class com.umeng.commonsdk.** {*;}
 -keep public class com.umeng.soexample.R$*{
     public static final int *;
 }
 -keep public class com.umeng.soexample.R$*{
     public static final int *;
 }

## see https://github.com/evant/gradle-retrolambda for java 8
-dontwarn java.lang.invoke.*

# 网易七鱼混淆
-dontwarn com.qiyukf.**
-keep class com.qiyukf.** {*;}

#alibaba
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
#所有使用fastJson的实体类
-keep class * implements com.pagoda.buy.portal.BaseEntity{
    *;
}

#神策
-dontwarn com.sensorsdata.analytics.android.**
-keep class com.sensorsdata.analytics.android.** {
*;
}
-keep class **.R$* {
    <fields>;
}
-keep public class * extends android.content.ContentProvider
-keepnames class * extends android.view.View

-keep class * extends android.app.Fragment {
 public void setUserVisibleHint(boolean);
 public void onHiddenChanged(boolean);
 public void onResume();
 public void onPause();
}
-keep class android.support.v4.app.Fragment {
 public void setUserVisibleHint(boolean);
 public void onHiddenChanged(boolean);
 public void onResume();
 public void onPause();
}
-keep class * extends android.support.v4.app.Fragment {
 public void setUserVisibleHint(boolean);
 public void onHiddenChanged(boolean);
 public void onResume();
 public void onPause();
}

#vlayout
-keepattributes InnerClasses
-keep class com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx { *; }
-keep class android.support.v7.widget.RecyclerView$LayoutParams { *; }
-keep class android.support.v7.widget.RecyclerView$ViewHolder { *; }
-keep class android.support.v7.widget.ChildHelper { *; }
-keep class android.support.v7.widget.ChildHelper$Bucket { *; }
-keep class android.support.v7.widget.RecyclerView$LayoutManager { *; }