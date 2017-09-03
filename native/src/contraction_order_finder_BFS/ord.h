#include<stdio.h>
#include<stdint.h>
#include<stdlib.h>
#include<glib.h>
#include<string.h>

#include "../helpers/common.h"
#include "../helpers/bitmask.h"

uint64_t ord(
  int* tensors_sizes, int** contracted_dims_sizes, int tensor_cnt, int** order
);
