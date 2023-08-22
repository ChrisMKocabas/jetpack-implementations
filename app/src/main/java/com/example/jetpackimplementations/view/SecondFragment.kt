package com.example.jetpackimplementations.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.room.Room
import com.example.jetpackimplementations.R
import com.example.jetpackimplementations.databinding.FragmentSecondBinding
import com.example.jetpackimplementations.db.ArtDao
import com.example.jetpackimplementations.db.ArtDatabase
import com.example.jetpackimplementations.model.Art
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null;
    private val binding get() = _binding!!
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var selectedBitmap: Bitmap? = null
    private lateinit var database: SQLiteDatabase
    private lateinit var info:String
    private var id:Int = -999

    private lateinit var artDatabase : ArtDatabase
    private lateinit var artDao : ArtDao
    private val mDisposable = CompositeDisposable()
    private var artFromMain : Art? = null

    private var storagePermissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    var storagePermissions33 = arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_AUDIO,
        Manifest.permission.READ_MEDIA_VIDEO
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Call the registerLauncher function to initialize launchers
        registerLaunchers()

        artDatabase = Room.databaseBuilder(requireContext(), ArtDatabase::class.java, "Arts").build()
        artDao = artDatabase.artDao()

        arguments?.let {
            info = SecondFragmentArgs.fromBundle(it).info
            id = SecondFragmentArgs.fromBundle(it).id
        }

        binding.saveButton.setOnClickListener { save(view) }
        binding.deleteButton.setOnClickListener { delete(view) }
        binding.updateButton.setOnClickListener { update(view) }

        if (id == 0) {
            binding.imageView.setOnClickListener { selectImage(view) }
        }

        adjustUiFields()
    }

    private fun adjustUiFields() {
        if (info == "new") {
            binding.artNameText.setText("")
            binding.artistNameText.setText("")
            binding.artYearText.setText("")
            binding.saveButton.visibility = View.VISIBLE
            binding.deleteButton.visibility = View.GONE
            binding.updateButton.visibility = View.GONE
            val selectedImageBackground = BitmapFactory.decodeResource(
                context?.resources,
                R.drawable.selectimage
            )
            binding.imageView.setImageBitmap(selectedImageBackground)
        } else {
            binding.saveButton.visibility = View.GONE
            binding.deleteButton.visibility = View.VISIBLE
            binding.updateButton.visibility = View.VISIBLE
            // query to select the specific art using passed id
            val selectedId = id
            mDisposable.add(
                artDao.getArtById(selectedId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponseWithOldArt)
            )
        }
    }

    private fun handleResponseWithOldArt(art : Art) {
        artFromMain = art
        binding.artNameText.setText(art.artName)
        binding.artistNameText.setText(art.artistName)
        binding.artYearText.setText(art.year)
        art.image?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            binding.imageView.setImageBitmap(bitmap)
        }
    }


    private fun save(view: View) {

        val artName = binding.artNameText.text.toString()
        val artistName = binding.artistNameText.text.toString()
        val year = binding.artYearText.text.toString()

        if (selectedBitmap != null) {
            val smallBitmap = makeSmallerBitmap(selectedBitmap!!,600)

            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArray = outputStream.toByteArray()

            val art = Art(artName,artistName,year,byteArray)

            mDisposable.add(artDao.insert(art)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse))

        }
    }

    private fun delete(view : View) {
        artFromMain?.let {
            mDisposable.add(artDao.delete(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse))
        }
    }

    private fun update(view: View) {
        artFromMain?.let { it ->
            it.artName = binding.artNameText.text.toString()
            it.artistName = binding.artistNameText.text.toString()
            it.year = binding.artYearText.text.toString()

            Log.d("MyApp", "Updating art: $it") // Add this line

            mDisposable.add(
                artDao.update(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            Log.d("MyApp", "Update successful")
                            handleResponse()
                        },
                        { error ->
                            Log.e("MyApp", "Error updating art: ${error.message}")
                        }
                    )
                    .also {
                        mDisposable.add(it) // Add this line to dispose the disposable after the transaction is completed
                    }
            )
        }
    }

    private fun handleResponse() {
        val action = SecondFragmentDirections.secondFirstFrg()
        Navigation.findNavController(requireView()).navigate(action)

    }


    private fun makeSmallerBitmap(image: Bitmap, maximumSize: Int): Bitmap {

            var width = image.width
            var height = image.height

            val bitmapRatio: Double = width.toDouble() / height.toDouble()

            if (bitmapRatio > 1) {
                //landscape
                width = maximumSize
                val scaledHeight = width / bitmapRatio
                height = scaledHeight.toInt()
            } else {
                //portrait
                height = maximumSize
                val scaledWidth = height * bitmapRatio
                width = scaledWidth.toInt()

            }

            return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private fun selectImage(view: View) {


        Log.d("MyApp", "selectImage() function called") // Add this line

        // request permission below Tiramisu (API 33)
        if (Build.VERSION.SDK_INT < 33 && ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            //show permission rationale
            if ( ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission required to access gallery", Snackbar.LENGTH_INDEFINITE).setAction("Grant Permission", View.OnClickListener {
                    //request permission
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        permissions() ?: arrayOf(),
                        1
                    )
                }).show()

            } else {  //don't show permission rationale
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissions() ?: arrayOf(),
                    1
                )
            }
            // request permission Tiramisu and above  (API 33)
        }else if (Build.VERSION.SDK_INT >= 33 &&
            (ContextCompat.checkSelfPermission(requireContext(),
                     Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)) {

            //show permission rationale
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)) {
                Snackbar.make(view, "Permission required to access gallery", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Grant Permission", View.OnClickListener {
                        // Request permissions
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            permissions() ?: arrayOf(),
                            1
                        )
                    }).show()
            } else {//don't show permission rationale
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissions() ?: arrayOf(),
                    1
                )
            }
            // permissions granted
        } else {
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            //intent
            activityResultLauncher.launch(intentToGallery)
        }
    }

    private fun permissions(): Array<String>? {
        val p: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            storagePermissions33
        } else {
            storagePermissions
        }
        return p
    }

    private fun registerLaunchers() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback() { result ->

                //make sure result of permission is ok
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    //save nullable result to constant
                    val intentFromResult = result.data
                    //check  to make sure intent is not null
                    if (intentFromResult != null) {
                        // if data is not null, save the data as image to process
                        val imageData = intentFromResult.data
                        //convert image to bitmap
                        if (imageData != null) {
                            try {
                                if (Build.VERSION.SDK_INT >= 28) {
                                    val source = ImageDecoder.createSource(
                                        requireActivity().contentResolver,
                                        imageData
                                    )
                                    selectedBitmap = ImageDecoder.decodeBitmap(source)
                                    binding.imageView.setImageBitmap(selectedBitmap)
                                } else {
                                    selectedBitmap = MediaStore.Images.Media.getBitmap(
                                        requireActivity().contentResolver,
                                        imageData
                                    )
                                    binding.imageView.setImageBitmap(selectedBitmap)
                                }
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        )

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {result ->
            if (result) {
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            } else {
                Toast.makeText(requireContext(), "Permission needed", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear()
    }
}