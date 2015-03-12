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
		//一单截获NFC的消息，就调用PendingIntent来激活窗口
		mPendingIntent = PendingIntent.getActivity(this , 0 , new Intent(this , getClass()) , 0);
	}
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		if(mText == null){//mText中是空着的话，就从NFC标签中读数据
			Intent myIntent = new Intent(this , ShowNFCTagContentActivity.class);
			myIntent.putExtras(intent);
			myIntent.setAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
			startActivity(myIntent);
		}
		else{ //如果mText中有数据的话，就往NFC标签中写数据
			//先获得Tag
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			NdefMessage ndefMessage = new NdefMessage( new NdefRecord[]{createTextRecord(mText)} );
			if(writeTag(ndefMessage, tag)){
				Toast.makeText(this , "已成功写入内容" , Toast.LENGTH_SHORT).show();
			}
			else{
				Toast.makeText(this , "写入内容失败，请检查NFC标签的容量是否充足" , Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 1 && resultCode == 1){ //我们之前设置过让二者都为1，所以这里可以理解为是对暗号
			mText = data.getStringExtra("text"); //根据键值对取出编辑框中输入的数据
			mInputText.setText(mText);
		}
	}
	
	
	
	/*
	 * TextRecord类中实现的最关键方法是把NFC标签中的NDEF数据字节流解析成纯文本，那么显然，
	 * 如果我们要把纯文本写入NFC，也得把这个纯文本解析成NDEF字节流，下面这个方法就是做这个的
	 * 把纯文本按照一定的格式，存储到NdefRecord的对象中
	 */
	public NdefRecord createTextRecord(String text){
		//获得生成语言编码的数组
		byte[] langBytes = Locale.CHINA.getLanguage().getBytes(Charset.forName("US-ASCII"));
		//指定字符集变量是UTF-8
		Charset utfEncoding = Charset.forName("UTF-8");
		//给传进来的纯文本设置字符集
		byte[] textBytes = text.getBytes(utfEncoding);
		//由于NDEF的状态字节最高位，如果为0就是UTF-8的，所以先声明一个变量
		int utfBit = 0;
		//状态字节
		char status = (char) (utfBit + langBytes.length);
		//构造NDEF格式，分成3段。第一段是状态字节(1个字节)；第二段是语言编码；第三段是文本。
		//数组方括号里面是为了确定大小，所以分别传3段的各自长度
		byte[] data = new byte[1 + langBytes.length + textBytes.length];
		data[0] = (byte) status; //设置状态码
		//第一个参数是源数组；第二个参数是从源数组哪里开始拷贝，从0开始就是从最头上；
		//第三个参数是目的数组；第四个参数是从目的数组的哪里开始接收，从1开始表示从下标为1那里开始，因为第0个是状态码
		//第五个参数是从源数组中拷贝多大的东西。为源数组的length的话就是把源数组全都拷到目的数组里面
		System.arraycopy(langBytes , 0 , data , 1 , langBytes.length);
		//最后再把文本拷到第3段
		System.arraycopy(textBytes , 0 , data , 1 + langBytes.length , textBytes.length);
		//到这里，NDEF格式的数据就构造完毕了，现在要把它封装到NdefRecord对象中
		NdefRecord ndefRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN , NdefRecord.RTD_TEXT , new byte[0] , data);
		return ndefRecord;
	}
	
	
	/*
	 * 把NDEF对象写到NFC标签里
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
		startActivityForResult(intent, 1); //注意，这里我们设置requestCode是1
	}
	
	
	/*
	 * 这里所要实现的机制要高于NFC的三重过滤机制
	 * 把这个RunApplicationActivity窗口设置为最高接受NFC消息的优先级
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		if(mNfcAdapter != null){
			//把这个RunApplicationActivity窗口设置为优先级高于所有能处理NFC标签的窗口，也就是将RunApplicationActivity窗口置为栈顶
			mNfcAdapter.enableForegroundDispatch(this , mPendingIntent , null , null);
		}
	}
	
	/*
	 * 当不想用这个程序的时候，就不把这个窗口置顶了
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		if(mNfcAdapter != null){
			mNfcAdapter.disableForegroundDispatch(this);
		}
	}
	
	
	

}
