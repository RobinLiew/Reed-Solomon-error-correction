package com.didihe1988.rscode.robinliew.rscore;

import java.nio.ByteBuffer;

/**
 * 
 * modified by RobinLiew 2017.9
 *
 */
public class RSEncoder extends ReedSolomonCode{

    public RSEncoder() {
    	super();
	}
    
 
    public byte[] encode(byte[] message){
    	//Fill in the input message
    	byte[] enlargedMessage=enlargeBytes(message);
    	//calculating the number of units encoded by RS: 223 bytes as a unit
    	int unitCount=enlargedMessage.length/this.k;
    	ByteBuffer buffer=ByteBuffer.allocate(unitCount*this.n);
    	
    	int start=0;
    	
    	for(int i=0;i<unitCount;i++){
    		//encode one by one
    		byte[] temp=new byte[223];
    		
    		System.arraycopy(enlargedMessage, start, temp, 0, 223);
    		start=start+223;
    		
    		Polynomial poly=doEncode(temp);
    		
    		//for check
    		byte[] forcheck=poly.toBytes();
    		byte[] enTemp=new byte[255];
    		if(forcheck.length!=255){
    			System.arraycopy(forcheck, 0, enTemp, 0, forcheck.length);
    		}else{
    			System.arraycopy(forcheck, 0, enTemp, 0, 255);
    		}
    		
    		buffer.put(enTemp);
    	}
    	
    	//for check
    	byte[] endata=buffer.array();
    	
    	return buffer.array();
    }
    
    
    private Polynomial doEncode(byte[] units){
        
        Polynomial pn=new Polynomial(units);
        
        Polynomial s2=new Polynomial(this.s2);
        s2.pncoeff[s2.length()-1]=ONE;
        
        Polynomial mprime=pn.mul(s2);
        
        Polynomial b=mprime.mod(this.g);
       
        Polynomial c=mprime.sub(b);
       
        return c;
    }  
    
    /**
     * @author RobinLiew  complement byte array
     * @param message
     * @return byte[]
     */
    public  byte[] enlargeBytes(byte[] srcData){
    	/*
    	 * for example,if the length of message is 400，
    	 * the actul length should be 223＊2=446 and we should add 46 zeros in the back
    	 */
    	int rawLen=srcData.length;
    	int unitCount=(int)Math.ceil(rawLen/(double)this.k);
    	
		int len=this.k*unitCount;
		
		byte[] bytes=new byte[len];
        if(len!=rawLen){
        	byte[] rawBytes=srcData;
    		
    		for(int i=0;i<rawBytes.length;i++){
    			bytes[i]=rawBytes[i];
    		}
    		for(int i=rawBytes.length;i<len;i++){
    			bytes[i]=0;
    		}
    		
        }else{
        	byte[] rawBytes=srcData;
    		
    		for(int i=0;i<rawBytes.length;i++){
    			bytes[i]=rawBytes[i];
    		}
        }
        return bytes;
    }
}
