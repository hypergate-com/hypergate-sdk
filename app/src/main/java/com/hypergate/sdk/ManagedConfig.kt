package com.hypergate.sdk

import android.content.Context
import android.content.RestrictionsManager
import android.os.Bundle

class ManagedConfig private constructor() {

    companion object {
        fun readManagedConfig(context:Context): Bundle {
            val restrictionsMgr = context.getSystemService(Context.RESTRICTIONS_SERVICE) as RestrictionsManager
            return restrictionsMgr.applicationRestrictions
        }
    }

}