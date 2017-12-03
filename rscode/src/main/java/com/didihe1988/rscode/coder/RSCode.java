package com.didihe1988.rscode.coder;

import com.didihe1988.rscode.math.GF256;
import com.didihe1988.rscode.math.Polynomial;


/**
 * Created by didihe1988 on 15-4-25.
 */
public class RSCode {
	//编码输入符号长度
    protected int n;
    //编码输出符号长度
    protected int k;
    //编码冗余长度 n-k=2t 也有资料称为2s
    protected int two_t;
    //生成多项式
    protected Polynomial g;

    //GF(2^8)的一个本原元
    protected final static GF256 ALPHA=new GF256(3);
    //用于比较或中间运算 不可用于赋值 赋值应该新分配空间
    protected final static GF256 ZERO=new GF256(0);
    protected final static GF256 ONE=new GF256(1);
    
	/**
	 * 构造函数 默认为RS(223,255)
	 */
    protected RSCode() {
		// TODO Auto-generated constructor stub
    	this(255, 223);
	}

    /**
     * 构造函数
     * @param n 编码输入符号长度
     * @param k 编码输出符号长度
     */
    private RSCode(int n,int k){
        this.n=n;
        this.k=k;
        this.two_t=n-k;
        //计算生成矩阵
        calGeneratorPolynomial();
    }

    public Polynomial getGeneratorPolynomial() {
        return g;
    }

    public int getK() {
        return k;
    }

    public int getN() {
        return n;
    }

    public int getTwoT(){
        return this.two_t;
    }

    /**
     *计算生成矩阵 
     */
    private void calGeneratorPolynomial(){
        GF256 num_1=new GF256(1);
        this.g=new Polynomial(new GF256[]{num_1});
        for(int i=1;i<=this.two_t;i++){
            Polynomial p=new Polynomial( new GF256[]{ALPHA.pow(i),num_1} );
            this.g=this.g.mul(p);
        }
    }
}
