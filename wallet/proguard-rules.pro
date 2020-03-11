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
-dontusemixedcaseclassnames
-optimizationpasses 5
-allowaccessmodification
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment
# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
 native <methods>;
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
-keepclassmembers class * extends android.app.Activity {
 public void *(android.view.View);
}
# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
 public static **[] values();
 public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
 public static final android.os.Parcelable$Creator *;
}

# 保持测试相关的代码
-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**

# okhttp
-keep class com.squareup.okhttp3.** { *;}
-dontwarn com.squareup.okhttp3.**
-keep class okhttp3.internal.huc.OkHttpURLConnection{
    *;
}
-dontwarn okhttp3.internal.huc.OkHttpURLConnection
-dontwarn okio.**

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform

# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# EventBus
-keep class org.greenrobot.eventbus.**{
    *;
}
-dontwarn org.greenrobot.eventbus.**
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

-keep class com.platon.aton.event.Event{
    *;
}
-dontwarn com.platon.aton.event.Event

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#umeng
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class com.platon.aton.R$*{
   public static final int *;
}
#5.0以上sdk使用
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}


## see https://github.com/evant/gradle-retrolambda for java 8
-dontwarn java.lang.invoke.*

#alibaba
-keepattributes Signature
-keep class com.alibaba.fastjson.** { *; }
-dontwarn com.alibaba.fastjson.**
#所有使用fastJson的实体类
-keep class com.platon.aton.entity.**{
    *;
}
#alipay
-keep class com.alipay.sdk.**{
    *;
}
-dontwarn com.alipay.sdk.**

#bitcoinj
-keep class org.bitcoin.**{
    *;
}
-dontwarn org.bitcoin.**

-keep class org.bitcoinj.**{
    *;
}
-dontwarn org.bitcoinj.**

-keep class com.subgraph.orchid.**{
    *;
}
-dontwarn com.subgraph.orchid.**

-keep class org.bouncycastle.**{
    *;
}
-dontwarn org.bouncycastle.**

-dontnote pringfox.documentation.spring.web.json.Json
-dontwarn pringfox.documentation.spring.web.json.Json

-dontnote net.sf.json.JSONNull
-dontwarn net.sf.json.JSONNull

-dontnote org.springframework.core.ResolvableType
-dontwarn org.springframework.core.ResolvableType

#rxjava
-keep class rx.internal.util.**{
    *;
}
-dontwarn rx.internal.util.**

-dontwarn org.slf4j.**
-dontwarn jnr.posix.**
-dontwarn com.kenai.jffi.**
-dontwarn com.google.common.cache.**
-dontwarn com.google.common.primitives.**
-keep class com.fasterxml.jackson.**{
    *;
}
-dontwarn com.fasterxml.jackson.**

-keep class org.web3j.**{
    *;
}
-dontwarn org.web3j.**

-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
long producerIndex;
long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

-keep class com.juzhen.framework.network.**{
    *;
}
-dontwarn com.juzhen.framework.network.**

-dontpreverify
-dontwarn com.umeng.**
-dontwarn com.facebook.**
-keep public class javax.**
-keep public class android.webkit.**
-dontwarn android.support.v4.**
-keep enum com.facebook.**
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public interface com.facebook.**
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**

-keep public class com.umeng.socialize.* {*;}

-keep class com.facebook.**
-keep class com.facebook.** { *; }
-keep class com.umeng.scrshot.**
-keep class com.umeng.socialize.sensor.**
-keep class com.umeng.socialize.handler.**
-keep class com.umeng.socialize.handler.*
-keep class UMMoreHandler{*;}

-keep class com.twitter.** { *; }

-keep public class com.umeng.com.umeng.soexample.R$*{
    public static final int *;
}

-keep class com.umeng.socialize.impl.ImageImpl {*;}
-keep class com.sina.** {*;}
-dontwarn com.sina.**
