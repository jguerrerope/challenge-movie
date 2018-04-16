package com.jguerrerope.movies.ui


import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.jguerrerope.movies.R
import org.jetbrains.anko.startActivity
import java.util.concurrent.TimeUnit


/**
 * The {@link SplashActivity} of the app that make as point entry
 */
class SplashActivity : AppCompatActivity() {
    private var delayHandler = Handler()

    /**
     * Runnable used to route app.
     */
    private val runnable: Runnable = Runnable {
        if (!isFinishing) {
            startActivity<MoviesListActivity>()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //execute router runnable with delay
        delayHandler.postDelayed(runnable, SPLASH_DELAY)
    }

    public override fun onDestroy() {
        // remove Runnable router
        delayHandler.removeCallbacks(runnable)
        super.onDestroy()
    }

    companion object {
        private val SPLASH_DELAY = TimeUnit.SECONDS.toMillis(2)
    }
}

