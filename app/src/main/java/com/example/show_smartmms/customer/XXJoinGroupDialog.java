package com.example.show_smartmms.customer;

import com.example.show_smartmms.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class XXJoinGroupDialog extends Dialog {
	private Context context;
	private ImageView img;
	private TextView txt;

	public XXJoinGroupDialog(Context context) {
		super(context, R.style.progress_dialog);
		this.context = context;
		// ���ز����ļ�
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.joingroupdialog, null);
//		img = (ImageView) view.findViewById(R.id.progress_dialog_img);
//		txt = (TextView) view.findViewById(R.id.progress_dialog_txt);
		// ��ͼƬ��Ӷ�̬Ч��
//		Animation anim = AnimationUtils.loadAnimation(context,
//				R.anim.loading_dialog_progressbar);
//		img.setAnimation(anim);
//		txt.setText(R.string.progressbar_dialog_txt);
//		// dialog�����ͼ
//		setContentView(view);
//		show();
		// dismiss();
	}


}