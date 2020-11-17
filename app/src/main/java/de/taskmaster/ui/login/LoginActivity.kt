package de.taskmaster.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.*
import de.taskmaster.R
import de.taskmaster.auth.AuthManager
import de.taskmaster.auth.LocalAuthHelper
import de.taskmaster.auth.UserInformation
import de.taskmaster.ui.app.AppActivity


class LoginActivity : AppCompatActivity() {

    private var indicatorWidth: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if (AuthManager.login(
                LocalAuthHelper.getLoginInformation(
                    applicationContext
                )
            )
        ) {
            startMainActivity()
        }
        setupUI()
    }

    private fun startMainActivity() {
        startActivity(Intent(this, AppActivity::class.java))
        finish()
    }


    private fun setupUI() {
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        val indicator = findViewById<View>(R.id.tab_indicator)

        tabLayout.tabGravity = GRAVITY_FILL
        val login = tabLayout.newTab()
        login.text = "Login"
        tabLayout.addTab(login)
        val register = tabLayout.newTab()
        register.text = "Register"
        tabLayout.addTab(register)

        viewPager.adapter = TabAdapter(2, this, supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
        viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: Tab) {}
            override fun onTabReselected(tab: Tab) {}
        })

        tabLayout.post {
            indicatorWidth = tabLayout.width / tabLayout.tabCount
            val indicatorParams: FrameLayout.LayoutParams =
                indicator.layoutParams as FrameLayout.LayoutParams
            indicatorParams.width = indicatorWidth
            indicator.layoutParams = indicatorParams
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                val params = indicator.layoutParams as FrameLayout.LayoutParams
                val translationOffset: Float = (positionOffset + position) * indicatorWidth
                params.leftMargin = translationOffset.toInt()
                indicator.layoutParams = params
            }

            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}

        })
    }

    fun tryLogin(view: View) {
        val username = findViewById<TextView>(R.id.username_email).text.toString()
        val password = findViewById<TextView>(R.id.password).text.toString()

        if (AuthManager.login(
                UserInformation(
                    username,
                    password
                )
            )
        ) {
            if (findViewById<CheckBox>(R.id.remember_password).isChecked) {
                LocalAuthHelper.saveLoginInformation(applicationContext, username, password)
            }
            startMainActivity()
        } else {
            //TODO: show some error on UI
        }
    }

}