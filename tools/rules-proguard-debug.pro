# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:/Work/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
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

-ignorewarnings

# Remove logs
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose
-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt

-dontwarn android.support.**
-dontwarn android.webkit.**
-dontwarn com.google.common.**
-dontwarn org.apache.lang.**

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-optimizationpasses 5

-allowaccessmodification
-renamesourcefileattribute SourceFile
-repackageclasses ''
-keepattributes Exceptions,*Annotation*,InnerClasses,SourceFile,LineNumberTable,Deprecated,Signature
-keepattributes EnclosingMethod

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

-keepclasseswithmembers class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

 -keepclassmembers public class * extends android.view.View {
  void set*(***);
  *** get*();
 }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
  public static <fields>;
}

#keep json classes
 -keepclassmembernames class * extends com.applozic.mobicommons.json.JsonMarker {
 	!static !transient <fields>;
 }

 -keepclassmembernames class * extends com.applozic.mobicommons.json.JsonParcelableMarker {
 	!static !transient <fields>;
 }
 #GSON Config
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class org.eclipse.paho.client.mqttv3.logging.JSR47Logger { *; }
-keep class android.support.** { *; }
-keep interface android.support.** { *; }
-dontwarn android.support.v4.**
-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**
-keep class com.google.gson.** { *; }