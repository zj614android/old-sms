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
	 * ָ�����ͼƬ�����ű���
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// ���ԭʼͼƬ�Ŀ��
		int imageHeight = options.outHeight;
		int imageWidth = options.outWidth;
		int inSimpleSize = 1;
		if (imageHeight > reqHeight || imageWidth > reqWidth) {
			// ����ѹ���ı�������Ϊ��߱���
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
		// ��������ͼƬ�õ�matrix����
		Matrix matrix = new Matrix();

		// ���������ʣ��³ߴ��ԭʼ�ߴ�
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// ����ͼƬ����
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = null;
		if (bgimage != null)
			bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height, matrix,
					true);
		return bitmap;

	}

	/**
	 * ���ڸ�ʽ��
	 * */
	public String formatDate(Date date) {
		if (sdf == null) {
			sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		}
		return sdf.format(date);
	}
}
