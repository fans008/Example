package com.common.ducis.base.databinding

/**
 * @ClassName: GlideDataBindingComponent
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2022/3/15
 */
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.Dimension
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.common.ducis.DucisLibrary
import com.common.ducis.commonutil.log.MyLog
import kotlin.math.max

object GlideDataBindingComponent {

    /**
     * 加载圆形图片
     * @param url 图片来源
     * @param holder 占位图
     * @param corner 设置圆角, 如果设置参数则默认为圆形
     */
    @BindingAdapter(value = ["imgCorner", "holder", "corner"], requireAll = false)
    @JvmStatic
    @Deprecated("使用imgCircle或img替换")
    fun loadImageCornerWithHolder(v: ImageView, url: Any?, holder: Drawable?, @Dimension corner: Int?) {
        val requestBuilder = Glide.with(v.context).load(url).placeholder(holder)
        MyLog.d("加载图片----loadImageCornerWithHolder------url:$url,corner:${corner}")
        if (corner == null) {
            requestBuilder.circleCrop()
        } else {
            requestBuilder.transform(
                CenterCrop(),
                RoundedCorners((corner * DucisLibrary.appContext.resources.displayMetrics.density).toInt())
            )
        }
        requestBuilder.into(v)
    }

    /**
     * 加载圆形图片
     * @param url 图片来源
     * @param holder 占位图
     */
    @BindingAdapter(value = ["imgCircle", "holder"], requireAll = false)
    @JvmStatic
    fun loadImageCircle(v: ImageView, url: Any?, holder: Drawable?) {
        MyLog.d("加载图片----loadImageCircle------url:$url")
        Glide.with(v.context).load(url).circleCrop().placeholder(holder).into(v)
    }

    /**
     * 加载图片
     * @param url 图片来源
     * @param holder 占位图
     * @param corner 图片四周圆角半径值
     */
    @BindingAdapter(value = ["img", "holder", "corner"], requireAll = false)
    @JvmStatic
    fun loadImageWithHolder(v: ImageView, url: Any?, holder: Drawable?, corner: Int?) {
        Glide.with(v.context).asBitmap()
            .transform(CenterCrop(), RoundedCorners(((corner ?: (10 * DucisLibrary.appContext.resources.displayMetrics.density))).toInt()))
            .load(url)
            .into(object:CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val imageWidth = resource.width
                    val imageHeight = resource.height
                    var width = imageWidth
                    var height = imageHeight
                    //默认最大值
                    val maxValue = (200 * DucisLibrary.appContext.resources.displayMetrics.density).toInt()
                    if (width > maxValue || height > maxValue){
                        if (width > height){
                            width = maxValue
                            height = width * imageHeight/imageWidth
                        }else{
                            height = maxValue
                            width = height * imageWidth/imageHeight
                        }
                    }
                    MyLog.e("加载图片----loadImageWithHolder------url:$url,corner:${corner}，width:$width,height:$height")
                    val layoutParams = v.layoutParams
                    layoutParams.width = width;
                    layoutParams.height = height;
                    v.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
        MyLog.d("加载图片----loadImageWithHolder------url:$url,corner:${corner}")
//        val requestBuilder = Glide.with(v.context).load(url).placeholder(holder)
//        if (corner != null) {
//            requestBuilder.transform(
//                CenterCrop(),
//                RoundedCorners((corner * DucisLibrary.appContext.resources.displayMetrics.density).toInt())
//            )
//        }
//        v.maxHeight = (200 * DucisLibrary.appContext.resources.displayMetrics.density).toInt()
//        v.maxWidth = (200 * DucisLibrary.appContext.resources.displayMetrics.density).toInt()
//        requestBuilder.into(v)
    }

    /**
     * 加载圆形Gif
     * @param url 图片来源
     * @param holder 占位图
     */
    @BindingAdapter(value = ["gifCircle", "holder"], requireAll = false)
    @JvmStatic
    fun loadGifCircle(v: ImageView, url: Any?, holder: Drawable?) {
        Glide.with(v.context).asGif().load(url).circleCrop().placeholder(holder).into(v)
    }

    /**
     * 加载Gif图片
     * @param url 图片来源
     * @param holder 占位图
     * @param corner 图片四周圆角半径值
     */
    @SuppressLint("CheckResult")
    @BindingAdapter(value = ["gif", "holder", "corner"], requireAll = false)
    @JvmStatic
    fun loadGifWithHolder(v: ImageView, url: Any?, holder: Drawable? = null, corner: Int?) {
        val requestBuilder = Glide.with(v.context).asGif().load(url).placeholder(holder)
        if (corner != null) {
            requestBuilder.transform(
                CenterCrop(),
                RoundedCorners((corner * DucisLibrary.appContext.resources.displayMetrics.density).toInt())
            )
        }
        requestBuilder.into(v)
    }
}