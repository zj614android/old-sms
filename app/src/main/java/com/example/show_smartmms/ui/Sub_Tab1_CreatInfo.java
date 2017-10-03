package com.example.show_smartmms.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.show_smartmms.R;
import com.example.show_smartmms.bean.SmsBean;
import com.example.show_smartmms.utils.SmsUtils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * �½���Ϣ����
 * */
public class Sub_Tab1_CreatInfo extends Activity {

	private AutoCompleteTextView autoCompleteTextView;
	private ArrayList<SmsBean> newArrays;
	private ArrayList<String> datas;
	private Button sendButton;
	private EditText ed_content;
	private ImageView choiceContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//ȥ��
		setContentView(R.layout.creatinfo_layout);
		super.onCreate(savedInstanceState);

		findView();
		init();
		onclick();
	}

	private void findView() {
		autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autotextview);
		ed_content = (EditText) findViewById(R.id.ed_content);
		sendButton = (Button) findViewById(R.id.send_button);
		choiceContact = (ImageView) findViewById(R.id.choice_contact);
	}

	private void init() {
		List<String> refreshData = RefreshData();
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,refreshData);
		autoCompleteTextView.setAdapter(arrayAdapter);
		autoCompleteTextView.setThreshold(1);
	}

	private void onclick() {
		autoCompleteTextView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				autoCompleteTextView.setText(datas.get(position).split("\\(")[0]);
			}
		});

		sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String phone_no = autoCompleteTextView.getText().toString();
				if(!TextUtils.isEmpty(phone_no)){
					SmsUtils.instance().sendMessage(Sub_Tab1_CreatInfo.this,
							phone_no,ed_content.getText().toString());
					ed_content.setText("");
				}
			}
		});

		// ��תϵͳ��ϵ�˽���
		choiceContact.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(Intent.ACTION_PICK,
						Contacts.CONTENT_URI);// action,uri
				startActivityForResult(intent, 100);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK && requestCode == 100) {
			Uri uri = data.getData();
			System.out.println(uri.toString());

			ContentResolver contentResolver = getContentResolver();
			Cursor cursor = getContentResolver().query(data.getData(),
					new String[] {}, null, null, null);
			if (cursor.moveToFirst()) {
				int columnIndex = cursor.getColumnIndex(Contacts.DISPLAY_NAME);
				String name = cursor.getString(columnIndex);
				String rightAddress = null;
				
				for (SmsBean iterable_element : newArrays) {
					rightAddress = iterable_element.getAddress();
					String contactName = SmsUtils.instance().getContactName(contentResolver, rightAddress);
					if (contactName!=null && contactName.equals(name)) {
						if (autoCompleteTextView != null && !TextUtils.isEmpty(rightAddress)){
							autoCompleteTextView.setText(rightAddress);
							return;
						}
					}
				}
				
			}
			Toast.makeText(Sub_Tab1_CreatInfo.this, "��ǰ��ϵ��Ϊ��", 0).show();
			autoCompleteTextView.setText("");
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onPause() {
		newArrays = SmsUtils.instance().queryAllContacters(getContentResolver());
		super.onPause();
	}
	
	private List<String> RefreshData() {
		if (newArrays == null)
			newArrays = new ArrayList<SmsBean>();
		newArrays = (ArrayList<SmsBean>) getIntent().getSerializableExtra("datalist");

		if (datas == null)
			datas = new ArrayList<String>();
		
		if(newArrays!=null)
		for (SmsBean iterable_element : newArrays) {
			String address = iterable_element.getAddress();
			String contactName = SmsUtils.instance().getContactName(getContentResolver(), address);
			String data = address + "(" + contactName + ")";
			datas.add(data);
		}

		return datas;
	}

}
