package com.github.andreyasadchy.xtra.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.github.andreyasadchy.xtra.XtraApp
import com.github.andreyasadchy.xtra.util.LifecycleListener

object AppInjector {

    fun init(xtraApp: XtraApp) {
        xtraApp.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity is FragmentActivity) {
                    activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                        object : FragmentManager.FragmentLifecycleCallbacks() {
                            override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
                                if (f is LifecycleListener) {
                                    xtraApp.addLifecycleListener(f)
                                }
                            }

                            override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                                if (f is LifecycleListener) {
                                    xtraApp.removeLifecycleListener(f)
                                }
                            }
                        }, true
                    )
                }
            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {

            }
        })
    }
}
