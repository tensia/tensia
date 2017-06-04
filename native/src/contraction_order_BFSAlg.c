#include<stdint.h>

#include "jni/tensia_contraction_order_BFSAlg__.h"

#include "contraction_order_BFSAlg/ord.h"

JNIEXPORT jobject JNICALL Java_tensia_contraction_1order_BFSAlg_00024_ord(
  JNIEnv* env, jobject obj, jintArray j_tensors_sizes,
  jobjectArray j_contracted_dims_sizes
) {
    int tensor_cnt = (*env)->GetArrayLength(env, j_tensors_sizes);
    int* tensors_sizes = (int*)(*env)->GetIntArrayElements(env, j_tensors_sizes, 0);
    int** contracted_dims_sizes = malloc(tensor_cnt*sizeof(int*));
    for(int i=0; i < tensor_cnt; i++) {
      jintArray a =
        (jintArray)(*env)->GetObjectArrayElement(env, j_contracted_dims_sizes, i);
      contracted_dims_sizes[i] = (int*)(*env)->GetIntArrayElements(env, a, 0);
    }
    int* order = NULL;
    uint64_t cost = ord(tensors_sizes, contracted_dims_sizes, tensor_cnt, &order);
    for(int i=0; i<tensor_cnt; i++) {
      jintArray a =
        (jintArray)(*env)->GetObjectArrayElement(env, j_contracted_dims_sizes, i);
      (*env)->ReleaseIntArrayElements(env, a, (jint*)contracted_dims_sizes[i], 0);
      (*env)->DeleteLocalRef(env, a);
    }
    (*env)->ReleaseIntArrayElements(env, j_tensors_sizes, tensors_sizes, 0);
    int orderSize = 2*tensor_cnt-1;
    jintArray j_order = (*env)->NewIntArray(env, orderSize);
    jint *j_order_a = (*env)->GetIntArrayElements(env, j_order, 0);
    for(int i=0; i<orderSize; i++)
      j_order_a[i] = (jint)order[i];
    (*env)->ReleaseIntArrayElements(env, j_order, j_order_a, 0);
    jclass resClass =
      (*env)->FindClass(env, "tensia/contraction_order/NativeContractionOrderResult");
    jmethodID resConstructor =
      (*env)->GetMethodID(env, resClass, "<init>", "(J[I)V");
    jobject res=(*env)->NewObject(env, resClass, resConstructor, cost, j_order);
    return res;
  }
