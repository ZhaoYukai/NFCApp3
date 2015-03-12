package com.example.nfcapp3;

import java.util.Arrays;

import android.nfc.NdefRecord;

public class TextRecord {
	//解析出来的文本放在这个变量中，只能放置一次
	private final String mText;
	
	
	//构造方法私有化
	private TextRecord(String text){
		mText = text;
	}
	
	
	public String getText(){
		return mText;
	}
	
	/*
	 * 这个是核心方法，声明成静态的
	 * 判断NdefRecord对象中存储的是不是NDEF文本格式数据
	 */
	public static TextRecord parse(NdefRecord ndefRecord){
		//验证TNF，必须得是TNF_WELL_KNOWN的才合格
		if(ndefRecord.getTnf() != NdefRecord.TNF_WELL_KNOWN){
			return null;
		}
		
		//验证可变的长度类型，必须得等于RTD_TEXT才行
		//但是这个是两个数组相比较，因此需要用到Arrays类的equals()方法
		if( ! Arrays.equals(ndefRecord.getType() , NdefRecord.RTD_TEXT)){
			return null;
		}
		
		//当上面两步验证都完成之后，下面就可以开始正式的解析工作了
		try {
			//首先获得NdefRecord对象中存的所有的字节流
			byte[] payload = ndefRecord.getPayload();
			
			//接下来判断状态字节首位的文本编码格式到底是UTF-8的还是UTF-16的
			//0x80转化成二进制就是1000 0000，那么它与payload[0] "&"的结果，就是只保留了状态字节中的首位
			//这个首位如果是0，那么就是UTF-8的；这个首位如果是1，那么就是UTF-16的
			String textEncoding = ( (payload[0] & 0x80) == 0 ? "UTF-8" : "UTF-16" );
			
			//语言编码的长度（占用的字节个数）
			//由于状态字节共8位，第一位是标志UTF的，第二位已知值是0也占用了一位，那么从第3位到第8位就是我们想要的
			//那么写出对应的二进制，就是0011 1111，转化成十六进制就是0x3F
			int languageCodeLength = (payload[0] & 0x3F);
			
			//根据刚刚获得的语言编码长度获得语言编码
			//这里之所以从1开始，是因为第0个是状态字节。最后一个参数"US-ASCII"是默认的语言编码
			String languageCode = new String(payload , 1 , languageCodeLength , "US-ASCII");
			
			//接下来是最重要的一部，从字节流中获得我们想要的那段信息
			String text = new String(payload , languageCodeLength + 1 , payload.length - languageCodeLength - 1 , textEncoding);
			
			//解析完成，把解析出来的文本返回
			return (new TextRecord(text));
		} 
		catch (Exception e) {
			throw new IllegalArgumentException();
		}

	}//parse()方法结束
	
	
	
	
	
	
	
}
