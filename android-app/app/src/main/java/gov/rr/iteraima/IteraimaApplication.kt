package gov.rr.iteraima

import android.app.Application
import android.content.Context

class IteraimaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private lateinit var instance: IteraimaApplication
        
        fun getAppContext(): Context = instance.applicationContext
    }
}
