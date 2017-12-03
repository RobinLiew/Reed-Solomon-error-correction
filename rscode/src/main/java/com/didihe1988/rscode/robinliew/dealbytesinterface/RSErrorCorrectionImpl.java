package com.didihe1988.rscode.robinliew.dealbytesinterface;


import java.util.Random;

import com.didihe1988.rscode.robinliew.rscore.RSDecoder;
import com.didihe1988.rscode.robinliew.rscore.RSEncoder;

/**
 * RS(255,223)纠错算法实现
 * @author RobinLiew 2017.9.26
 *
 */
public class RSErrorCorrectionImpl implements IRSErrorCorrection {

	
	private RSEncoder encoder=new RSEncoder();

	private RSDecoder decoder=new RSDecoder();
	
	public static Boolean isCanBeRecovered=true;
	
	private byte[] temp=new byte[233];
	
	private ByteBuffer buffer=new ByteBuffer() ;
	
	private byte[] enTemp=new byte[255];
	
	public byte[] rs_encode(byte[] data) {
		return encoder.encode(data);
	}

	public int rs_decode(byte[] recover, byte[] rsData) {
		byte[] result = null;
		result=decoder.decode(rsData);
		
		if(!isCanBeRecovered){
			return 1;
		}
		System.arraycopy(result, 0, recover, 0, result.length);
		return 0;
	}
	
	public static void main(String[] args){
		
		byte[] src=new byte[223];
		for(int i=0;i<223;i++){
			src[i]=(byte) new Random().nextInt(255);
		}
		byte[] srcdouble=new byte[446];
		for(int i=0;i<2;i++){
			System.arraycopy(src, 0, srcdouble, i*223, 223);
		}
		
		IRSErrorCorrection error = new RSErrorCorrectionImpl();
		byte[] en_data=error.rs_encode(srcdouble);
		
		//Deliberately mistaken the values of several data(故意弄错几个数据的值)
		en_data[0]=0;
		en_data[1]=4;
		en_data[3]=0;
		//byte[] recover = new byte[src.length];
		byte[] recover = new byte[srcdouble.length];
		int flag = error.rs_decode(recover,en_data);
		
        
        System.out.println("completion of the test！！！");
	}
	
}
