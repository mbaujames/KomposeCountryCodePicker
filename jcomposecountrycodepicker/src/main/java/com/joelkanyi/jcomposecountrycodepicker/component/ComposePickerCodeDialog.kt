package com.joelkanyi.jcomposecountrycodepicker.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.joelkanyi.jcomposecountrycodepicker.data.CountryData
import com.joelkanyi.jcomposecountrycodepicker.data.utils.getCountryName
import com.joelkanyi.jcomposecountrycodepicker.data.utils.getFlags
import com.joelkanyi.jcomposecountrycodepicker.data.utils.allCountries

@Composable
fun ComposePickerCodeDialog(
    modifier: Modifier = Modifier,
    padding: Dp = 8.dp,
    limitedCountries: List<String>,
    defaultSelectedCountry: CountryData = allCountries.first(),
    showCountryCode: Boolean = true,
    pickedCountry: (CountryData) -> Unit = {},
    showFlag: Boolean = true,
    showCountryName: Boolean = false,
) {
    val countryList: List<CountryData> = if (limitedCountries.isEmpty()) {
        allCountries
    } else {
        allCountries.filter {
            limitedCountries.contains(it.countryCode) ||
                limitedCountries.contains(it.cCountryPhoneNoCode) ||
                limitedCountries.contains(it.cCountryName)
        }
    }
    var isPickCountry by remember {
        mutableStateOf(defaultSelectedCountry)
    }
    var isOpenDialog by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .padding(padding)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) {
                isOpenDialog = true
            },
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showFlag) {
                Image(
                    modifier = modifier
                        .width(28.dp)
                        .height(18.dp),
                    painter = painterResource(
                        id = getFlags(
                            isPickCountry.countryCode,
                        ),
                    ),
                    contentDescription = null,
                )
            }
            if (showCountryCode) {
                Text(
                    text = isPickCountry.cCountryPhoneNoCode,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
            }
            if (showCountryName) {
                Text(
                    text = stringResource(id = getCountryName(isPickCountry.countryCode.lowercase())),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 6.dp),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }

        if (isOpenDialog) {
            CountryDialog(
                countryList = countryList,
                onDismissRequest = { isOpenDialog = false },
                dialogStatus = isOpenDialog,
                onSelected = { countryItem ->
                    pickedCountry(countryItem)
                    isPickCountry = countryItem
                    isOpenDialog = false
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CountryDialog(
    modifier: Modifier = Modifier,
    countryList: List<CountryData>,
    onDismissRequest: () -> Unit,
    onSelected: (item: CountryData) -> Unit,
    dialogStatus: Boolean,
    properties: DialogProperties = DialogProperties(),
) {
    var searchValue by remember { mutableStateOf("") }
    if (!dialogStatus) searchValue = ""
    var isSearch by remember { mutableStateOf(false) }
    var filteredItems = mutableListOf<CountryData>()

    AlertDialog(
        modifier = Modifier
            .fillMaxSize(),
        onDismissRequest = onDismissRequest,
        properties = properties.let {
            DialogProperties(
                dismissOnBackPress = it.dismissOnBackPress,
                dismissOnClickOutside = it.dismissOnClickOutside,
                securePolicy = it.securePolicy,
                usePlatformDefaultWidth = false,
            )
        },
        content = {
            Surface(
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Scaffold(
                    topBar = {
                        val focusRequester = remember { FocusRequester() }
                        LaunchedEffect(isSearch) {
                            if (isSearch) {
                                focusRequester.requestFocus()
                            }
                        }
                        CenterAlignedTopAppBar(
                            title = {
                                if (isSearch) {
                                    TextField(
                                        modifier = Modifier.focusRequester(focusRequester),
                                        value = searchValue,
                                        onValueChange = { searchStr ->
                                            searchValue = searchStr
                                            /**
                                             * Search by searchValue
                                             */
                                            filteredItems = countryList.filter {
                                                it.cCountryName.contains(searchStr, ignoreCase = true) ||
                                                    it.cCountryPhoneNoCode.contains(
                                                        searchStr,
                                                        ignoreCase = true,
                                                    ) ||
                                                    it.countryCode.contains(
                                                        searchStr,
                                                        ignoreCase = true,
                                                    )
                                            }.toMutableList()
                                        },
                                        placeholder = {
                                            Text(
                                                text = "Search...",
                                                color = MaterialTheme.colorScheme.onSurface,
                                            )
                                        },
                                        colors = TextFieldDefaults.colors(
                                            disabledContainerColor = Color.Transparent,
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            disabledIndicatorColor = Color.Transparent,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                        ),
                                        textStyle = MaterialTheme.typography.labelLarge,
                                    )
                                } else {
                                    Text(
                                        modifier = Modifier.offset(y = (-2).dp),
                                        text = "Select Country",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    onDismissRequest()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = null,
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = {
                                    isSearch = !isSearch
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                    )
                                }
                            },
                        )
                    },
                ) { scaffold ->
                    scaffold.calculateBottomPadding()
                    val items = if (searchValue.isEmpty()) {
                        countryList
                    } else {
                        filteredItems
                    }
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(items) { countryItem ->
                            Row(
                                Modifier
                                    .padding(18.dp)
                                    .fillMaxWidth()
                                    .clickable(onClick = { onSelected(countryItem) }),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Image(
                                        modifier = modifier.width(30.dp),
                                        painter = painterResource(
                                            id = getFlags(
                                                countryItem.countryCode,
                                            ),
                                        ),
                                        contentDescription = null,
                                    )
                                    Text(
                                        stringResource(id = getCountryName(countryItem.countryCode.lowercase())),
                                        Modifier.padding(horizontal = 18.dp),
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.Serif,
                                    )
                                }

                                Text(
                                    text = countryItem.cCountryPhoneNoCode,
                                )
                            }
                        }
                    }
                }
            }
        },
    )
}
