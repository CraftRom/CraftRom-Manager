package com.craftrom.manager.ui.dcenter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.craftrom.manager.databinding.FragmentDcenterBinding
import com.craftrom.manager.ui.dcenter.adapter.DCenterAdapter
import com.craftrom.manager.utils.DeviceSystemInfo
import com.craftrom.manager.utils.response.ContentUpdateResponse
import com.craftrom.manager.utils.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DCenterFragment : Fragment() {
    private var _binding: FragmentDcenterBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDcenterBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val layoutManager = LinearLayoutManager(context)
        binding.contentList.layoutManager = layoutManager

            RetrofitClient().getService()
                .content(DeviceSystemInfo.deviceCode())
                .enqueue(object : Callback<List<ContentUpdateResponse>> {
                    override fun onFailure(call: Call<List<ContentUpdateResponse>>, t: Throwable) {
                        Toast.makeText(context, t.localizedMessage, Toast.LENGTH_SHORT).show()
                        binding.contentList.visibility =View.GONE
                        binding.emptyHelp.visibility = View.VISIBLE
                    }

                    override fun onResponse(
                        call: Call<List<ContentUpdateResponse>>,
                        response: Response<List<ContentUpdateResponse>>
                    ) {
                        binding.emptyHelp.visibility = View.GONE
                        binding.contentList.visibility =View.VISIBLE
                        binding.contentList.adapter = DCenterAdapter(response.body())
                    }

                })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}