package com.m37moud.responsivestories.ui.activities.story.bottomsheet

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.data.database.entity.CategoriesEntity
import com.m37moud.responsivestories.models.CategoriesModel
import com.m37moud.responsivestories.util.Constants
import com.m37moud.responsivestories.util.Constants.Companion.DEFAULT_CATEGORY_TYPE
import com.m37moud.responsivestories.viewmodel.MainViewModel
import com.m37moud.responsivestories.viewmodel.VideosViewModel
import kotlinx.android.synthetic.main.categories_bottom_sheet.*
import kotlinx.android.synthetic.main.categories_bottom_sheet.view.*
import kotlinx.coroutines.launch
import java.util.*

class CategoriesBottomSheet : BottomSheetDialogFragment() {


    private lateinit var videosViewModel: VideosViewModel
    private lateinit var mainViewModel: MainViewModel

    private var categoryChip = DEFAULT_CATEGORY_TYPE
    private var categoryChipId = 0
    private lateinit var listCategoriesReadDatabase: ArrayList<CategoriesEntity>
//    private lateinit var listCategory: ArrayList<CategoriesModel>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        listCategory = ArrayList()

        videosViewModel = ViewModelProvider(requireActivity()).get(VideosViewModel::class.java)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        readCategoriesFromVideos()

        val mView = inflater.inflate(R.layout.categories_bottom_sheet, container, false)

        val data =
            arguments?.getParcelableArrayList<CategoriesModel>("myListCategory") as ArrayList<CategoriesModel>
        Log.d("CategoriesBottomSheet", "initChip:$data  size: ${data.size}"  )

        initChip(data, mView.categories_chipGroub)

//        videosViewModel.readCategoriesType.asLiveData().observe(viewLifecycleOwner) { value ->
//            Log.d("mah RecipesBottomSheet", "requestApiData success!" + value.toString())
//
//            categoryChip = value.selectedCategoryType
//            updateChip(value.selectedCategoryTypeId, mView.categories_chipGroub)
//        }
//
        mView.categories_chipGroub.setOnCheckedChangeListener { group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId)
            val selectedCategoryType = chip.text.toString().toLowerCase(Locale.ROOT)
            categoryChip = selectedCategoryType

            if (selectedCategoryType == "all" || selectedCategoryType== "الكل") {
                categoryChip = DEFAULT_CATEGORY_TYPE
            }

            categoryChipId = selectedChipId

            Log.d(
                "mah RecipesBottomSheet",
                "setOnClickListener sucsess!" + categoryChip.toString() + categoryChipId.toString()
            )
        }
//
        mView.apply_btn.setOnClickListener {
            Constants.clickSound(requireContext())

            Log.d(
                "mah RecipesBottomSheet",
                "setOnClickListener sucsess!" + categoryChip.toString() + categoryChipId.toString()
            )

            videosViewModel.saveCategoryType(
                categoryChip,
                categoryChipId

            )

            if(categoryChip == ""){
                videosViewModel.saveExitStatus(false)
            }else {


                videosViewModel.saveExitStatus(true)
            }


//            arguments?.putString("chipCategory" , categoryChip)
//            mainViewModel.readVideosWithCategory(categoryChip)


            this.dismiss()
//            //do search
//            recipesViewModel.saveSearch(true)
//
//            val action =
//                RecipesBottomSheetDirections.actionRecipesBottomSheetToRecipesFragment(true)
//            findNavController().navigate(action)
        }

        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videosViewModel.readCategoriesType.asLiveData().observe(viewLifecycleOwner) { value ->
            Log.d("mah RecipesBottomSheet", "requestApiData success!" + value.toString())

            categoryChip = value.selectedCategoryType
            updateChip(value.selectedCategoryTypeId, view.categories_chipGroub)
        }


    }

    override fun onStart() {
        super.onStart()
        //this forces the sheet to appear at max height even on landscape
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun updateChip(chipId: Int, chipGroup: ChipGroup) {
        Log.d("updateChip", "updateChip: called")

        if (chipId != 0) {
            try {
                chipGroup.findViewById<Chip>(chipId).isChecked = true
            } catch (e: Exception) {
                Log.d("updateChip", e.message.toString())
            }
        }
    }



    private fun initChip(list: ArrayList<CategoriesModel>?, chipGroup: ChipGroup) {

        Log.d("initChip", "initChip: called list size : ${list?.size}")

        if (list is ArrayList<CategoriesModel>) {
            if (list.isNotEmpty()) {
                Log.d("initChip", "initChip: true")

                repeat(list.size) {
                    val model = list[it]
                    val chip = Chip(requireContext())
                    Log.d("initChip", "initChip: ${model.categoryName}")
                    chip.isClickable = true
                    chip.isCheckable = true
                    chip.id = model.categoryId!!.toInt()

                    chip.text = model.categoryName
                    val drawable = ChipDrawable.createFromAttributes(
                        requireContext(),
                        null,
                        0,
                        R.style.CustomChipStyle
                    )

//                    chip.setOnCheckedChangeListener { compoundButton, b ->
//                        chip.setTextColor(resources.getColor(R.color.white))
//
//                    }
////                        chip.setOnClickListener {
////                            chip.setTextColor(resources.getColor(R.color.white))
////
////                        }

                    chip.setChipDrawable(drawable)

//                    chip.setOnCheckedChangeListener { buttonView, isChecked ->
//                        chip.setTextColor(resources.getColor(R.color.white))
//                        Log.i("checkedChipIds","${buttonView.id} $isChecked")
//                    }
//                    chip.setTextAppearance(R.style.CustomChipStyle)


                    chipGroup.addView(chip)

                }
            } else {
                Log.d("initChip", "initChip: false")

            }
        }


    }
//    private fun readCategoriesFromVideos() {
//        Log.d("readCategoriesVideos", " called!")
//        lifecycleScope.launch {
//            mainViewModel.readCategoriesFromVideos.observe(this@CategoriesBottomSheet, Observer { database ->
//                if (database.isNotEmpty()) {
//
//                    Log.d("readCategoriesVideos", "if statement true")
//
////                    listCategory = database as ArrayList<CategoriesModel>
//                    listCategoriesReadDatabase = database as java.util.ArrayList
//                    listCategoriesReadDatabase.forEach {
//                        val categoryModel =
//                            CategoriesModel(it.categoryId, it.categoryName, it.categoryImage)
////                        listCategory.add(categoryModel)
//
//                    }
//
//
//                    Log.d("readCategoriesVideos", "list is " + listCategory)
//
//                } else {
//
//                    Log.d("readCategoriesVideos", "if statement is false ...")
////                    Log.d("mah readDatabase", "if statement is false ...listVid = " + listVid.toString())
//                    mainViewModel.readCategoriesFromVideos.removeObservers(this@CategoriesBottomSheet)
//                }
//            })
//        }
//    }


//    private fun readCategoriesFromVideos() {
//        Log.d("readCategoriesVideos", " called!")
//        lifecycleScope.launch {
//            mainViewModel.readCategoriesFromVideos.observe(requireActivity(), Observer { database ->
//                if (database.isNotEmpty()) {
//
//                    Log.d("readCategoriesVideos", "if statement true")
//
////                    listCategory = database as ArrayList<CategoriesModel>
//                    listCategoriesReadDatabase = database as ArrayList
//                    listCategoriesReadDatabase.forEach {
//                        val categoryModel =
//                            CategoriesModel(it.categoryId, it.categoryName, it.categoryImage)
//                        listCategory.add(categoryModel)
//
//                    }
//
//
//                    Log.d("readCategoriesVideos", "list is " + listCategory)
//
//                } else {
//                    Log.d("readCategoriesVideos", "if statement is false ...")
////                    Log.d("mah readDatabase", "if statement is false ...listVid = " + listVid.toString())
//                    mainViewModel.readCategories.removeObservers(requireActivity())
//                }
//            })
//        }
//    }

//
//    override fun onDismiss(dialog: DialogInterface) {
//        super.onDismiss(dialog)
//        Constants.fabCloseSound(requireContext())
//        videosViewModel.saveCategoryType(
//            "",
//            0
//
//        )
//        arguments?.putString("chipCategory", categoryChip)
//    }


}