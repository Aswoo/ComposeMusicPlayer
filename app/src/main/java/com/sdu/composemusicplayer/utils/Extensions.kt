package com.sdu.composemusicplayer.utils


import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@OptIn(ExperimentalMaterialApi::class)
val BottomSheetScaffoldState.currentfraction: Float
    @Composable
    get() {
        val fraction = bottomSheetState.progress
        val currentValue = bottomSheetState.currentValue

        return when {
            currentValue == BottomSheetValue.Collapsed -> 0f
            currentValue == BottomSheetValue.Expanded -> 1f
            else -> 1f - fraction
        }
    }