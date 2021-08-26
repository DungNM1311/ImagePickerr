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
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-optimizationpasses 5
-overloadaggressively
-repackageclasses ''
-allowaccessmodification

#-keep class com.vnpay.ekyc.data.entities.response.** { *; }

-keepclassmembernames class * {
    native <methods>;
}

#-keepclassmembers enum * { *; }

#-optimizations !library/gson

-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes Annotation
#
# Gson specific classes
-dontwarn sun.misc.**
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
#-keepnames class com.vnpay.ekyc.ui** {  }
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Dialog
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends android.view.View

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int wtf(...);
    public static int d(...);
    public static int e(...);
    public static int wtf(...);
    public static int println(...);
    public static java.lang.String getStackTraceString(java.lang.Throwable);

}

-assumenosideeffects class java.lang.Throwable {
    public void printStackTrace();
}

-assumenosideeffects class java.lang.Exception {
    public void printStackTrace();
}

-assumenosideeffects class java.io.PrintStream {
     public void println(%);
     public void println(**);
}