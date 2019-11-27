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
-keep class android.content.*{public *;}
-keep class android.database.*{public *;}
-keep class android.graphics.*{public *;}
-keep class android.app.*{public *;}
-keep class com.bestom.ei_library.commons.beans.*{public *;}
-keep class com.bestom.ei_library.commons.constant.*{public *;}
-keep class com.bestom.ei_library.commons.exceptions.*{public *;}
-keep class com.bestom.ei_library.commons.listener.*{public *;}
-keep class com.bestom.ei_library.commons.utils.*{public *;}
-keep class com.bestom.ei_library.core.api.*{public *;}
-keep class com.bestom.ei_library.EIFace{public *;}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
