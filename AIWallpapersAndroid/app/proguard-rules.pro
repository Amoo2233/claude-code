# Keep kotlinx.serialization models
-keepclassmembers class com.aiwallpapers.app.model.** {
    *** Companion;
}
-keepclasseswithmembers class com.aiwallpapers.app.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}
