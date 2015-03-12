package com.example.nfcapp3;

import java.nio.charset.Charset;
import java.util.Locale;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ReadWriteTextMainActivity extends Activity {
	
	private NfcAdapter mNfcAdapter;
	private PendingIntent mPendingIntent;
	
	private TextView mInputText;
	private String mText;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_write_text_main);
		
		mInputText = (TextView) findViewById(R.id.textview_input_text);
		
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		//һ���ػ�NFC����Ϣ���͵���PendingIntent�������
		mPendingIntent = PendingIntent.getActivity(this , 0 , new Intent(this , getClass()) , 0);
	}
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		if(mText == null){//mText���ǿ��ŵĻ����ʹ�NFC��ǩ�ж�����
			Intent myIntent = new Intent(this , ShowNFCTagContentActivity.class);
			myIntent.putExtras(intent);
			myIntent.setAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
			startActivity(myIntent);
		}
		else{ //���mText�������ݵĻ�������NFC��ǩ��д����
			//�Ȼ��Tag
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			NdefMessage ndefMessage = new NdefMessage( new NdefRecord[]{createTextRecord(mText)} );
			if(writeTag(ndefMessage, tag)){
				Toast.makeText(this , "�ѳɹ�д������" , Toast.LENGTH_SHORT).show();
			}
			else{
				Toast.makeText(this , "д������ʧ�ܣ�����NFC��ǩ�������Ƿ����" , Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 1 && resultCode == 1){ //����֮ǰ���ù��ö��߶�Ϊ1����������������Ϊ�Ƕ԰���
			mText = data.getStringExtra("text"); //���ݼ�ֵ��ȡ���༭�������������
			mInputText.setText(mText);
		}
	}
	
	
	
	/*
	 * TextRecord����ʵ�ֵ���ؼ������ǰ�NFC��ǩ�е�NDEF�����ֽ��������ɴ��ı�����ô��Ȼ��
	 * �������Ҫ�Ѵ��ı�д��NFC��Ҳ�ð�������ı�������NDEF�ֽ���������������������������
	 * �Ѵ��ı�����һ���ĸ�ʽ���洢��NdefRecord�Ķ�����
	 */
	public NdefRecord createTextRecord(String text){
		//����������Ա��������
		byte[] langBytes = Locale.CHINA.getLanguage().getBytes(Charset.forName("US-ASCII"));
		//ָ���ַ���������UTF-8
		Charset utfEncoding = Charset.forName("UTF-8");
		//���������Ĵ��ı������ַ���
		byte[] textBytes = text.getBytes(utfEncoding);
		//����NDEF��״̬�ֽ����λ�����Ϊ0����UTF-8�ģ�����������һ������
		int utfBit = 0;
		//״̬�ֽ�
		char status = (char) (utfBit + langBytes.length);
		//����NDEF��ʽ���ֳ�3�Ρ���һ����״̬�ֽ�(1���ֽ�)���ڶ��������Ա��룻���������ı���
		//���鷽����������Ϊ��ȷ����С�����Էֱ�3�εĸ��Գ���
		byte[] data = new byte[1 + langBytes.length + textBytes.length];
		data[0] = (byte) status; //����״̬��
		//��һ��������Դ���飻�ڶ��������Ǵ�Դ�������￪ʼ��������0��ʼ���Ǵ���ͷ�ϣ�
		//������������Ŀ�����飻���ĸ������Ǵ�Ŀ����������￪ʼ���գ���1��ʼ��ʾ���±�Ϊ1���￪ʼ����Ϊ��0����״̬��
		//����������Ǵ�Դ�����п������Ķ�����ΪԴ�����length�Ļ����ǰ�Դ����ȫ������Ŀ����������
		System.arraycopy(langBytes , 0 , data , 1 , langBytes.length);
		//����ٰ��ı�������3��
		System.arraycopy(textBytes , 0 , data , 1 + langBytes.length , textBytes.length);
		//�����NDEF��ʽ�����ݾ͹�������ˣ�����Ҫ������װ��NdefRecord������
		NdefRecord ndefRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN , NdefRecord.RTD_TEXT , new byte[0] , data);
		return ndefRecord;
	}
	
	
	/*
	 * ��NDEF����д��NFC��ǩ��
	 */
	public Boolean writeTag(NdefMessage ndefMessage , Tag tag){
		try {
			Ndef ndef = Ndef.get(tag);
			ndef.connect();
			ndef.writeNdefMessage(ndefMessage);
			return true;
		} 
		catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	
	public void onClick_InputText(View view)
	{
		Intent intent = new Intent(this, InputTextActivity.class);
		startActivityForResult(intent, 1); //ע�⣬������������requestCode��1
	}
	
	
	/*
	 * ������Ҫʵ�ֵĻ���Ҫ����NFC�����ع��˻���
	 * �����RunApplicationActivity��������Ϊ��߽���NFC��Ϣ�����ȼ�
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		if(mNfcAdapter != null){
			//�����RunApplicationActivity��������Ϊ���ȼ����������ܴ���NFC��ǩ�Ĵ��ڣ�Ҳ���ǽ�RunApplicationActivity������Ϊջ��
			mNfcAdapter.enableForegroundDispatch(this , mPendingIntent , null , null);
		}
	}
	
	/*
	 * ����������������ʱ�򣬾Ͳ�����������ö���
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		if(mNfcAdapter != null){
			mNfcAdapter.disableForegroundDispatch(this);
		}
	}
	
	
	

}
