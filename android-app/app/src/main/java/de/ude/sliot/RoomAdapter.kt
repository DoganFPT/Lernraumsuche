package de.ude.sliot

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import de.ude.sliot.data_class.Room
import de.ude.sliot.filter_enum.HumidityEnum
import de.ude.sliot.filter_enum.NoiseEnum
import de.ude.sliot.filter_enum.WiFiStrengthEnum
import kotlinx.android.synthetic.main.activity_room_card.view.*
import kotlin.math.roundToInt

class RoomAdapter(private val items: ArrayList<Room>, private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_room_card, parent, false))
    }

    private fun drawRecommendationImage(recommendationLevel : Int, isDataMissing : Boolean, imageDimension : Float, textSize : Float, alphaChannel : Int) : Bitmap
    {
        //Helper variables
        val strokeWidth = 5f
        val bmpCfg = Bitmap.Config.ARGB_8888

        //Create a mask for PorterDuff (select top-part which should be not filled with the gradient)
        val bitmapMask = Bitmap.createBitmap(imageDimension.toInt(),imageDimension.toInt(), bmpCfg)
        val paintMask = Paint()
        paintMask.isAntiAlias = true
        paintMask.style = Paint.Style.FILL
        paintMask.strokeWidth = strokeWidth
        paintMask.setARGB(255,0,0,0)
        Canvas(bitmapMask).drawRect(0f, 0f, imageDimension, imageDimension * (100 - recommendationLevel) / 100, paintMask)

        //Create gradient circle
        val bitmapGradientPart = Bitmap.createBitmap(imageDimension.toInt(),imageDimension.toInt(),bmpCfg)
        val paintGradientPart = Paint()
        paintGradientPart.isAntiAlias = true
        paintGradientPart.shader = LinearGradient(0f, imageDimension, 0f, 0f,
                                                    Color.argb(alphaChannel, 239,228,191), Color.argb(alphaChannel, 0,76,147),
                                                    Shader.TileMode.CLAMP)
        paintGradientPart.style = Paint.Style.FILL
        Canvas(bitmapGradientPart).drawCircle(imageDimension/2, imageDimension/2, imageDimension/2 - strokeWidth, paintGradientPart)

        //Create white circle (background)
        val bitmapBackgroundPart = Bitmap.createBitmap(imageDimension.toInt(), imageDimension.toInt(), bmpCfg)
        val paintBackgroundPart = Paint()
        paintBackgroundPart.isAntiAlias = true
        paintBackgroundPart.style = Paint.Style.FILL
        paintBackgroundPart.setARGB(255,255,255,255)
        Canvas(bitmapBackgroundPart).drawCircle(imageDimension/2, imageDimension/2, imageDimension/2 - strokeWidth, paintBackgroundPart)

        //Cut off top part of gradient
        val porterDuffPaint = Paint()
        porterDuffPaint.isAntiAlias = true
        porterDuffPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        Canvas(bitmapGradientPart).drawBitmap(bitmapMask, Matrix(), porterDuffPaint)

        //Cut off bottom part of background
        porterDuffPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        Canvas(bitmapBackgroundPart).drawBitmap(bitmapMask, Matrix(), porterDuffPaint)

        //Draw final bitmap with some additions
        val completeBitmap = Bitmap.createBitmap(imageDimension.toInt(), imageDimension.toInt(), bmpCfg)
        val canvas = Canvas(completeBitmap)
        canvas.drawBitmap(bitmapBackgroundPart, Matrix(), Paint())
        canvas.drawBitmap(bitmapGradientPart, Matrix(), Paint())

        //Addition: black circle around
        val paintCircle = Paint()
        paintCircle.isAntiAlias = true
        paintCircle.style = Paint.Style.STROKE
        paintCircle.setARGB(alphaChannel, 120,120,120)
        paintCircle.strokeWidth = strokeWidth
        canvas.drawCircle(imageDimension/2, imageDimension/2, imageDimension/2 - strokeWidth, paintCircle)

        //Addition: notification icon (if we are missing JSONData for the room!)
        if (isDataMissing) {
            val triangle = Path()
            triangle.fillType = Path.FillType.EVEN_ODD
            triangle.moveTo(6*imageDimension/32, 2*imageDimension/32)
            triangle.lineTo(2*imageDimension/32, 10*imageDimension/32)
            triangle.lineTo(10*imageDimension/32, 10*imageDimension/32)
            triangle.close()

            val paintNotify = Paint()
            paintNotify.isAntiAlias = true

            //black
            paintNotify.strokeWidth = 2f * strokeWidth
            paintNotify.style = Paint.Style.STROKE
            paintNotify.setARGB(alphaChannel, 0, 0,0)
            canvas.drawPath(triangle, paintNotify)

            //white
            paintNotify.strokeWidth = 1.6f * strokeWidth
            paintNotify.style = Paint.Style.STROKE
            paintNotify.setARGB(alphaChannel, 255, 255,255)
            canvas.drawPath(triangle, paintNotify)

            //red
            paintNotify.strokeWidth = 0.8f * strokeWidth
            paintNotify.style = Paint.Style.FILL_AND_STROKE
            paintNotify.setARGB(alphaChannel, 225, 200,0)
            canvas.drawPath(triangle, paintNotify)

            //exclamation mark
            val paintChar = Paint()
            paintChar.isAntiAlias = true
            paintChar.setARGB(alphaChannel, 0, 0, 0)
            paintChar.textAlign = Paint.Align.CENTER
            paintChar.textSize = textSize
            //paintChar.typeface = Typeface.MONOSPACE
            paintChar.typeface = Typeface.SERIF
            
            canvas.drawText("!", 192*imageDimension/1024, (265*imageDimension/1024) - (paintNotify.descent() + paintNotify.ascent()) / 2, paintChar)
        }

        //Addition: centered text
        val paintText = Paint()
        paintText.isAntiAlias = true
        paintText.setARGB(alphaChannel, 0,0,0)
        paintText.textAlign = Paint.Align.CENTER
        paintText.textSize = textSize
        paintText.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText(context.getString(R.string.formatter_recommendation_level, recommendationLevel), imageDimension/2, (imageDimension / 2) - (paintText.descent() + paintText.ascent()) / 2, paintText)

        return completeBitmap
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //Take values from the passed array (room JSONData) and assign them to the ViewHolder's properties
        if (holder is ViewHolder) {
            val room = items[position]

            //Use image for recommendation section
            //Magic formula? No!
            //Look up density-independent pixels (https://developer.android.com/training/multiscreen/screendensities)
            //px = dp * (dpi / 160) and we have used 250 pixel for the initial design choices. Thus we have to put (approximately) 95 into our formula.
            val bitmap = drawRecommendationImage(room.recommendationLevel, room.isDataMissing(), ((95 * Resources.getSystem().displayMetrics.densityDpi) / 160).toFloat(), 50f, 255)

            //Load settings for multi-language support an °C/°F support
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val useFahrenheit = sharedPreferences.getBoolean(context.getString(R.string.settings_key_fahrenheit), false)

            holder.imageViewRecommendationLevel?.setImageBitmap(bitmap)
            holder.textViewRoomName?.let { it.text = room.title }
            holder.textViewRoomTemperature?.let {
                if (room.temperatureHistory?.isMissingData() != false) //IMPORTANT: != false is in this case not the same as == true !!!
                    it.text = context.getString(R.string.caption_not_available)
                else {
                    if (!useFahrenheit)
                        it.text = context.getString(R.string.formatter_temperature_C, room.temperatureHistory.avg.roundToInt())
                    else
                        room.temperatureHistory.avg.let { celsiusTemp -> it.text = context.getString(R.string.formatter_temperature_F, (celsiusTemp * 1.8 + 32).roundToInt()) }
                }
            }
            holder.textViewRoomHumidity?.let {
                if (room.humidityHistory?.isMissingData() != false) //IMPORTANT: != false is in this case not the same as == true !!!
                    it.text = context.getString(R.string.caption_not_available)
                else {
                    val enum = HumidityEnum.valueToEnum(room.humidityHistory.avg)
                    it.text = when (enum) {
                        HumidityEnum.VERY_DRY -> context.getString(R.string.caption_verydry)
                        HumidityEnum.DRY -> context.getString(R.string.caption_dry)
                        HumidityEnum.AVERAGE -> context.getString(R.string.caption_average_normal)
                        HumidityEnum.HUMID -> context.getString(R.string.caption_humid)
                        HumidityEnum.VERY_HUMID -> context.getString(R.string.caption_veryhumid)
                    }
                }
            }
            holder.textViewRoomWifiSignalStrength?.let {
                if (room.wifiHistory?.isMissingData() != false) //IMPORTANT: != false is in this case not the same as == true !!!
                    it.text = context.getString(R.string.caption_not_available)
                else {
                    val enum = WiFiStrengthEnum.valueToEnum(room.wifiHistory.avg)
                    it.text = when (enum) {
                        WiFiStrengthEnum.NONE -> context.getString(R.string.caption_none_kein)
                        WiFiStrengthEnum.VERY_BAD -> context.getString(R.string.caption_verybad)
                        WiFiStrengthEnum.BAD -> context.getString(R.string.caption_bad)
                        WiFiStrengthEnum.AVERAGE -> context.getString(R.string.caption_average_middle)
                        WiFiStrengthEnum.GOOD -> context.getString(R.string.caption_good)
                        WiFiStrengthEnum.VERY_GOOD -> context.getString(R.string.caption_verygood)
                        WiFiStrengthEnum.EXCELLENT -> context.getString(R.string.caption_excellent)
                    }
                }
            }
            holder.textViewRoomNoiseLevel?.let {
                if (room.noiseHistory?.isMissingData() != false) //IMPORTANT: != false is in this case not the same as == true !!!
                    it.text = context.getString(R.string.caption_not_available)
                else {
                    val enum = NoiseEnum.valueToEnum(room.noiseHistory.avg)
                    it.text = when (enum) {
                        NoiseEnum.VERY_QUIET -> context.getString(R.string.caption_veryquiet)
                        NoiseEnum.QUIET -> context.getString(R.string.caption_quiet)
                        NoiseEnum.WHISPERING -> context.getString(R.string.caption_whispering)
                        NoiseEnum.AVERAGE -> context.getString(R.string.caption_average_normal)
                        NoiseEnum.ROOM_VOLUME -> context.getString(R.string.caption_moderate_roomvolume)
                        NoiseEnum.LOUD -> context.getString(R.string.caption_loud)
                        NoiseEnum.VERY_LOUD -> context.getString(R.string.caption_veryloud)
                    }
                }
            }
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        //Add my view elements from the view that is passed as properties here
        val imageViewRecommendationLevel : ImageView? = view.recommendationImage
        val textViewRoomName: TextView? = view.roomName
        val textViewRoomTemperature: TextView? = view.roomTemp
        val textViewRoomHumidity: TextView? = view.roomHumidity
        val textViewRoomWifiSignalStrength: TextView? = view.roomWifiSignalStrength
        val textViewRoomNoiseLevel: TextView? = view.roomNoiseLevel
    }
}