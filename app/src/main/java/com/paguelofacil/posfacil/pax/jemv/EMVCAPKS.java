package com.paguelofacil.posfacil.pax.jemv;

/**
 * Created by administrador on 21/6/15.
 */
public class EMVCAPKS {
    byte[] RID=new byte[7];
    byte index;
    byte exponente;
    byte longitud;
    byte[] expiryDate=new byte[3];
    byte[] effectiveDate=new byte[3];
    byte[] secureHash=new byte[20];
    byte[] key1=new byte[32];
    byte[] key2=new byte[32];
    byte[] key3=new byte[32];
    byte[] key4=new byte[32];
    byte[] key5=new byte[32];
    byte[] key6=new byte[32];
    byte[] key7=new byte[32];
    byte[] key8=new byte[32];

    String dmsTrace;

    public EMVCAPKS(byte[] RID, byte index, byte exponente, byte longitud, byte[] expiryDate, byte[] effectiveDate, byte[] secureHash, byte[] key1, byte[] key2, byte[] key3, byte[] key4, byte[] key5, byte[] key6, byte[] key7, byte[] key8) {
        this.RID = RID;
        this.index = index;
        this.exponente = exponente;
        this.longitud = longitud;
        this.expiryDate = expiryDate;
        this.effectiveDate = effectiveDate;
        this.secureHash = secureHash;
        this.key1 = key1;
        this.key2 = key2;
        this.key3 = key3;
        this.key4 = key4;
        this.key5 = key5;
        this.key6 = key6;
        this.key7 = key7;
        this.key8 = key8;
    }

    public byte[] getRID() {
        return RID;
    }

    public void setRID(byte[] RID) {
        this.RID = RID;
    }

    public byte getIndex() {
        return index;
    }

    public void setIndex(byte index) {
        this.index = index;
    }

    public byte getExponente() {
        return exponente;
    }

    public void setExponente(byte exponente) {
        this.exponente = exponente;
    }

    public byte getLongitud() {
        return longitud;
    }

    public void setLongitud(byte longitud) {
        this.longitud = longitud;
    }

    public byte[] getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(byte[] expiryDate) {
        this.expiryDate = expiryDate;
    }

    public byte[] getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(byte[] effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public byte[] getSecureHash() {
        return secureHash;
    }

    public void setSecureHash(byte[] secureHash) {
        this.secureHash = secureHash;
    }

    public byte[] getKey1() {
        return key1;
    }

    public void setKey1(byte[] key1) {
        this.key1 = key1;
    }

    public byte[] getKey2() {
        return key2;
    }

    public void setKey2(byte[] key2) {
        this.key2 = key2;
    }

    public byte[] getKey3() {
        return key3;
    }

    public void setKey3(byte[] key3) {
        this.key3 = key3;
    }

    public byte[] getKey4() {
        return key4;
    }

    public void setKey4(byte[] key4) {
        this.key4 = key4;
    }

    public byte[] getKey5() {
        return key5;
    }

    public void setKey5(byte[] key5) {
        this.key5 = key5;
    }

    public byte[] getKey6() {
        return key6;
    }

    public void setKey6(byte[] key6) {
        this.key6 = key6;
    }

    public byte[] getKey7() {
        return key7;
    }

    public void setKey7(byte[] key7) {
        this.key7 = key7;
    }

    public byte[] getKey8() {
        return key8;
    }

    public void setKey8(byte[] key8) {
        this.key8 = key8;
    }

    public String getDmsTrace()
    {
        return dmsTrace;
    }

    public void setDmsTrace(String dmsTrace) {
        this.dmsTrace = dmsTrace;
    }
}

