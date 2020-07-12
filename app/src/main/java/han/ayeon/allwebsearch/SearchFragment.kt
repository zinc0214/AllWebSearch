package han.ayeon.allwebsearch

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.Observer
import han.ayeon.allwebsearch.databinding.FragmentSearchBinding
import han.ayeon.allwebsearch.model.SearchResult
import han.ayeon.allwebsearch.model.Site
import han.ayeon.allwebsearch.viewmodel.SearchViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        viewModel = SearchViewModel()

        viewModel.search(Site.ALL, "android", 1)
        viewModel.googleResult.observe(this, googleResultObserver)
        viewModel.naverResult.observe(this, naverResultObserver)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* view.findViewById<Button>(R.id.button_first).setOnClickListener {
             findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
         }*/
    }

    private val googleResultObserver = Observer<List<SearchResult>> {
        Log.e("ayhan", "googleResult : ${it.size}")
        for (i in it) {
            Log.e("ayhan", "google: ${i.title}")
        }
    }

    private val naverResultObserver = Observer<List<SearchResult>> {
        Log.e("ayhan", "naverResult : ${it.size}")
        for (i in it) {
            Log.e("ayhan", "naver: ${i.title}")
        }
    }

}