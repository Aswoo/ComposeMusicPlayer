@file:OptIn(ExperimentalMaterialApi::class)

package com.sdu.composemusicplayer.utils

import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
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

        return when (currentValue) {
            BottomSheetValue.Collapsed -> 0f
            BottomSheetValue.Expanded -> 1f
            else -> 1f - fraction
        }
    }

@OptIn(ExperimentalMaterialApi::class)
val BottomSheetScaffoldState.currentfraction3: Float
    @Composable
    get() {
        val fraction by remember {
            derivedStateOf {
                val progress = bottomSheetState.progress
                when {
                    progress < 0f -> 0f
                    progress > 1f -> 1f
                    else -> progress
                }
            }
        }
        return 1f - fraction
    }
