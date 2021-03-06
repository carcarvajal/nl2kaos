/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.7
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package edu.upc.freeling;

public class PreorderIteratorDepnode extends GenericIteratorDepnode {
  private long swigCPtr;

  protected PreorderIteratorDepnode(long cPtr, boolean cMemoryOwn) {
    super(freelingJNI.PreorderIteratorDepnode_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(PreorderIteratorDepnode obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        freelingJNI.delete_PreorderIteratorDepnode(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public PreorderIteratorDepnode() {
    this(freelingJNI.new_PreorderIteratorDepnode__SWIG_0(), true);
  }

  public PreorderIteratorDepnode(PreorderIteratorDepnode arg0) {
    this(freelingJNI.new_PreorderIteratorDepnode__SWIG_1(PreorderIteratorDepnode.getCPtr(arg0), arg0), true);
  }

  public PreorderIteratorDepnode(TreeDepnode arg0) {
    this(freelingJNI.new_PreorderIteratorDepnode__SWIG_2(TreeDepnode.getCPtr(arg0), arg0), true);
  }

  public PreorderIteratorDepnode(SiblingIteratorDepnode arg0) {
    this(freelingJNI.new_PreorderIteratorDepnode__SWIG_3(SiblingIteratorDepnode.getCPtr(arg0), arg0), true);
  }

  public PreorderIteratorDepnode operator_increment() {
    return new PreorderIteratorDepnode(freelingJNI.PreorderIteratorDepnode_operator_increment__SWIG_0(swigCPtr, this), false);
  }

  public PreorderIteratorDepnode operator_decrement() {
    return new PreorderIteratorDepnode(freelingJNI.PreorderIteratorDepnode_operator_decrement__SWIG_0(swigCPtr, this), false);
  }

  public PreorderIteratorDepnode operator_increment(int arg0) {
    return new PreorderIteratorDepnode(freelingJNI.PreorderIteratorDepnode_operator_increment__SWIG_1(swigCPtr, this, arg0), true);
  }

  public PreorderIteratorDepnode operator_decrement(int arg0) {
    return new PreorderIteratorDepnode(freelingJNI.PreorderIteratorDepnode_operator_decrement__SWIG_1(swigCPtr, this, arg0), true);
  }

}
