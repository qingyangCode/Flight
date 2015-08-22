# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/ZEPP/Library/Android/sdk/tools/proguard/proguard-android.txt
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
-dontpreverify
-dontoptimize
-dontusemixedcaseclassnames
-repackageclasses
-allowaccessmodification
-optimizationpasses 10
-dontskipnonpubliclibraryclasses
-verbose
#-printmapping proguard.map
-overloadaggressively
-defaultpackage ''

-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes EnclosingMethod

-keep class * extends java.lang.annotation.Annotation { *; }

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference



-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class com.google.gson.** { *; }

-keep class com.uandme.flight.data.dao.** { *; }


-keep class com.uandme.flight.entity.** { *; }



-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keepattributes *Annotation*,EnclosingMethod,Signature



-dontwarn okio.**
-keep class okio.** { *; }

-dontwarn com.google.common.**
-keep class com.google.common.** { *; }

-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *;}

-dontwarn android.support.**
-keep class android.support.** { *; }
-keep interface android.support.** { *; }


-keep class com.zepp.soccer.djinni.** {*;}

-keep class **.R$* {
    <fields>;
    }

-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    *;
}
