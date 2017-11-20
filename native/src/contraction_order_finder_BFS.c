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
    int locked_cnt = 0;
      for(int i=0; i<tensor_cnt;i++)
        if(tensors_locks[i])
          locked_cnt++;
    int** order = NULL;
    uint64_t cost =
      ord(tensors_sizes, contracted_dims_sizes, tensors_locks, tensor_cnt, &order);
    debug("ord finished\n");
    for(int i=0; i<tensor_cnt; i++) {
      jintArray a =
        (jintArray)(*env)->GetObjectArrayElement(env, j_contracted_dims_sizes, i);
      (*env)->ReleaseIntArrayElements(env, a, (jint*)contracted_dims_sizes[i], 0);
      (*env)->DeleteLocalRef(env, a);
    }
    (*env)->ReleaseIntArrayElements(env, j_tensors_sizes, tensors_sizes, 0);
    (*env)->ReleaseBooleanArrayElements(env, j_tensors_locks, tensors_locks, 0);
    debug("release finished\n");

    jclass cls = (*env)->FindClass(env, "[I");
    jobjectArray j_order = (*env)->NewObjectArray(env, MAX(1, locked_cnt), cls, NULL);

    for (int i = 0; i < MAX(1, locked_cnt); i++) {
      int size = order[i][0];
      jintArray j_inner_order = (*env)->NewIntArray(env, size);
      (*env)->SetIntArrayRegion(env, j_inner_order, 0, size, order[i]+1);
      // set inner's values
      (*env)->SetObjectArrayElement(env, j_order, i, j_inner_order);
      (*env)->DeleteLocalRef(env, j_inner_order);
    }
    debug("out array created\n");
    jclass resClass = (*env)->FindClass(env, RESULT_CLASS);
    jmethodID resConstructor = (*env)->GetMethodID(env, resClass, "<init>", "(J[[I)V");
    jobject res=(*env)->NewObject(env, resClass, resConstructor, cost, j_order);
    return res;
  }
