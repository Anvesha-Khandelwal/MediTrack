# MediTrack ProGuard rules
# Keep Room generated classes
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Kotlin metadata
-keep class kotlin.Metadata { *; }
