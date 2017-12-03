package com.didihe1988.rscode.robinliew.rscore;

import com.didihe1988.rscode.math.GF256;


/**
 * modified by RobinLiew 2017.9.24
 */
public class Polynomial {
	//For comparison or intermediate operation
	private final static GF256 ZERO = new GF256(0);

	// Polynomial coefficient
	public GF256[] pncoeff;

	public Polynomial(GF256[] pncoeff) {
		this.pncoeff = pncoeff;
	}

	public Polynomial(byte[] bytes) {
		this.pncoeff = new GF256[bytes.length];
		int posValue;
		for (int i = 0; i < bytes.length; i++) {
			posValue=bytes[i]>=0?bytes[i]:bytes[i]+256;
			this.pncoeff[this.pncoeff.length - 1 - i] = new GF256(
					posValue);
			
		}
		
	}
	
	public Polynomial(int extent) {
		this.pncoeff = new GF256[extent+1];
		for (int i = 0; i < extent+1 ; i++) {
			this.pncoeff[i] = ZERO;
		}
	}

	public int length() {
		return this.pncoeff.length;
	}

	public int extent() {
		if (isEmpty()) {
			return 0;
		}
		int extent = length() - 1;
		for (int i = length() - 1; i >= 0; i--) {
			if (this.pncoeff[i].equals(ZERO)) {
				extent--;
			} else {
				break;
			}
		}
		return extent;
	}

	public Polynomial add(Polynomial b) {
		Polynomial a = this;
		Polynomial c = new Polynomial(Math.max(a.extent(), b.extent()));
		for (int i = 0; i <= a.extent(); i++) {
			c.pncoeff[i] = c.pncoeff[i].add(a.pncoeff[i]);
		}

		for (int i = 0; i <= b.extent(); i++) {
			c.pncoeff[i] = c.pncoeff[i].add(b.pncoeff[i]);
		}
		return c;
	}

	public Polynomial sub(Polynomial b) {
		Polynomial a = this;
		Polynomial c = new Polynomial(Math.max(a.extent(), b.extent()));
		for (int i = 0; i <= a.extent(); i++) {
			c.pncoeff[i] = c.pncoeff[i].add(a.pncoeff[i]);
		}

		for (int i = 0; i <= b.extent(); i++) {
			c.pncoeff[i] = c.pncoeff[i].sub(b.pncoeff[i]);
		}
		return c;
	}

	public Polynomial mul(Polynomial b) {
		Polynomial a = this;
		int inta=a.extent();
		int intb=b.extent();
		Polynomial c = new Polynomial(a.extent() + b.extent());
		for (int i = 0; i <= a.extent(); i++) {
			for (int j = 0; j <= b.extent(); j++) {
				c.pncoeff[i + j] = c.pncoeff[i + j]
						.add(a.pncoeff[i].mul(b.pncoeff[j]));
			}
		}
		return c;
	}

	public Polynomial mod(Polynomial b) {
		Polynomial remain = this;
		while (remain.extent() >= b.extent()) {
			
			int differ = remain.extent() - b.extent();
			GF256[] curCoefs = new GF256[b.pncoeff.length + differ];
			
			for (int i = 0; i < differ; i++) {
				curCoefs[i] = new GF256(0);
			}
			for (int i = differ, j = 0; i < curCoefs.length; i++, j++) {
				curCoefs[i] = b.pncoeff[j];
			}
		
			for (int i = 0; i < curCoefs.length; i++) {
				curCoefs[i] = curCoefs[i].mul(remain.pncoeff[remain
						.length() - 1]);
			}
			Polynomial subPoly = new Polynomial(curCoefs);
			remain = remain.sub(subPoly);
		}
		return remain;
	}

	public GF256 evaluate(GF256 value) {
		GF256 result = new GF256(0);
		for (int i = extent(); i >= 0; i--) {
			result = pncoeff[i].add(value.mul(result));
		}
		return result;
	}

	public boolean isEmpty() {
		for (int i = 0; i < length(); i++) {
			if (!this.pncoeff[i].equals(ZERO)) {
				return false;
			}
		}
		return true;
	}

	public byte[] toBytes() {
		
		int len=this.pncoeff.length+1;
		
		byte[] bytes = new byte[this.pncoeff.length];
		for (int i = 0; i < this.pncoeff.length; i++) {
			bytes[this.pncoeff.length - 1 - i] =  (byte) pncoeff[i].getValue();
		}
		return bytes;
	}

	public String toValue() {
		byte[] bytes = new byte[this.pncoeff.length];
		for (int i = 0; i < this.pncoeff.length; i++) {
			bytes[this.pncoeff.length - 1 - i] = (byte) pncoeff[i]
					.getValue();
		}
		return new String(bytes);
	}

	public byte[] toValue(int length) {
		byte[] bytes = new byte[length];
		int redundantLen = this.pncoeff.length - length;
			
		byte[] temp=new byte[223];
		for(int i=0;i<223-1;i++){
			temp[223-1-i]=(byte)pncoeff[i+redundantLen+1].getValue();
		}
		
		if(pncoeff[0].getValue()==0){
			return temp;
		}
	
		for (int i = 0; i < length; i++) {
			
			bytes[length -1 - i] = (byte) pncoeff[i + redundantLen]
					.getValue();

		}
		return bytes;
	}
	
	@Override
	public String toString() {
		if (extent() == 0)
			return "" + pncoeff[0].getValue();
		if (extent() == 1)
			return pncoeff[1].getValue() + "x + "
					+ pncoeff[0].getValue();
		String s = pncoeff[extent()].getValue() + "x^" + extent();
		for (int i = extent() - 1; i >= 0; i--) {
			if (pncoeff[i].getValue() == 0)
				continue;
			else if (pncoeff[i].getValue() > 0)
				s = s + " + " + (pncoeff[i].getValue());
			else if (pncoeff[i].getValue() < 0)
				s = s + " - " + (-pncoeff[i].getValue());
			if (i == 1)
				s = s + "x";
			else if (i > 1)
				s = s + "x^" + i;
		}
		return s;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Polynomial) {
			Polynomial tmp = (Polynomial) obj;
			if (tmp.length() == length()) {
				boolean isEqual = true;
				for (int i = 0; i < length(); i++) {
					if (!tmp.pncoeff[i].equals(this.pncoeff[i])) {
						isEqual = false;
					}
				}
				return isEqual;
			}
		}
		return false;
	}

}
