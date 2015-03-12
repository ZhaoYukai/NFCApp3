package com.example.nfcapp3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class InputTextActivity extends Activity {
	
	private EditText mTextTag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_text);
		
		mTextTag = (EditText) findViewById(R.id.edittext_text_tag);
	}
	
	
	//��ť�����¼�
	public void onClick_OK(View view){
		Intent intent = new Intent();
		intent.putExtra("text" , mTextTag.getText().toString()); //��ֵ��
		setResult(1 , intent); //ע�⣬�����������ø�resultCodeΪ1���������Ҫ
		finish();
	}
	
	
	
}
