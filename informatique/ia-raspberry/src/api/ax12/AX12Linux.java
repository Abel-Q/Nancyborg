/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.40
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package api.ax12;

public class AX12Linux extends AX12Base {
  private long swigCPtr;

  protected AX12Linux(long cPtr, boolean cMemoryOwn) {
    super(AX12JNI.SWIGAX12LinuxUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(AX12Linux obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        AX12JNI.delete_AX12Linux(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public AX12Linux(String devpath, int id, int baud) {
    this(AX12JNI.new_AX12Linux__SWIG_0(devpath, id, baud), true);
  }

  public AX12Linux(String devpath, int id) {
    this(AX12JNI.new_AX12Linux__SWIG_1(devpath, id), true);
  }

  public int getSysError() {
    return AX12JNI.AX12Linux_getSysError(swigCPtr, this);
  }

  public void setCurrentBaud(int new_baud) {
    AX12JNI.AX12Linux_setCurrentBaud(swigCPtr, this, new_baud);
  }

}