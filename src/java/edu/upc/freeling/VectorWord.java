/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.7
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package edu.upc.freeling;

public class VectorWord {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected VectorWord(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(VectorWord obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        freelingJNI.delete_VectorWord(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public VectorWord() {
    this(freelingJNI.new_VectorWord__SWIG_0(), true);
  }

  public VectorWord(long n) {
    this(freelingJNI.new_VectorWord__SWIG_1(n), true);
  }

  public long size() {
    return freelingJNI.VectorWord_size(swigCPtr, this);
  }

  public long capacity() {
    return freelingJNI.VectorWord_capacity(swigCPtr, this);
  }

  public void reserve(long n) {
    freelingJNI.VectorWord_reserve(swigCPtr, this, n);
  }

  public boolean empty() {
    return freelingJNI.VectorWord_empty(swigCPtr, this);
  }

  public void clear() {
    freelingJNI.VectorWord_clear(swigCPtr, this);
  }

  public void pushBack(Word x) {
    freelingJNI.VectorWord_pushBack(swigCPtr, this, Word.getCPtr(x), x);
  }

  public Word get(int i) {
    return new Word(freelingJNI.VectorWord_get(swigCPtr, this, i), false);
  }

  public void set(int i, Word val) {
    freelingJNI.VectorWord_set(swigCPtr, this, i, Word.getCPtr(val), val);
  }

}
