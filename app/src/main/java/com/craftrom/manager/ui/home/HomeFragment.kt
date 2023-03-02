    package com.craftrom.manager.ui.home

    import android.annotation.SuppressLint
    import android.content.SharedPreferences
    import android.os.Build
    import android.os.Bundle
    import android.util.Log
    import android.view.*
    import android.widget.LinearLayout
    import android.widget.TextView
    import android.widget.Toast
    import androidx.fragment.app.Fragment
    import androidx.navigation.fragment.findNavController
    import androidx.preference.PreferenceManager
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.craftrom.manager.R
    import com.craftrom.manager.core.ServiceContext
    import com.craftrom.manager.databinding.FragmentHomeBinding
    import com.craftrom.manager.events.RebootEvent
    import com.craftrom.manager.utils.Const.KALI_NAME
    import com.craftrom.manager.utils.Const.KERNEL_NAME
    import com.craftrom.manager.utils.Const.PREF_KEY_DEV_OPTIONS
    import com.craftrom.manager.utils.Const.PREF_KEY_ROOT_ENABLE
    import com.craftrom.manager.utils.Const.RSS_FEED_LINK
    import com.craftrom.manager.utils.Const.TAG
    import com.craftrom.manager.utils.DeviceSystemInfo
    import com.craftrom.manager.utils.app.NewsUtil
    import com.craftrom.manager.utils.rss.RssFeed
    import com.craftrom.manager.utils.rss.RssItem
    import com.craftrom.manager.utils.service.RetrofitInstance.setupRetrofitCall
    import org.koin.android.ext.android.inject
    import retrofit2.Call
    import retrofit2.Callback
    import retrofit2.Response


    open class HomeFragment : Fragment(){

        companion object {
            var LIST_LIMIT: Int = 5
        }

        private lateinit var adapterRecyclerViewRssContent: AdapterRecyclerViewNews
        private val arrayListContent = ArrayList<RecyclerViewNewsItem>()
        private val newsUtil: NewsUtil by inject()
        private var _binding: FragmentHomeBinding? = null

        // This property is only valid between onCreateView and
        // onDestroyView.
        private val binding get() = _binding!!

        override fun onStart() {
            super.onStart()
            setHasOptionsMenu(true)
        }

        @SuppressLint("SetTextI18n")
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = FragmentHomeBinding.inflate(inflater, container, false)
            val root: View = binding.root
            val exodusosLayout:LinearLayout = binding.homeDeviceWrapper.homeExodusos
            val device: TextView = binding.homeDeviceWrapper.homeDeviceTitle
            val android_version:TextView = binding.homeDeviceWrapper.homeAndroidInfo
            val exodusos:TextView = binding.homeDeviceWrapper.homeExodusosInfo
            val codename:TextView = binding.homeDeviceWrapper.homeDeviceCodenameInfo
            val platform:TextView = binding.homeDeviceWrapper.homeDevicePlatformInfo
            val kernel:TextView = binding.homeDeviceWrapper.homeDeviceKernelInfo
            val chidori:TextView = binding.homeDeviceWrapper.homeDeviceChidoriInfo
            val security_patch: TextView = binding.homeDeviceWrapper.homeSecurityInfo


            // показываем ProgressBar
            binding.progressBar.visibility = View.VISIBLE
            binding.emptyHelp.visibility = View.GONE
            binding.pageRecyclerview.visibility = View.GONE

            device.text = "${DeviceSystemInfo.brand()} ${DeviceSystemInfo.model()}"
            android_version.text = "${DeviceSystemInfo.releaseVersion()} (API ${DeviceSystemInfo.apiLevel()})"
            if (DeviceSystemInfo.exodusVersion().isNotEmpty())
            {
                exodusosLayout.visibility= View.VISIBLE
                exodusos.text = "${DeviceSystemInfo.exodusVersion()} (${DeviceSystemInfo.exodusMaintainer()})"
            } else {
                exodusosLayout.visibility= View.INVISIBLE
            }
            security_patch.text = Build.VERSION.SECURITY_PATCH
            codename.text = DeviceSystemInfo.device()
            platform.text = "${DeviceSystemInfo.board()} (${DeviceSystemInfo.hardware()}-${DeviceSystemInfo.arch()})"
            kernel.text = "${DeviceSystemInfo.osName()} ${DeviceSystemInfo.kernelVersion()}"
            if (DeviceSystemInfo.chidoriName() == KERNEL_NAME  || DeviceSystemInfo.tsukuyoumiName() == KALI_NAME)
            {
                chidori.text = resources.getString(R.string.yes) + " (${DeviceSystemInfo.deviceCode()})"
            } else {
                chidori.text = resources.getString(R.string.no)
            }

            setupContentRecyclerView()

            fillArrayListRecyclerViewContent()
            newsUtil.setupListCount()
            return root
        }

        private fun setupContentRecyclerView() {
            val layoutManager = LinearLayoutManager(context)
            binding.pageRecyclerview.layoutManager = layoutManager

            adapterRecyclerViewRssContent = AdapterRecyclerViewNews(arrayListContent)

            binding.pageRecyclerview.adapter = adapterRecyclerViewRssContent
        }

        private fun fillArrayListRecyclerViewContent (){


            val call : Call<RssFeed> = setupRetrofitCall(
                RSS_FEED_LINK
                ,"feed.xml")

            call.enqueue(object : Callback<RssFeed> {

                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<RssFeed>, response: Response<RssFeed>) {

                    Log.d("RssFeed", "Success")

                    val listItems : List<RssItem>? = response.body()?.items

                    listItems?.let {
                        for (item in listItems){

                            arrayListContent.add(
                                RecyclerViewNewsItem(
                                    description = item.description,
                                    image = item.image,
                                    author = item.author,
                                    link = item.link,
                                    publishDate = item.publishDate,
                                    title = item.title
                                )
                            )
                        }

                        adapterRecyclerViewRssContent.notifyDataSetChanged()
                        binding.progressBar.visibility = View.GONE
                        binding.emptyHelp.visibility = View.GONE
                        binding.pageRecyclerview.visibility = View.VISIBLE
    //              headerPage.text = "(${arrayListContent.size})"

                    }
                }

                override fun onFailure(call: Call<RssFeed>, t: Throwable) {
                    Log.d("RssFeed", t.message!!)
                    binding.pageRecyclerview.visibility = View.GONE
                    binding.emptyHelp.visibility = View.VISIBLE
                    Toast.makeText(context, "No Feeds!", Toast.LENGTH_LONG).show()
                }
            })
        }



        override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
            inflater.inflate(R.menu.menu_home_md2, menu)

            // Add a listener for changes to the "devOptions" and "PREF_KEY_ROOT_ENABLE" preferences
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ServiceContext.context)
            val devOptions = sharedPreferences.getBoolean(PREF_KEY_DEV_OPTIONS, false)
            val rootEnabled = sharedPreferences.getBoolean(PREF_KEY_ROOT_ENABLE, false)

            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                if (key == PREF_KEY_DEV_OPTIONS || key == PREF_KEY_ROOT_ENABLE) {
                    val isVisible = sharedPreferences.getBoolean(PREF_KEY_DEV_OPTIONS, false) && sharedPreferences.getBoolean(PREF_KEY_ROOT_ENABLE, false)
                    menu.findItem(R.id.action_reboot)?.isVisible = isVisible
                }
            }

            sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
            menu.findItem(R.id.action_reboot)?.isVisible = devOptions && rootEnabled
        }


        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_reboot -> {
                    activity?.let { RebootEvent.inflateMenu(it).show() }
                    true
                }
                R.id.action_settings -> {
                    Log.i(TAG,"R.id.action_settings")
                    findNavController().navigate(
                        R.id.nav_settings, null
                    )
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }


        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
            if (arrayListContent.isNotEmpty() or arrayListContent.isEmpty()){
                arrayListContent.clear()
            }
        }
    }