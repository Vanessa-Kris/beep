package ui.components

import android.Manifest
import android.content.Context
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.i18n.phonenumbers.PhoneNumberUtil
import ui.model.Platform
import java.util.Locale
import com.example.beep.R
import kotlin.compareTo

data class Contact(
    val name: String,
    val platform: Platform
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeSheetContent(
    onStartChat: (String) -> Unit,
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedPlatform by remember { mutableStateOf<Platform?>(null) }
    var hasContactsPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }
    var showPermissionPrompt by remember { mutableStateOf(!hasContactsPermission) }
    var contacts by remember { mutableStateOf<List<Contact>>(emptyList()) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasContactsPermission = isGranted
        showPermissionPrompt = false
        if (isGranted) {
            contacts = loadContacts(context)
        }
    }
    var searchQuery by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    val countries = remember { getCountries() }

    var selectedCountry by remember {
        mutableStateOf(
            countries.first { it.regionCode == "CM" }
        )
    }

    var showCountryPicker by remember { mutableStateOf(false) }



    LaunchedEffect(hasContactsPermission) {
        contacts = if (hasContactsPermission) {
            loadContacts(context)
        } else {
            emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp)
    ) {
//        Box(
//            modifier = Modifier
//                .align(Alignment.CenterHorizontally)
//                .width(36.dp)
//                .height(4.dp)
//                .background(
//                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
//                    RoundedCornerShape(2.dp)
//                )
//        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(40.dp))

            Text(
                text = (stringResource(R.string.new_message)),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = (stringResource(R.string.close)),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Platform.entries.forEach { platform ->
                PlatformChip(
                    label = platform.label,
                    selected = selectedPlatform == platform,
                    onClick = {
                        selectedPlatform = if (selectedPlatform == platform) null else platform
                    }
                )
            }
        }
        Spacer(Modifier.height(12.dp))

        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(stringResource(R.string.search_contacts)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        Spacer(Modifier.height(12.dp))

        Row (
            modifier = Modifier
                .fillMaxWidth(),
//                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Box(modifier = Modifier.weight(1f)) {
                TextField(
                    value = phoneNumber,
                    onValueChange = {
                        phoneNumber = it.filter { c -> c.isDigit() }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    placeholder = { Text(stringResource(R.string.phone_number)) },
                    singleLine = true,
                    leadingIcon = {
                        Row(
                            modifier = Modifier
                                .clickable { showCountryPicker = true }
                                .padding(start = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = countryCodeToFlagEmoji(selectedCountry.regionCode),
                                fontSize = 18.sp
                            )

                            Spacer(Modifier.width(6.dp))

                            Text(
                                text = "+${selectedCountry.callingCode}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 44.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (phoneNumber.isNotBlank()) {
                        val fullNumber = "+${selectedCountry.callingCode}$phoneNumber"
                        val parsed = parsePhone(fullNumber, selectedCountry)
                        if (parsed != null) {
                            onStartChat(parsed)
                        }
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (phoneNumber.isNotBlank()) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        CircleShape
                    ),
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = (stringResource(R.string.add)),
                    tint = if (phoneNumber.isNotBlank()) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }


        Spacer(Modifier.height(16.dp))

        if (showPermissionPrompt && !hasContactsPermission) {
            ContactsPermissionPrompt(
                onAllow = {
                    permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                },
                onDeny = {
                    showPermissionPrompt = false
                    contacts = loadContacts(context)
                }
            )
        } else {
            val filteredContacts = contacts.filter { contact ->
                (selectedPlatform == null || contact.platform == selectedPlatform) &&
                        contact.name.contains(searchQuery, ignoreCase = true)
            }

            if (filteredContacts.isEmpty()) {
                NoContactsView(
                    phoneNumberInput = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                TextField(
                                    value = phoneNumber,
                                    onValueChange = {
                                        phoneNumber = it.filter { c -> c.isDigit() }
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    placeholder = { Text(stringResource(R.string.phone_number)) },
                                    singleLine = true,
                                    leadingIcon = {
                                        Row(
                                            modifier = Modifier
                                                .clickable { showCountryPicker = true }
                                                .padding(start = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = countryCodeToFlagEmoji(selectedCountry.regionCode),
                                                fontSize = 18.sp
                                            )

                                            Spacer(Modifier.width(6.dp))

                                            Text(
                                                text = "+${selectedCountry.callingCode}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 44.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = {
                                    if (phoneNumber.isNotBlank()) {
                                        val fullNumber = "+${selectedCountry.callingCode}$phoneNumber"
                                        val parsed = parsePhone(fullNumber, selectedCountry)
                                        if (parsed != null) {
                                            onStartChat(parsed)
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        if (phoneNumber.isNotBlank()) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                        CircleShape
                                    ),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = stringResource(R.string.add),
                                    tint = if (phoneNumber.isNotBlank()) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                )
            } else {

                ContactsList(
                    contacts = filteredContacts,
                    onContactClick = { contact ->
                        onStartChat(contact.name)
                    }
                )
            }
        }

    if (showCountryPicker) {
        ModalBottomSheet(
            onDismissRequest = { showCountryPicker = false }
        ) {
            LazyColumn {
                items(countries) { country ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedCountry = country
                                showCountryPicker = false
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = countryCodeToFlagEmoji(country.regionCode),
                            fontSize = 20.sp
                        )

                        Spacer(Modifier.width(12.dp))

                        Column {
                            Text(
                                text = country.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "+${country.callingCode}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                }
            }
        }
    }
    }
}

private fun loadContacts(context: Context): List<Contact> {
    val contacts = mutableListOf<Contact>()
    val contentResolver = context.contentResolver

    try {
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
            ),
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            if (it.count > 0) {
                val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)

                if (nameIndex >= 0) {
                    while (it.moveToNext()) {
                        val name = it.getString(nameIndex)
                        if (!name.isNullOrBlank()) {
                            contacts.add(Contact(name, Platform.SIGNAL))
                        }
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return contacts
}



@Composable
private fun PlatformChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = if (selected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val iconRes = when (label) {
                "Signal" -> ui.components.Platform.SIGNAL.iconRes
                "Telegram" -> ui.components.Platform.TELEGRAM.iconRes
                else -> 0
            }

            if (iconRes != 0) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                )
            }

            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
private fun ContactsPermissionPrompt(
    onAllow: () -> Unit,
    onDeny: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = (stringResource(R.string.allow_access_to_contacts)),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = (stringResource(R.string.allow_access_to_contacts_description)),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(onClick = onDeny) {
                Text("Not now")
            }
            Button(onClick = onAllow) {
                Text("Allow")
            }
        }
    }
}

@Composable
private fun NoContactsView(
    phoneNumberInput: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_contacts),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.add_contacts_to_start_chatting),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        phoneNumberInput()
    }
}

@Composable
private fun ContactsList(
    contacts: List<Contact>,
    onContactClick: (Contact) -> Unit
) {
    LazyColumn(
        modifier = Modifier.height(300.dp)
    ) {
        items(contacts) { contact ->
            ContactItem(
                contact = contact,
                onClick = { onContactClick(contact) }
            )
        }
    }
}

@Composable
private fun ContactItem(
    contact: Contact,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        label = "contact-scale"
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable {
                pressed = true
                onClick()
            }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(
            initials = contact.name.take(1),
            platform = when (contact.platform) {
                ui.model.Platform.TELEGRAM -> ui.components.Platform.TELEGRAM
                ui.model.Platform.SIGNAL -> ui.components.Platform.SIGNAL
            }
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = contact.name,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

fun getCountries(): List<Country> {
    val phoneUtil = PhoneNumberUtil.getInstance()

    return phoneUtil.supportedRegions.map { region ->
        Country(
            name = Locale("", region).displayCountry,
            regionCode = region,
            callingCode = phoneUtil.getCountryCodeForRegion(region)
        )
    }.sortedBy { it.name }
}

fun parsePhone(
    rawNumber: String,
    country: Country
): String? {
    return try {
        val phoneUtil = PhoneNumberUtil.getInstance()
        val number = phoneUtil.parse(rawNumber, country.regionCode)

        if (phoneUtil.isValidNumber(number)) {
            phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164)
        } else null
    } catch (e: Exception) {
        null
    }
}

fun countryCodeToFlagEmoji(countryCode: String): String {
    return countryCode
        .uppercase(Locale.US)
        .map { char ->
            Character.toChars(0x1F1E6 + (char.code - 'A'.code))
        }
        .joinToString("") { String(it) }
}


data class Country(
    val name: String,
    val regionCode: String,
    val callingCode: Int
)
