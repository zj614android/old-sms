package com.example.show_smartmms.broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SendMessageBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		int resultCode = getResultCode();
		if(resultCode == Activity.RESULT_OK){
			Toast.makeText(context, "���ŷ��ͳɹ�", 0).show();
		}else {
			Toast.makeText(context, "���ŷ���ʧ��", 0).show();
		}
	}
}
