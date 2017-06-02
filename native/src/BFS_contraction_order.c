#include<stdint.h>

#include "jni/BFSContractionOrder__.h"

#include "BFS_contraction_order/ord.h"

JNIEXPORT jlong JNICALL Java_BFSContractionOrder_00024_ord(
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
    uint64_t res = ord(tensors_sizes, contracted_dims_sizes, tensor_cnt);
    for(int i=0; i < tensor_cnt; i++) {
      jintArray a =
        (jintArray)(*env)->GetObjectArrayElement(env, j_contracted_dims_sizes, i);
      (*env)->ReleaseIntArrayElements(env, a, (jint*)contracted_dims_sizes[i], 0);
      (*env)->DeleteLocalRef(env, a);
    }
    (*env)->ReleaseIntArrayElements(env, j_tensors_sizes, tensors_sizes, 0);
    return (long long)res;
  }
