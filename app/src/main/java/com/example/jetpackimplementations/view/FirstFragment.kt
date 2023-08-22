package com.example.jetpackimplementations.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.jetpackimplementations.adapter.ArtRecyclerAdapter
import com.example.jetpackimplementations.databinding.FragmentFirstBinding
import com.example.jetpackimplementations.db.ArtDao
import com.example.jetpackimplementations.db.ArtDatabase
import com.example.jetpackimplementations.model.Art
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null;
    private val binding get() = _binding!!

    private val mDisposable = CompositeDisposable()
    private lateinit var artDao : ArtDao
    private lateinit var artDatabase : ArtDatabase

    //define the recycler view adapter
    private lateinit var artAdapter: ArtRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        val view = binding.root

        val linearLayoutManager = LinearLayoutManager(this.requireContext())
        linearLayoutManager.orientation =
            LinearLayoutManager.VERTICAL
        binding.artRecyclerView.layoutManager = linearLayoutManager
        artAdapter = ArtRecyclerAdapter(ArrayList<Art>())
        return view


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        artDatabase = Room.databaseBuilder(requireContext(), ArtDatabase::class.java, "Arts").build()
        artDao = artDatabase.artDao()

        getFromSQL()


//      ADD MENU TO FRAGMENT
//
//        addMenuProvider(R.menu.art_menu) {
//            info="new"
//
//            val action = FirstFragmentDirections.firstSecondFrg(info,id)
//            when (it) {
//                R.id.add_art_item -> {
//                    Navigation.findNavController(this.requireView()).navigate(action)
//                    true
//                }
//                else -> false
//            }
//        }

    }

    private fun getFromSQL() {
        mDisposable.add(artDao.getArtWithNameAndId()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse))
    }

    private fun handleResponse(artList: List<Art>) {

        artAdapter = ArtRecyclerAdapter(artList as ArrayList<Art>)
        binding.artRecyclerView.adapter = artAdapter
    }

    //ADD MENU TO FRAGMENT

//    private fun Fragment.addMenuProvider(@MenuRes menuRes: Int, callback: (id: Int) -> Boolean) {
//        val menuProvider = object : MenuProvider {
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                menuInflater.inflate(menuRes, menu)
//            }
//
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean = callback(menuItem.itemId)
//
//        }
//        (requireActivity() as MenuHost).addMenuProvider(
//            menuProvider,
//            viewLifecycleOwner,
//            Lifecycle.State.RESUMED
//        )
//    }

        override fun onDestroyView() {
        super.onDestroyView()
            _binding = null
            mDisposable.clear()
        }


}