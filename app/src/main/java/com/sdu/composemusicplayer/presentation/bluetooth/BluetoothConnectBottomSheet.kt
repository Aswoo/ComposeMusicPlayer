package com.sdu.composemusicplayer.presentation.bluetooth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdu.composemusicplayer.core.audio.BluetoothUtil
import com.sdu.composemusicplayer.ui.theme.SpotiGreen
import com.sdu.composemusicplayer.ui.theme.SpotiLightGray

/**
 * Spotify 스타일의 블루투스 연결 바텀시트
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothConnectBottomSheet(
    onDismiss: () -> Unit,
    onDeviceSelected: (BluetoothDevice) -> Unit,
    onBluetoothSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BluetoothConnectContent(
        onDismiss = onDismiss,
        onDeviceSelected = onDeviceSelected,
        onBluetoothSettingsClick = onBluetoothSettingsClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun BluetoothConnectContent(
    onDismiss: () -> Unit,
    onDeviceSelected: (BluetoothDevice) -> Unit,
    onBluetoothSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var bluetoothDeviceName by remember { mutableStateOf<String?>(null) }
    
    // 실제 블루투스 연결 상태 확인
    LaunchedEffect(Unit) {
        try {
            val connectedDevice = BluetoothUtil.getBluetoothConnectedDeviceAsync(context)
            bluetoothDeviceName = connectedDevice
        } catch (e: Exception) {
            bluetoothDeviceName = null
        }
    }

    // 제목 (핸들바는 ModalBottomSheet가 자동 제공)
    Text(
        text = "Connect",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 20.dp)
    )

    // 기기 목록 (실제 블루투스 상태 기반)
    val devices = remember(bluetoothDeviceName) {
        val connectedDevice = bluetoothDeviceName
        val isBluetoothConnected = connectedDevice != null && 
                                   connectedDevice != "현재 휴대 전화" && 
                                   connectedDevice != "Disconnected" &&
                                   connectedDevice != "No Bluetooth Adapter"
        
        if (isBluetoothConnected) {
            // 블루투스 기기가 연결된 경우: 연결된 기기 + 휴대폰
            listOf(
                BluetoothDevice(
                    name = connectedDevice,
                    type = DeviceType.EARPHONES,
                    isConnected = true
                ),
                BluetoothDevice(
                    name = "이 휴대 전화",
                    type = DeviceType.PHONE,
                    isConnected = false
                )
            )
        } else {
            // 블루투스 기기가 연결되지 않은 경우: 휴대폰만
            listOf(
                BluetoothDevice(
                    name = "이 휴대 전화",
                    type = DeviceType.PHONE,
                    isConnected = true
                )
            )
        }
    }

    devices.forEach { device ->
        BluetoothDeviceItem(
            device = device,
            onDeviceClick = { onDeviceSelected(device) },
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Bluetooth 설정 버튼
    TextButton(
        onClick = onBluetoothSettingsClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Filled.Bluetooth,
            contentDescription = "Bluetooth 설정",
            tint = SpotiGreen,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Bluetooth",
            color = SpotiGreen,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }

    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun BluetoothDeviceItem(
    device: BluetoothDevice,
    onDeviceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onDeviceClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (device.isConnected) Color(0xFF2A2A2A) else Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (device.type) {
                    DeviceType.EARPHONES -> Icons.Filled.Headset
                    DeviceType.PHONE -> Icons.Filled.PhoneAndroid
                },
                contentDescription = device.name,
                tint = if (device.isConnected) SpotiGreen else SpotiLightGray,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = device.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (device.isConnected) SpotiGreen else Color.White
                )

                if (device.isConnected) {
                    Text(
                        text = "이 휴대폰",
                        style = MaterialTheme.typography.bodySmall,
                        color = SpotiLightGray
                    )
                }
            }

            // 연결 상태 표시
            if (device.isConnected) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = SpotiGreen,
                            shape = RoundedCornerShape(6.dp)
                        )
                )
            }
        }
    }
}