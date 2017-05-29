#include<stdint.h>

#include "jni/BFS_contraction_order.h"

#include "BFS_contraction_order/ord.h"

JNIEXPORT jlong JNICALL Java_BFSContractionOrder_00024_ord
  (JNIEnv * env, jobject obj, jintArray _tensors_sizes, jobjectArray _contracted_dims_sizes) {
    int tensors_sizes[] = {12, 20, 30};
    int* contracted_dims_sizes[3];
    contracted_dims_sizes[0] = (int[]){0, 4, 3};
    contracted_dims_sizes[1] = (int[]){4, 0, 5};
    contracted_dims_sizes[2] = (int[]){3, 5, 0};
    uint64_t res = ord(tensors_sizes, contracted_dims_sizes, 3);
    return (int)res;
  }
