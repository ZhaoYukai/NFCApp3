package com.example.nfcapp3;

import java.util.Arrays;

import android.nfc.NdefRecord;

public class TextRecord {
	//�����������ı�������������У�ֻ�ܷ���һ��
	private final String mText;
	
	
	//���췽��˽�л�
	private TextRecord(String text){
		mText = text;
	}
	
	
	public String getText(){
		return mText;
	}
	
	/*
	 * ����Ǻ��ķ����������ɾ�̬��
	 * �ж�NdefRecord�����д洢���ǲ���NDEF�ı���ʽ����
	 */
	public static TextRecord parse(NdefRecord ndefRecord){
		//��֤TNF���������TNF_WELL_KNOWN�Ĳźϸ�
		if(ndefRecord.getTnf() != NdefRecord.TNF_WELL_KNOWN){
			return null;
		}
		
		//��֤�ɱ�ĳ������ͣ�����õ���RTD_TEXT����
		//�������������������Ƚϣ������Ҫ�õ�Arrays���equals()����
		if( ! Arrays.equals(ndefRecord.getType() , NdefRecord.RTD_TEXT)){
			return null;
		}
		
		//������������֤�����֮������Ϳ��Կ�ʼ��ʽ�Ľ���������
		try {
			//���Ȼ��NdefRecord�����д�����е��ֽ���
			byte[] payload = ndefRecord.getPayload();
			
			//�������ж�״̬�ֽ���λ���ı������ʽ������UTF-8�Ļ���UTF-16��
			//0x80ת���ɶ����ƾ���1000 0000����ô����payload[0] "&"�Ľ��������ֻ������״̬�ֽ��е���λ
			//�����λ�����0����ô����UTF-8�ģ������λ�����1����ô����UTF-16��
			String textEncoding = ( (payload[0] & 0x80) == 0 ? "UTF-8" : "UTF-16" );
			
			//���Ա���ĳ��ȣ�ռ�õ��ֽڸ�����
			//����״̬�ֽڹ�8λ����һλ�Ǳ�־UTF�ģ��ڶ�λ��ֵ֪��0Ҳռ����һλ����ô�ӵ�3λ����8λ����������Ҫ��
			//��ôд����Ӧ�Ķ����ƣ�����0011 1111��ת����ʮ�����ƾ���0x3F
			int languageCodeLength = (payload[0] & 0x3F);
			
			//���ݸոջ�õ����Ա��볤�Ȼ�����Ա���
			//����֮���Դ�1��ʼ������Ϊ��0����״̬�ֽڡ����һ������"US-ASCII"��Ĭ�ϵ����Ա���
			String languageCode = new String(payload , 1 , languageCodeLength , "US-ASCII");
			
			//������������Ҫ��һ�������ֽ����л��������Ҫ���Ƕ���Ϣ
			String text = new String(payload , languageCodeLength + 1 , payload.length - languageCodeLength - 1 , textEncoding);
			
			//������ɣ��ѽ����������ı�����
			return (new TextRecord(text));
		} 
		catch (Exception e) {
			throw new IllegalArgumentException();
		}

	}//parse()��������
	
	
	
	
	
	
	
}
