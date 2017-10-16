#include<stdint.h>

#include "jni/pl_edu_agh_tensia_contraction_order_BFSOrderFinder__.h"

#include "contraction_order_finder_BFS/ord.h"

#define RESULT_CLASS "pl/edu/agh/tensia/contraction/order/NativeOrderFinderResult"

JNIEXPORT jobject JNICALL Java_pl_edu_agh_tensia_contraction_order_BFSOrderFinder_00024_ord(
  JNIEnv* env, jobject obj, jintArray j_tensors_sizes,
  jobjectArray j_contracted_dims_sizes, jbooleanArray j_tensors_locks
) {
    int tensor_cnt = (*env)->GetArrayLength(env, j_tensors_sizes);
    int* tensors_sizes = (int*)(*env)->GetIntArrayElements(env, j_tensors_sizes, 0);
    int** contracted_dims_sizes = malloc(tensor_cnt*sizeof(int*));
    for(int i=0; i < tensor_cnt; i++) {
      jintArray a =
        (jintArray)(*env)->GetObjectArrayElement(env, j_contracted_dims_sizes, i);
      contracted_dims_sizes[i] = (int*)(*env)->GetIntArrayElements(env, a, 0);
    }
    unsigned char* tensors_locks = (unsigned char*)(*env)->GetBooleanArrayElements(env, j_tensors_locks, 0);
    int* order = NULL;
    uint64_t cost =
      ord(tensors_sizes, contracted_dims_sizes, tensors_locks, tensor_cnt, &order);
    for(int i=0; i<tensor_cnt; i++) {
      jintArray a =
        (jintArray)(*env)->GetObjectArrayElement(env, j_contracted_dims_sizes, i);
      (*env)->ReleaseIntArrayElements(env, a, (jint*)contracted_dims_sizes[i], 0);
      (*env)->DeleteLocalRef(env, a);
    }
    (*env)->ReleaseIntArrayElements(env, j_tensors_sizes, tensors_sizes, 0);
    (*env)->ReleaseBooleanArrayElements(env, j_tensors_locks, tensors_locks, 0);

    int orderSize = 2*tensor_cnt-1;
    jintArray j_order = (*env)->NewIntArray(env, orderSize);
    jint *j_order_a = (*env)->GetIntArrayElements(env, j_order, 0);
    for(int i=0; i<orderSize; i++)
      j_order_a[i] = (jint)order[i];
    (*env)->ReleaseIntArrayElements(env, j_order, j_order_a, 0);
    jclass resClass =
      (*env)->FindClass(env, RESULT_CLASS);
    jmethodID resConstructor =
      (*env)->GetMethodID(env, resClass, "<init>", "(J[I)V");
    jobject res=(*env)->NewObject(env, resClass, resConstructor, cost, j_order);
    return res;
  }
