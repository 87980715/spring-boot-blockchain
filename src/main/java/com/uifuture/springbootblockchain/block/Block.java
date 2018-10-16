/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.block;

import com.uifuture.springbootblockchain.pow.PowResult;
import com.uifuture.springbootblockchain.pow.ProofOfWork;
import com.uifuture.springbootblockchain.transaction.MerkleTree;
import com.uifuture.springbootblockchain.transaction.Transaction;
import com.uifuture.springbootblockchain.util.ByteUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigInteger;

/**
 * 区块
 * 暂定区块体积为4M
 * @author chenhx
 * @version Block.java, v 0.1 2018-10-11 下午 9:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Block {
    /**
     * 区块hash值
     */
    private String hash;
    /**
     * 前一个区块的hash值
     */
    private String prevBlockHash;
    /**
     * 交易信息
     */
    private Transaction[] transactions;
    /**
     * 区块创建时间(单位:毫秒)
     */
    private long timeStamp;
    /**
     * 工作量证明计数器
     */
    private BigInteger nonce;
    /**
     * 区块高度
     */
    private int height;

    /**
     * <p> 创建创世区块 </p>
     *
     * @param coinbase
     * @return
     */
    public static Block newGenesisBlock(Transaction coinbase) {
        return Block.newBlock(ByteUtils.ZERO_HASH, new Transaction[]{coinbase}, 0);
    }

    /**
     * <p> 创建新区块 </p>
     *
     * @param previousHash
     * @param transactions
     * @return
     */
    public static Block newBlock(String previousHash, Transaction[] transactions, int height) {
        Block block = new Block("", previousHash, transactions, System.currentTimeMillis()
                , new BigInteger("0"), height);
        ProofOfWork pow = ProofOfWork.newProofOfWork(block);
        //计算
        PowResult powResult = pow.run();
        block.setHash(powResult.getHash());
        block.setNonce(powResult.getNonce());
        return block;
    }

    /**
     * 对区块中的交易信息进行Hash计算
     *
     * @return
     */
    public byte[] hashTransaction() {
        byte[][] txIdArrays = new byte[this.getTransactions().length][];
        for (int i = 0; i < this.getTransactions().length; i++) {
            txIdArrays[i] = this.getTransactions()[i].hash();
        }
        return new MerkleTree(txIdArrays).getRoot().getHash();
    }
}
