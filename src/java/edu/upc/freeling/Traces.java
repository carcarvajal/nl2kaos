/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.7
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package edu.upc.freeling;

public class Traces {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected Traces(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(Traces obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        freelingJNI.delete_Traces(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public static void setTraceLevel(int value) {
    freelingJNI.Traces_TraceLevel_set(value);
  }

  public static int getTraceLevel() {
    return freelingJNI.Traces_TraceLevel_get();
  }

  public static void setTraceModule(long value) {
    freelingJNI.Traces_TraceModule_set(value);
  }

  public static long getTraceModule() {
    return freelingJNI.Traces_TraceModule_get();
  }

  public Traces() {
    this(freelingJNI.new_Traces(), true);
  }

}