package com.example.show_smartmms.utils;//package com.example.show_smartmms.utils;
//
//import android.text.Editable;
//import android.text.Selection;
//import android.text.TextWatcher;
//import android.widget.EditText;
//
///**
// * ����δ�õ� ����
// * */
//public class MaxLengthWatcher implements TextWatcher {
//
//	private int maxLen = 0;
//	private EditText editText = null;
//
//	public MaxLengthWatcher(int maxLen, EditText editText) {
//		super();
//		this.maxLen = maxLen;
//		this.editText = editText;
//	}
//	
//	public MaxLengthWatcher(){
//	}
//
//	@Override
//	public void beforeTextChanged(CharSequence s, int start, int count,
//			int after) {
//		// TODO Auto-generated method stub
//		System.out.println("��˾ͽbeforeTextChanged     "+s);
//
//	}
//	@Override
//	public void onTextChanged(CharSequence s, int start, int before, int count) {
////		Editable editable = editText.getText();
////		int len = editable.length();
////
////		if (len > maxLen) {
////			int selEndIndex = Selection.getSelectionEnd(editable);
////			String str = editable.toString();
////			// ��ȡ���ַ���
////			String newStr = str.substring(0, maxLen);
////			editText.setText(newStr);
////			editable = editText.getText();
////
////			// ���ַ����ĳ���
////			int newLen = editable.length();
////			// �ɹ��λ�ó����ַ�������
////			if (selEndIndex > newLen) {
////				selEndIndex = editable.length();
////			}
////			// �����¹�����ڵ�λ��
////			Selection.setSelection(editable, selEndIndex);
////		}
//		System.out.println("��˾ͽonTextChanged     "+s);
//	}
//
//	@Override
//	public void afterTextChanged(Editable s) {
//		// TODO Auto-generated method stub
//		System.out.println("��˾ͽEditable     "+s);
//
//	}
//
//}
