package com.didihe1988.rscode.robinliew.rscore;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.didihe1988.rscode.math.GF256;
import com.didihe1988.rscode.robinliew.dealbytesinterface.RSErrorCorrectionImpl;

/**
 * 
 * modified by RobinLiew 2017.9
 *
 */
public class RSDecoder extends ReedSolomonCode{

	public RSDecoder() {
		super();
	}
    
	public byte[] decode(byte[] receive){
		//calculate number of decode units
		int unitCount=receive.length/this.n;
		//used to store encoded data
		byte[] buffer = new byte[unitCount*223];
		//To better manipulate byte,we use ByteBuffer bytes of own definition 
		ByteBuffer bytes=ByteBuffer.wrap(receive);
		byte[] unitBytes=new byte[this.n];
		
		int start=0;
		
		for(int i=0;i<unitCount;i++){
			bytes.get(unitBytes);
			//put the coded string into buffer
			byte[] temp=doDecode(unitBytes);
			System.arraycopy(doDecode(unitBytes), 0, buffer, start, 223);
			start=start+223;
		}
		return buffer;
	}
	

	public byte[] doDecode(byte[] receive) {
 		Polynomial receivePoly = new Polynomial(receive);
		// calculate syndrome
		Polynomial syndromes = calSyndromes(receivePoly);
		System.out.println("syndromes: "+syndromes);
		// BerlekampMassey algorithm
		Polynomial[] bmPolys = calBerlekampMassey(syndromes);
		Polynomial sigma = bmPolys[0];
		Polynomial omega = bmPolys[1];
		// chienSearch algorithm
		List[] chienLists = chienSearch(sigma);
		// forney 
		List<GF256> YList = forney(omega, (List<GF256>) chienLists[0]);
		List<Integer> jList = chienLists[1];
		
		//record the number of erroneous elements
		int errorCnt=jList.size();
		byte[] unRecoverData=new byte[223];
		if(errorCnt>30){
			RSErrorCorrectionImpl.isCanBeRecovered=false; 
		}
		
		GF256[] errors = new GF256[255];
		
		for (int i = 0; i < 255; i++) {
			if (jList.contains(i)) {
				errors[i] = YList.get(jList.lastIndexOf(i));
			} else {
				errors[i] = ZERO;
			}
		}
		Polynomial errorPoly = new Polynomial(errors);
		System.out.println("error Polynomial: "+errorPoly);
		Polynomial codeword = receivePoly.sub(errorPoly);
		return codeword.toValue(223);
	}

	public boolean verify(byte[] receive) {
		Polynomial receivePoly = new Polynomial(receive);
		return (receivePoly.mod(this.g)).isEmpty();
	}

	public Polynomial calSyndromes(Polynomial receive) {
		Polynomial syndromes = new Polynomial(this.s2);
		syndromes.pncoeff[0] = new GF256(0);
		for (int i = 1; i <= s2; i++) {
			syndromes.pncoeff[i] = receive.evaluate(this.ALPHA.pow(i));
		}
		return syndromes;
	}
	
	public List[] chienSearch(Polynomial sigma) {
		List[] results = new List[2];
		List<GF256> gfList = new ArrayList<GF256>();
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 1; i <= this.n; i++) {
			
			if (sigma.evaluate(ALPHA.pow(i)).equals(ZERO)) {
				
				gfList.add(ALPHA.pow(-i));
				list.add(this.n - i);
			}
		}
		results[0] = gfList;
		results[1] = list;
		return results;
	}

	public List<GF256> forney(Polynomial omega, List<GF256> XList) {
		int t = this.s2 / 2;
		List<GF256> YList = new ArrayList<GF256>();
		for (int i = 0; i < XList.size(); i++) {
			GF256 X = XList.get(i);
		
			GF256 Y = X.pow(t);
			Y = Y.mul(omega.evaluate(X.inverse()));
			Y = Y.mul(X.inverse());
			
			GF256 prod = new GF256(1);
			for (int j = 0; j < t; j++) {
				GF256 Xj = this.ZERO;
				if (i == j) {
					continue;
				}
				
				if (j < XList.size()) {
					Xj = XList.get(j);
				}
				prod = prod.mul(X.sub(Xj));
			}
			Y = Y.mul(prod.inverse());
			YList.add(Y);
		}
		return YList;
	}
	
	public Polynomial[] calBerlekampMassey(Polynomial s) {
		GF256[] syndromes = s.pncoeff;
		Polynomial[] sigmas = new Polynomial[this.s2 + 2];
		Polynomial[] omegas = new Polynomial[this.s2 + 2];
		int[] D = new int[this.s2 + 2];
		int[] JSubD = new int[this.s2 + 2];
		GF256[] deltas = new GF256[this.s2 + 2];

		
		sigmas[0] = new Polynomial(new GF256[] { ONE });
		sigmas[1] = new Polynomial(new GF256[] { ONE });
		omegas[0] = new Polynomial(new GF256[] { ZERO });
		omegas[1] = new Polynomial(new GF256[] { ONE });
		D[0] = D[1] = 0;
		JSubD[0] = -1;
		JSubD[1] = 0;
		deltas[0] = ONE;
		deltas[1] = syndromes[1];

		for (int j = 0; j < this.s2; j++) {
			// 计算修正项 Delta_j
			int degree = sigmas[j + 1].extent();
			GF256 mid_result = ZERO;
			for (int i = 1; i <= degree; i++) {
				mid_result = mid_result.add(syndromes[j + 1 - i]
						.mul(sigmas[j + 1].pncoeff[i]));
			}
			GF256 delta = syndromes[j + 1].add(mid_result);
			deltas[j + 1] = delta;

			if (delta.equals(ZERO)) {
				sigmas[j + 2] = sigmas[j + 1];
				omegas[j + 2] = omegas[j + 1];
				D[j + 2] = D[j + 1];
				JSubD[j + 2] = JSubD[j + 1] + 1;
			} else {
				/*
				 * delta[j]!=0
				 * JSubD[0]:-1-D(-1) JSubD[1]:0-D(0) JSubD[2]:1-D(1)依次类推
				 */
				int max = j;
				for (int i = j; i >= 0; i--) {
					if ((JSubD[i] > JSubD[max]) && (!deltas[i].equals(ZERO))) {
						max = i;
					}
				}

				GF256 tmp_test = deltas[j + 1].mul(deltas[max].pow(-1));
				Polynomial testPoly = new Polynomial(1);
				testPoly.pncoeff[1] = tmp_test;
				sigmas[j + 2] = sigmas[j + 1].sub(testPoly.mul(sigmas[max]));
				omegas[j + 2] = omegas[j + 1].sub(testPoly.mul(omegas[max]));
				D[j + 2] = sigmas[j + 2].extent();
				JSubD[j + 2] = j + 1 - D[j + 2];
			}
		}
	
		Polynomial[] results = new Polynomial[2];
		results[0] = sigmas[this.s2 + 1];
		results[1] = omegas[this.s2 + 1];
		return results;
	}

}
