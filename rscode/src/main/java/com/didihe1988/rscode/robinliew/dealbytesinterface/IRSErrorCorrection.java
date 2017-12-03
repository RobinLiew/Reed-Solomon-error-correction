package com.didihe1988.rscode.robinliew.dealbytesinterface;
/**
 * RS(255,223)Error correction algorithm interface
 * @author RobinLiew  2017.9
 *
 */
public interface IRSErrorCorrection {
	/**
	 * 编码
	 * @param data  Raw data to be checked, transfer packets for file mode(待校验的原始数据，为文件模式传输包 )            
	 * @return Return check data(返回校验数据)
	 */
	public byte[] rs_encode(byte[] data);
	/**
	 * 解码
	 * @param recover data after correcting error recovery(纠错恢复后的数据)
	 * @param rsDate  error correction check data(纠错校验数据)
	 * @return  return 0 indicates error correction success and writes error recovery data to recover; 
	 * return 1 indicates error correction failure(返回0表示纠错成功，并将纠错恢复的数据写入recover；返回1表示纠错失败)
	 */
	public int rs_decode(byte[] recover,byte[] rsData);
}
