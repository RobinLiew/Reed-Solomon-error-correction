package com.didihe1988.rscode.robinliew.rscore;

import com.didihe1988.rscode.math.GF256;


/**
 * modified by RobinLiew 2017.9.24
 */
public class ReedSolomonCode {
	
	/**
	 * the length of Encoding input symbol 
	 */
    protected int n;
    /**
     * the length of Encoding output symbol
     */
    protected int k;
    /**
     * the length of Coding redundancy
     */
    protected int s2;
    /**
     * generate polynomial
     */
    protected Polynomial g;
    
    protected final static GF256 ALPHA=new GF256(3);
    
    protected final static GF256 ZERO=new GF256(0);
    protected final static GF256 ONE=new GF256(1);
    
    ReedSolomonCode() {
    	this(255, 223);
	}

    private ReedSolomonCode(int n,int k){
        this.n=n;
        this.k=k;
        this.s2=n-k;
       
        calGeneratorPolynome();
    }

    public Polynomial getGeneratorPolynome() {
        return g;
    }

    public int getK() {
        return k;
    }

    public int getN() {
        return n;
    }

    public int getS2(){
        return this.s2;
    }

    private void calGeneratorPolynome(){
        GF256 num=new GF256(1);
        this.g=new Polynomial(new GF256[]{num});
        for(int i=1;i<=this.s2;i++){
            Polynomial p=new Polynomial( new GF256[]{ALPHA.pow(i),num} );
            this.g=this.g.mul(p);
        }
    }
}
