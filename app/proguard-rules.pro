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

-optimizationpasses 5
-dontpreverify
-dontoptimize
-verbose
-keepattributes *Annotation*
-ignorewarning
-keepclasseswithmembernames class * {
     native <methods>;
}
-keepattributes *Annotation*
-keep enum com.ainemo.sdk.NemoSDKListener** {
    **[] $VALUES;
    public *;
}
-keepclassmembers enum * { *; }
-keepnames class * implements java.io.Serializable
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class com.ainemo.sdk.model.Settings{*;}
-keep class com.ainemo.sdk.NemoSDK{
  public *;
}
-keep class com.ainemo.**{*;}
-keep class com.llvision.**{*;}
-keep class android.utils.**{*;}
-keep class android.util.**{*;}
-keep class android.log.**{*;}
-keep class android.http.**{*;}
-keep class vulture.module.**{*;}
-keep class vulture.home.call.media.omap.**{*;}

-keep class com.google.gson.stream.** {*;}
-keep class com.google.gson.** {*;}
-keep class com.google.gson.Gson {*;}
-keep class com.google.gson.examples.android.model.** {*;}
-keep class rx.internal.util.**{*;}