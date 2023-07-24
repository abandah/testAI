package com.ubitc.popuppush.ui.test_activity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.ubitc.popuppush.databinding.ActivityTestBinding
import com.ubitc.popuppush.ui.BaseActivity



class TestActivity : BaseActivity() {

    private var binding: ActivityTestBinding? = null
    var viewModel: TestActivityViewModel? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[TestActivityViewModel::class.java]
        binding!!.viewModel = viewModel
        binding!!.lifecycleOwner = this
        setContentView(binding!!.root)



    }

}