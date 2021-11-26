/**
 * Copyright (C) 2020 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fernandocejas.sample.features.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import com.fernandocejas.sample.R
import com.fernandocejas.sample.core.extension.inTransaction
import com.fernandocejas.sample.core.platform.BaseActivity
import com.fernandocejas.sample.core.platform.BaseFragment

class LoginActivity : BaseActivity() {
    companion object {
        fun callingIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }

    override fun fragment() = LoginFragment()

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        (supportFragmentManager.findFragmentById(R.id.fragmentContainer) as BaseFragment).onTouchEvent(event);
        return true
    }
}
