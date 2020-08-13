-ignorewarnings
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}

"R.string.hms*",
"R.string.connect_server_fail_prompt_toast",
"R.string.getting_message_fail_prompt_toast",
"R.string.no_available_network_prompt_toast",
"R.string.third_app_*",
"R.string.upsdk_*",
"R.layout.hms*",
"R.layout.upsdk_*",
"R.drawable.upsdk*",
"R.color.upsdk*",
"R.dimen.upsdk*",
"R.style.upsdk*",
"R.string.agc*"


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