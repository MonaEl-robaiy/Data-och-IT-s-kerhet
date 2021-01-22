package com.company;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class RSA {
    private KeyPair privateKey;
    private KeyPair publicKey;

    RSA(String keyFileName, int keySize) {
        // Skapar nya nycklar
        generateKeys(keyFileName, keySize);
    }

    RSA(String keyFileName) {
        // Läsa in nycklar
        privateKey = readKey(keyFileName + "_priv.key");
        publicKey = readKey(keyFileName + "_pub.key");
    }

    //Spara nycklar
    public void saveKey(String fileName, KeyPair key) {
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(key);
            out.close();
            System.out.println("Saved key as " + fileName);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
    //Läsa in sparade nycklar
    public KeyPair readKey(String fileName) {
        KeyPair key = null;
        try {
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            key = (KeyPair) in.readObject();
            in.close();
            System.out.println("Read key from " + fileName);
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }
        return key;
    }
    //Generera nycklar
    public void generateKeys(String fileName, int bitLength) {
        SecureRandom rand = new SecureRandom();

        BigInteger p = new BigInteger(bitLength / 2, 100, rand);
        BigInteger q = new BigInteger(bitLength / 2, 100, rand);
        BigInteger n = p.multiply(q);
        BigInteger phiN = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger e = new BigInteger("3");
        while (phiN.gcd(e).intValue() > 1) {
            e = e.add(new BigInteger("2"));
        }

        BigInteger d = e.modInverse(phiN);
        publicKey = new KeyPair(e, n);
        privateKey = new KeyPair(d, n);
        saveKey(fileName + "_pub.key", publicKey);
        saveKey(fileName + "_priv.key", privateKey);

    }
    //kunna kryptera strängar
    public String encrypt(String message, KeyPair key) {
        return (new BigInteger(message.getBytes(StandardCharsets.UTF_8))).modPow(key.getKey(), key.getN()).toString();
    }
    //kunna dekryptera strängar
    public String decrypt(String message, KeyPair key) {
        String msg = new String(message.getBytes(StandardCharsets.UTF_8));
        return new String((new BigInteger(msg)).modPow(key.getKey(), key.getN()).toByteArray());
    }

    public KeyPair getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(KeyPair privateKey) {
        this.privateKey = privateKey;
    }

    public KeyPair getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(KeyPair publicKey) {
        this.publicKey = publicKey;
    }
}
