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
 * �����������ʾNFC��ǩ�е����ݵ��߼�����
 */
public class ShowNFCTagContentActivity extends Activity{
	
	//��������ʾNFC��ǩ�е����ݵ��ı��ؼ�
	private TextView mTagContent;
	
	//���NFC��ǩ�е�����
	private Tag mDetectedTag;
	
	//�洢Tag�е�һЩ�ַ�������
	private String mTagText;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_nfctag_content);
		
		mTagContent = (TextView) findViewById(R.id.textview_tag_content);
		
		//��ô���һ�����ڴ�����������
		mDetectedTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
		//��Ϊ�Ѿ���AndroidManifest�������˹���������˵�������յ������ݿ϶���NDEF��ʽ��
		//�����������ֱ�Ӷ���NDEF��Ķ���
		Ndef ndef = Ndef.get(mDetectedTag);
		
		mTagText = ndef.getType() //NFC��ǩ������
				 + "\n�˱�ǩ���������:"
				 + ndef.getMaxSize() //NFC��ǩ�����洢����
				 + "�ֽ�\n\n";
		
		readNFCTag();
		
		mTagContent.setText(mTagText);
	}


	private void readNFCTag() {
		//�ж�һ�´�������Intent�еĶ����ǲ��Ǹ�NFC�йص�
		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())){
			//�ǳ�ԭʼ�����飬ʵ���Ͼ����ֽ���
			Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefMessage[] msgs = null;
			int contentSize = 0; //ͳ���ֽ������ֽڴ�С
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
				mTagText += textRecord.getText() + "\n\n�ı���С��" + contentSize + "�ֽ�";
			} 
			catch (Exception e) {
				
			}
			
			
		}//if����
	}//readNFCTag()��������
	
	
	
	
	
	
	
	
	
	
}
