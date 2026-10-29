package com.meditrack.app

import android.app.Application
import com.meditrack.app.data.MediTrackDatabase

/**
 * Application class for MediTrack.
 * Provides a singleton instance of the Room database.
 */
class MediTrackApp : Application() {
    val database: MediTrackDatabase by lazy { MediTrackDatabase.getInstance(this) }
}
