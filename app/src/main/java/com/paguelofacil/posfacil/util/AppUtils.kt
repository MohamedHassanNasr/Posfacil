package com.paguelofacil.posfacil.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.telephony.PhoneNumberUtils
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.core.content.edit
import androidx.core.graphics.ColorUtils
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.data.database.PreferenceManager
import com.paguelofacil.posfacil.data.database.local.LocalDataSource
import com.paguelofacil.posfacil.util.Constantes.CoroutinesBase
import com.paguelofacil.posfacil.util.Constantes.LastUpdatedOn
import java.text.DecimalFormat
import java.util.*


const val countryCode = "+51"

/**
 * Generate random number
 *
 * @return a random integer value. Useful for notification Ids.
 */
fun generateRandomNumber(): Int {
    val random = Random()
    return random.nextInt(9999 - 1000) + 1000
}

/**
 * Get time zone offset
 *
 * @return a timezone offset of the device
 */
fun getTimeZoneOffset(): String {
    val mCalendar: Calendar = GregorianCalendar()
    val mTimeZone = mCalendar.timeZone
    return ((mTimeZone.rawOffset) / (1000 * 60)).toString()
}

fun formatAndAddDecimalToNumber(text: String): String {
    val textWithoutDecimal = StringBuilder()
    textWithoutDecimal.append("000")
    textWithoutDecimal.append(text.replace(".", ""))
    textWithoutDecimal.insert(textWithoutDecimal.length - 2, ".")

    val nf = DecimalFormat.getInstance()
    nf.minimumIntegerDigits = 1
    nf.minimumFractionDigits = 2
    nf.maximumFractionDigits = 2

    val number: Number? = nf.parse(textWithoutDecimal.toString())
    return formatAmount(number?.toDouble())
}

/**
 * Format amount
 *
 * @param amount to be formatted
 * @return the formatted string value of the [amount] with two digit precession
 */
fun formatAmount(amount: Double?): String {
    return amount?.let { String.format("%.2f", amount) } ?: { 0.00 }.toString()
}

/**
 * Get device id
 *
 * @param context
 * @return the Android Id of the device
 */
@SuppressLint("HardwareIds")
fun getDeviceId(context: Context): String = Settings.Secure.getString(
    context.contentResolver,
    Settings.Secure.ANDROID_ID
)

/**
 * Is valid password
 *
 * @param password
 * @return true if the [password] string matched with the pattern else false
 */
fun isValidPassword(password: String): Boolean {
    val specialCharacters = "-@%\\[\\}+'!/#$^?:;,\\(\"\\)~`.*=&\\{>\\]<_"
    val PASSWORD_REGEX =
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[$specialCharacters])(?=\\S+$).{8,25}$"
    return password.matches(Regex(PASSWORD_REGEX))
}

/**
 * Is valid visa card
 *
 * @param cardNo
 * @return true if the [cardNo] matches with the Visa card number pattern else false
 */
fun isValidVisaCard(cardNo: String): Boolean {
    val visaCardRegex =
        "^4[0-9]{12}(?:[0-9]{3})?$"
    return cardNo.matches(Regex(visaCardRegex))
}

/**
 * Copy View to Canvas and return bitMap
 * Won't work on Surface View
 */
fun getBitmapFromView(view: View): Bitmap? {
    val bitmap =
        Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    return bitmap
}

/**
 * Copy View to Canvas and return bitMap and fill it with default color
 * Won't work on Surface View
 */
fun getBitmapFromView(view: View, defaultColor: Int): Bitmap? {
    val bitmap =
        Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(defaultColor)
    view.draw(canvas)
    return bitmap
}

/**
 * Is valid master card
 *
 * @param cardNo
 * @return true if the [cardNo] matches with the Master card number pattern else false
 */
fun isValidMasterCard(cardNo: String): Boolean {
    val masterCardRegex =
        "^(5[1-5][0-9]{14}|2(22[1-9][0-9]{12}|2[3-9][0-9]{13}|[3-6][0-9]{14}|7[0-1][0-9]{13}|720[0-9]{12}))$"
    return cardNo.matches(Regex(masterCardRegex))
}

/**
 * Checks if the Internet connection is available.
 * @return true if the Internet connection is available. False otherwise.
 */
fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            //for other device how are able to connect with Ethernet
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        val nwInfo = connectivityManager.activeNetworkInfo ?: return false
        nwInfo.isConnected
    }
}

/**
 * Clear all activities
 *
 * @param className move to this class after clearing the task stack
 */
fun Activity.clearAllActivities(className: Class<*>) {
    this.finishAffinity()
    val intent = Intent(this, className)
    startActivity(intent)
}

/**
 * Defined request codes for File selection action
 */
object REQ_CODES {
    const val REQ_CAMERA = 5336
    const val REQ_GALLERY = 5337
}

/**
 * Format phone no
 *
 * @param phoneNumber
 * @param countryCode
 * @return [phoneNumber] prefixed with the [countryCode] in a formatted matter
 */
fun formatPhoneNo(phoneNumber: String, countryCode: String): String {
    val stringBuilder = StringBuilder(phoneNumber)
    if (!phoneNumber.startsWith(countryCode)) {
        stringBuilder.insert(0, countryCode)
    }
    return stringBuilder.toString()
}

/**
 * Format card no
 *
 * @param cardNumber
 * @param spaceAfterDigits
 * @return [cardNumber] with the spacing after digits specified as per [spaceAfterDigits]
 */
fun formatCardNo(cardNumber: String, spaceAfterDigits: Int): String {
    val stringBuilder = StringBuilder(cardNumber)
    if (cardNumber.isNotEmpty() && (cardNumber.length % (spaceAfterDigits + 1) == 0) && (cardNumber[cardNumber.length - 1].toString() != " ")) {
        stringBuilder.insert(stringBuilder.length - 1, " ")
    }
    return stringBuilder.toString()
}

/**
 * Format account no
 *
 * @param accountNo
 * @param spaceAfterDigits
 * @return [accountNo] with the spacing after digits specified as per [spaceAfterDigits]
 */
fun formatAccountNo(accountNo: String, spaceAfterDigits: Int): String {
    val stringBuilder = StringBuilder(accountNo)
    if (accountNo.isNotEmpty() && (accountNo.length % (spaceAfterDigits + 1) == 0) && (accountNo[accountNo.length - 1].toString() != "-")) {
        stringBuilder.insert(stringBuilder.length - 1, "-")
    }
    return stringBuilder.toString()
}

/**
 * Is email valid
 *
 * @param emailId
 * @return true if the [emailId] matched with email pattern. Otherwise false
 */
fun isEmailValid(emailId: String): Boolean {
    val EMAIL_PATTERN = ("[A-Z0-9a-z-!#$%^&*()_+=|:;'<,>.?/]+@[A-Za-z0-9.]+\\.[A-Za-z]{2,50}")
    return if (emailId.length < 3 || emailId.length > 265) false else {
        emailId.matches(Regex(EMAIL_PATTERN))
    }
}

/**
 * Show toast message
 *
 * @param message
 * @param length
 */
fun showToast(message: String?, length: Int? = Toast.LENGTH_SHORT) {
    Toast.makeText(ApplicationClass.instance, message, Toast.LENGTH_SHORT).show()
}

/**
 * Clear previous user's local data except the quick actions, recent payment contacts and contacts.
 * & recent charge request contacts
 *
 */

fun clearLocalDataForPreviousUser() = CoroutinesBase.io {
    LocalDataSource.instance.apply {
     //   walletBalanceDao().delete()
       // cardsLocalDao().deleteAll()

        //recentPendingActivitiesLocalDao().deleteAll()
        //recentCompletedActivitiesLocalDao().deleteAll()

        //servicesLocalDao().deleteAll()

        //fundRequestsLocalDao().deleteAll()
        //bankAccountsLocalDao().deleteAll()

        PreferenceManager.sharedPref.edit {
            remove(LastUpdatedOn.FUND_REQUESTS)
            remove(LastUpdatedOn.SERVICES)
            remove(LastUpdatedOn.SHOPS)
            remove(LastUpdatedOn.CONTACTS)
            remove(LastUpdatedOn.PENDING_ACTIVITIES)
            remove(LastUpdatedOn.COMPLETED_ACTIVITIES)
        }
    }
}


/**
 * Is dark color
 *
 * @param color
 * @return true if the color code provided belongs to a darker shade. False otherwise
 */
fun isDarkColor(@ColorInt color: Int): Boolean {
    return ColorUtils.calculateLuminance(color) < 0.5
}

/**
 * Open Whatsapp app chat screen for a phone number
 *
 * @param context
 * @param phone
 * @param countryCode
 */
@SuppressLint("NewApi")
fun whatsApp(context: Context, phone: String, countryCode: String) {
    val formattedNumber: String = PhoneNumberUtils.formatNumber(phone, countryCode)
    try {
        val sendIntent = Intent("android.intent.action.MAIN")
        sendIntent.component = ComponentName("com.whatsapp", "com.whatsapp.Conversation")
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.type = "text/plain"
        sendIntent.putExtra(Intent.EXTRA_TEXT, "")
        sendIntent.putExtra("jid", "$formattedNumber@s.whatsapp.net")
        sendIntent.setPackage("com.whatsapp")

        if (sendIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(sendIntent)
        } else {
            Toast.makeText(context, context.getString(R.string.whatsapp_not_installed), Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, context.getString(R.string.whatsapp_not_installed), Toast.LENGTH_SHORT).show()
    }
}

/**
 * Open an available email client app profiled with the email addresses and the email subject
 * in order to compose a mail
 *
 * @param context
 * @param addresses
 * @param subject
 */
fun composeEmail(context: Context, addresses: Array<String>, subject: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:") // only email apps should handle this
        putExtra(Intent.EXTRA_EMAIL, addresses)
        putExtra(Intent.EXTRA_SUBJECT, subject)
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, context.getString(R.string.no_email_app_available), Toast.LENGTH_LONG).show()
    }
}

/**
 * Moves the given **T** item to the specified index
 */
fun <T> MutableList<T>.move(item: T, newIndex: Int) {
    val currentIndex = indexOf(item)
    if (currentIndex < 0) return
    removeAt(currentIndex)
    add(newIndex, item)
}

/**
 * Moves the given item at the `oldIndex` to the `newIndex`
 */
fun <T> MutableList<T>.moveAt(oldIndex: Int, newIndex: Int) {
    val item = this[oldIndex]
    removeAt(oldIndex)
    if (oldIndex > newIndex)
        add(newIndex, item)
    else
        add(newIndex - 1, item)
}

/**
 * Moves all items meeting a predicate to the given index
 */
fun <T> MutableList<T>.moveAll(newIndex: Int, predicate: (T) -> Boolean) {
    check(newIndex >= 0 && newIndex < size)
    val split = partition(predicate)
    clear()
    addAll(split.second)
    addAll(if (newIndex >= size) size else newIndex, split.first)
}

/**
 * Moves the given element at specified index up the **MutableList** by one increment
 * unless it is at the top already which will result in no movement
 */
fun <T> MutableList<T>.moveUpAt(index: Int) {
    if (index == 0) return
    if (index < 0 || index >= size) throw Exception("Invalid index $index for MutableList of size $size")
    val newIndex = index + 1
    val item = this[index]
    removeAt(index)
    add(newIndex, item)
}

/**
 * Moves the given element **T** up the **MutableList** by one increment
 * unless it is at the bottom already which will result in no movement
 */
fun <T> MutableList<T>.moveDownAt(index: Int) {
    if (index == size - 1) return
    if (index < 0 || index >= size) throw Exception("Invalid index $index for MutableList of size $size")
    val newIndex = index - 1
    val item = this[index]
    removeAt(index)
    add(newIndex, item)
}

/**
 * Moves the given element **T** up the **MutableList** by an index increment
 * unless it is at the top already which will result in no movement.
 * Returns a `Boolean` indicating if move was successful
 */
fun <T> MutableList<T>.moveUp(item: T): Boolean {
    val currentIndex = indexOf(item)
    if (currentIndex == -1) return false
    val newIndex = (currentIndex - 1)
    if (currentIndex <= 0) return false
    remove(item)
    add(newIndex, item)
    return true
}

/**
 * Moves the given element **T** up the **MutableList** by an index increment
 * unless it is at the bottom already which will result in no movement.
 * Returns a `Boolean` indicating if move was successful
 */
fun <T> MutableList<T>.moveDown(item: T): Boolean {
    val currentIndex = indexOf(item)
    if (currentIndex == -1) return false
    val newIndex = (currentIndex + 1)
    if (newIndex >= size) return false
    remove(item)
    add(newIndex, item)
    return true
}


/**
 * Moves first element **T** up an index that satisfies the given **predicate**, unless its already at the top
 */
inline fun <T> MutableList<T>.moveUp(crossinline predicate: (T) -> Boolean) = find(predicate)?.let { moveUp(it) }

/**
 * Moves first element **T** down an index that satisfies the given **predicate**, unless its already at the bottom
 */
inline fun <T> MutableList<T>.moveDown(crossinline predicate: (T) -> Boolean) = find(predicate)?.let { moveDown(it) }

/**
 * Moves all **T** elements up an index that satisfy the given **predicate**, unless they are already at the top
 */
inline fun <T> MutableList<T>.moveUpAll(crossinline predicate: (T) -> Boolean) = asSequence().withIndex()
    .filter { predicate.invoke(it.value) }
    .forEach { moveUpAt(it.index) }

/**
 * Moves all **T** elements down an index that satisfy the given **predicate**, unless they are already at the bottom
 */
inline fun <T> MutableList<T>.moveDownAll(crossinline predicate: (T) -> Boolean) = asSequence().withIndex()
    .filter { predicate.invoke(it.value) }
    .forEach { moveDownAt(it.index) }

/**
 * Swaps the position of two items at two respective indices
 */
fun <T> MutableList<T>.swap(indexOne: Int, indexTwo: Int) {
    Collections.swap(this, indexOne, indexTwo)
}

/**
 * Swaps the index position of two items
 */
fun <T> MutableList<T>.swap(itemOne: T, itemTwo: T) = swap(indexOf(itemOne), indexOf(itemTwo))