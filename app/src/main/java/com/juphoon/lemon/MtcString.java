/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.juphoon.lemon;

public class MtcString {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected MtcString(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(MtcString obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        MtcVerJNI.delete_MtcString(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setValue(String value) {
    MtcVerJNI.MtcString_value_set(swigCPtr, this, value);
  }

  public String getValue() {
    return MtcVerJNI.MtcString_value_get(swigCPtr, this);
  }

  public MtcString() {
    this(MtcVerJNI.new_MtcString(), true);
  }

}
