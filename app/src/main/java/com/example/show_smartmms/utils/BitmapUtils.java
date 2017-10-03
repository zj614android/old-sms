package com.example.show_smartmms.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.show_smartmms.common.CommonFields;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class BitmapUtils {

	static BitmapUtils bitmapUtils;
	private SimpleDateFormat sdf;
	private int width = 0;
	private int height = 0;

	private BitmapUtils() {
	}

	public static synchronized BitmapUtils instance() {
		if (bitmapUtils == null) {
			bitmapUtils = new BitmapUtils();
		}
		return bitmapUtils;
	}

	/**
	 * 指定输出图片的缩放比例
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// 获得原始图片的宽高
		int imageHeight = options.outHeight;
		int imageWidth = options.outWidth;
		int inSimpleSize = 1;
		if (imageHeight > reqHeight || imageWidth > reqWidth) {
			// 计算压缩的比例：分为宽高比例
			final int heightRatio = Math.round((float) imageHeight
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) imageWidth
					/ (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSimpleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSimpleSize;
	}

	public Bitmap zoomImage(Bitmap bgimage, int newWidth, int newHeight) {

		if (bgimage != null) {
			width = bgimage.getWidth();
			height = bgimage.getHeight();
		}
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();

		// 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = null;
		if (bgimage != null)
			bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height, matrix,
					true);
		return bitmap;

	}

	/**
	 * 日期格式化
	 * */
	public String formatDate(Date date) {
		if (sdf == null) {
			sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		}
		return sdf.format(date);
	}
}
