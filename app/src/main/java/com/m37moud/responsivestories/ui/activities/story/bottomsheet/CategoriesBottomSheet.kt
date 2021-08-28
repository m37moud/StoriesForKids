package com.m37moud.responsivestories.ui.activities.story.bottomsheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.models.CategoriesModel
import com.m37moud.responsivestories.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.m37moud.responsivestories.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.m37moud.responsivestories.viewmodel.VideosViewModel

class CategoriesBottomSheet : BottomSheetDialogFragment() {


    private lateinit var videosViewModel: VideosViewModel


    private var mealTypeChip = DEFAULT_MEAL_TYPE
    private var mealTypeChipId = 0
    private var dietTypeChip = DEFAULT_DIET_TYPE
    private var dietTypeChipId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        videosViewModel = ViewModelProvider(requireActivity()).get(VideosViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mView = inflater.inflate(R.layout.categories_bottom_sheet, container, false)

//        recipesViewModel.readMealAndDietType.asLiveData().observe(viewLifecycleOwner) { value ->
//            Log.d("mah RecipesBottomSheet", "requestApiData sucsess!" + value.toString())
//
//            mealTypeChip = value.selectedMealType
//            dietTypeChip = value.selectedDietType
//            updateChip(value.selectedMealTypeId, mView.mealType_chipGroup)
//            updateChip(value.selectedDietTypeId, mView.dietType_chipGroup)
//        }
//
//        mView.mealType_chipGroup.setOnCheckedChangeListener { group, selectedChipId ->
//            val chip = group.findViewById<Chip>(selectedChipId)
//            val selectedMealType = chip.text.toString().toLowerCase(Locale.ROOT)
//            mealTypeChip = selectedMealType
//            mealTypeChipId = selectedChipId
//        }
//
//        mView.dietType_chipGroup.setOnCheckedChangeListener { group, selectedChipId ->
//            val chip = group.findViewById<Chip>(selectedChipId)
//            val selectedDietType = chip.text.toString().toLowerCase(Locale.ROOT)
//
//            dietTypeChip = selectedDietType
//            dietTypeChipId = selectedChipId
//        }
//
//        mView.apply_btn.setOnClickListener {
//            Log.d("mah RecipesBottomSheet", "setOnClickListener sucsess!" + mealTypeChip.toString() + dietTypeChip.toString())
//            recipesViewModel.saveMealAndDietType(
//                mealTypeChip,
//                mealTypeChipId,
//                dietTypeChip,
//                dietTypeChipId
//            )
//            //do search
//            recipesViewModel.saveSearch(true)
//
//            val action =
//                RecipesBottomSheetDirections.actionRecipesBottomSheetToRecipesFragment(true)
//            findNavController().navigate(action)
//        }

        return mView
    }
    override fun onStart() {
        super.onStart()
        //this forces the sheet to appear at max height even on landscape
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun updateChip(chipId: Int, chipGroup: ChipGroup) {
        if (chipId != 0) {
            try {
                chipGroup.findViewById<Chip>(chipId).isChecked = true
            } catch (e: Exception) {
                Log.d("RecipesBottomSheet", e.message.toString())
            }
        }
    }


    private fun initChip(list: ArrayList<CategoriesModel>, chipGroup: ChipGroup){


        repeat(list.size) {
            val model = list[it]
            val chip = Chip(requireContext())
            chip.text = model.categoryName
            chip.setTextAppearance(R.style.customChipStyle)
            chipGroup.addView(chip)
        }


    }


}