package com.example.nfcapp3;

import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;

/*
 * 这个类用来显示NFC标签中的内容的逻辑代码
 */
public class ShowNFCTagContentActivity extends Activity{
	
	//界面上显示NFC标签中的内容的文本控件
	private TextView mTagContent;
	
	//存放NFC标签中的内容
	private Tag mDetectedTag;
	
	//存储Tag中的一些字符串数据
	private String mTagText;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_nfctag_content);
		
		mTagContent = (TextView) findViewById(R.id.textview_tag_content);
		
		//获得从上一个窗口传过来的数据
		mDetectedTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
		//因为已经在AndroidManifest中设置了过滤器，因此到这里接收到的数据肯定是NDEF格式的
		//所以下面就能直接定义NDEF类的对象
		Ndef ndef = Ndef.get(mDetectedTag);
		
		mTagText = ndef.getType() //NFC标签的类型
				 + "\n此标签的最大容量:"
				 + ndef.getMaxSize() //NFC标签的最大存储容量
				 + "字节\n\n";
		
		readNFCTag();
		
		mTagContent.setText(mTagText);
	}


	private void readNFCTag() {
		//判断一下传过来的Intent中的动作是不是跟NFC有关的
		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())){
			//非常原始的数组，实际上就是字节流
			Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefMessage[] msgs = null;
			int contentSize = 0; //统计字节流的字节大小
			if(rawMsgs != null){
				msgs = new NdefMessage[rawMsgs.length];
				for(int i=0 ; i<rawMsgs.length ; i++){
					msgs[i] = (NdefMessage) rawMsgs[i];
					contentSize += msgs[i].toByteArray().length;
				}
			}
			
			try {
				NdefRecord record = msgs[0].getRecords()[0];
				TextRecord textRecord = TextRecord.parse(record);
				mTagText += textRecord.getText() + "\n\n文本大小是" + contentSize + "字节";
			} 
			catch (Exception e) {
				
			}
			
			
		}//if结束
	}//readNFCTag()方法结束
	
	
	
	
	
	
	
	
	
	
}
