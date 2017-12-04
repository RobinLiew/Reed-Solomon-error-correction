# I modify part source code and add the interface and implemention of dealing with byte array 
I read the source code of this great job, trying to modify a small part of the code. And on the basis of this excellent work, we add the interface and implementation of processing byte array, so that we can make the code applied to more scenes such as network data transmission, data storage and so on.

我阅读了这个很棒的工作的源码，试着修改了小部分代码。并且在这个卓越工作的基础上，增加了处理字节数组的接口和实现，这样我们就能使该代码应用于网络数据传输、数据存储等更多的场景。

# example 
  
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

# Reed-Solomon-error-correction
Reed–Solomon error correction in java, providing RS(255,223) encode and decode method.

#Usage
encode the string before net transmission or local storage:
  

    RSEncoder encoder=new RSEncoder();
    byte[] bytes=encoder.encode(string);

decode while receiving from network or reading from the disk

    String result=decoder.decode(receive);
#Example

     public static void simulateTransmission(String message){
    	//encode
    	System.out.println("String ready to translate：");
    	System.out.println(message);
    	System.out.println("--------------------------");
    	RSEncoder encoder=new RSEncoder();
    	byte[] bytes=encoder.encode(message);
        RSDecoder decoder=new RSDecoder();
        //modify several bytes to simulate errors in transmission
        byte[] receive=bytes;
        receive[0]='a';
        receive[300]='a';
        receive[301]='b';
        //decode
        String result=decoder.decode(receive);
        System.out.println("--------------------------");
        System.out.println("String after error correction： ");
        System.out.println(result);
        System.out.println("--------------------------");
    	System.out.println("Is result the same as message？： "+message.equals(result));
    }
    
 ![](https://github.com/didihe1988/Reed-Solomon-error-correction/raw/master/rscode/screenshot/demo.png)

#Document

 - **Class RSEncoder**

**byte[] encode(String message)**
Encode the given string with RS(255,223).Return encoded byte array.<br>
First，invoke *enlargeMessage()* to make sure message string can be divided into several encoding unit.Then,for each encoding unit,invoke *doEncode()*.At last,merge the codeword polynomial ,and turn it to byte array.<br>

**Polynomial doEncode(String unit)**
Conduct encoding for each encoding unit.Return codeword polynomial.The length of an encoding unit is 223.After encoding,32 byte redundancy is added into the unit.<br>

**String enlargeMessage(String message)**
Enlarge message string to integer multiples of 223, by padding zeros at the end of string. <br>

- **Class RSDecoder**

**String decode(byte[] receive)**
Decode the given byte array ,correct the error and return raw stirng.<br>
For each 255-length decoding unit,invoke the *doDecode()* and use *StringBuffer* to store the decoded string.After that,turn StirngBuffer to string,and trim the string(Because raw string has been padded by zeros after encoding).<br>
**String doDecode(byte[] receive)**
Conduct decoding for each decoding unit.Return decoded string.<br>
Procedure:<br>
(1)*calSyndromes(Polynomial receive)*：Calculate syndromes polynomial.<br>
(2)*calBerlekampMassey(Polynomial s)*:Run BerlekampMassey algorithm,input syndromes polynomial and return array of sigma and omega.<br>
(3)*chienSearch(Polynomial sigma)*:Input error-locator polynomial--sigma,return the roots of sigma.<br>
(4)*forney()*:Input error-value polynomial--omega and the corresponding error location,return the coefficient of error polynomial.<br>
(5)generate error polynomial,codeword = receive - error polynomial.<br>








