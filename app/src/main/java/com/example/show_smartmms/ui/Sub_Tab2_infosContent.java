package com.example.show_smartmms.ui;

import java.util.ArrayList;

import com.example.show_smartmms.R;
import com.example.show_smartmms.bean.SmsBean;
import com.example.show_smartmms.utils.SmsUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class Sub_Tab2_infosContent extends Activity {

	// ui:
	private ImageView avt;
	private TextView name;
	private TextView date;
	private TextView type;
	private TextView body;
	
	// data:
	private String data_body;
	private String data_date;
	private String data_type;
	private String data_name;
	private Bitmap data_avt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sub2_infos_detail);

		findView();
		prepareData();
		fillUi();
	}

	private void fillUi() {

		name.setText(data_name);
		date.setText(data_date);
		body.setText(data_body);
		if (data_avt != null) {
			avt.setImageBitmap(data_avt);
		} else {
			avt.setImageResource(R.drawable.ic_unknow_contact_picture);
		}

		// type:
		// 0����
		// 1�ռ���
		// 2�l��
		// 3�ݸ�
		// 4�l����
		// 5ʧ��
		// 6�ȴ��l��
		switch (Integer.parseInt(data_type)) {
		case 1:
			type.setText("�ռ���");
			break;
		case 2:
			type.setText("�ѷ���");
			break;
		case 3:
			type.setText("�ݸ���");
			break;
		case 4:
			type.setText("������");
			break;
		default:
			break;
		}
	}

	private void prepareData() {
		SmsBean data = (SmsBean) getIntent().getSerializableExtra("data");
		data_body = data.getBody();
		data_date = data.getDate();
		data_type = data.getType();
		if(data_type == null)
			data_type = 0+"";
		data_name = data.getName();
		data_avt = SmsUtils.instance().getContactAvantar(getContentResolver(),data.getAddress());
	}

	private void findView() {
		avt = (ImageView) findViewById(R.id.avt);
		name = (TextView) findViewById(R.id.name);
		date = (TextView) findViewById(R.id.date);
		type = (TextView) findViewById(R.id.type);
		body = (TextView) findViewById(R.id.body);

	}

}
