package com.craftrom.manager.ui.dcenter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private var binding: FragmentDcenterBinding? = null
    private lateinit var dCenterAdapter: DCenterAdapter
    private val retrofitClient: RetrofitClient by lazy { RetrofitClient() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDcenterBinding.inflate(inflater, container, false)
        val layoutManager = LinearLayoutManager(requireContext())
        binding?.contentList?.layoutManager = layoutManager

        dCenterAdapter = DCenterAdapter()
        binding?.contentList?.adapter = dCenterAdapter // встановлюємо адаптер
        fetchData()

        return binding?.root
    }

    private fun fetchData() {
        val call: Call<List<ContentUpdateResponse>> = retrofitClient.getService().content(DeviceSystemInfo.deviceCode())
        call.enqueue(object : Callback<List<ContentUpdateResponse>> {
            override fun onFailure(call: Call<List<ContentUpdateResponse>>, t: Throwable) {
                binding?.emptyHelp?.visibility = View.VISIBLE
                binding?.contentList?.visibility = View.GONE
            }

            override fun onResponse(
                call: Call<List<ContentUpdateResponse>>,
                response: Response<List<ContentUpdateResponse>>
            ) {
                response.body()?.let {data ->
                    dCenterAdapter.setData(data) // оновлюємо дані в адаптері
                    binding?.emptyHelp?.visibility = View.GONE
                    binding?.contentList?.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}