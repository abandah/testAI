# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keepattributes Signature
-keepclassmembers class ccom.ubitc.api_send_model.** {*;}
-keepclassmembers class ccom.ubitc.models.** {*;}

#-keep class * implements com.google.gson.TypeAdapterFactory
#-keep class * implements com.google.gson.JsonSerializer
#-keep class * implements com.google.gson.JsonDeserializer

#-keep interface androidx.** { *; }
#-keep class androidx.core.util.Predicate { *; }
#-keep class androidx.core.app.CoreComponentFactory { *; }
#-keepnames class com.google.firebase.FirebaseApp.** { *; }
#-keepnames class com.google.firebase.FirebaseApp.GlobalBackgroundStateListener.** { *; }
##-keep class androidx.core.** { *; }
#-keep class androidx.core.util.Predicate.** { *; }
#-keep class androidx.appcompat.** { *; }
#-keep class com.google.android.material.**{*;}
#-keep class androidx.appcompat.** { *; }
#-keep class com.google.android.material.**{*;}
#-keepnames class com.shaded.fasterxml.jackson.** { *; }
#-keepnames class org.shaded.apache.** { *; }
#-keepnames class javax.servlet.** { *; }
#-dontwarn org.w3c.dom.**
#-dontwarn org.joda.time.**
#-dontwarn org.shaded.apache.commons.logging.impl.**
#
#-keep class com.crashlytics.** { *; }
#-dontwarn com.crashlytics.**
#
#-assumenosideeffects class android.util.Log {
#    public static *** v(...);
#    public static *** d(...);
#    public static *** i(...);
#    public static *** w(...);
#    public static *** e(...);
#}
#-keepattributes Signature
#-keepattributes *Annotation*
#-keepattributes EnclosingMethod
#-keepattributes InnerClasses

# JSR 305 annotations are for embedding nullability information.
#-dontwarn javax.annotation.**
#
## A resource is loaded with a relative path so the package of this class must be preserved.
#-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
#
## Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
#-dontwarn org.codehaus.mojo.animal_sniffer.*
#
## OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
#-dontwarn okhttp3.internal.platform.**
#-dontwarn org.conscrypt.**
#-dontwarn org.bouncycastle.**
#-dontwarn org.openjsse.**
-dontwarn android.util.**