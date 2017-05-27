#include<stdio.h>
#include<stdint.h>
#include<stdlib.h>
#include<glib.h>

#define max(A, B) ((A) >= (B) ? (A) : (B))

const int de_brujin_array[64] =
{
    0,  1,  2, 53,  3,  7, 54, 27,
    4, 38, 41,  8, 34, 55, 48, 28,
   62,  5, 39, 46, 44, 42, 22,  9,
   24, 35, 59, 56, 49, 18, 29, 11,
   63, 52,  6, 26, 37, 40, 33, 47,
   61, 45, 43, 21, 23, 58, 17, 10,
   51, 25, 36, 32, 60, 20, 57, 16,
   50, 31, 19, 15, 30, 14, 13, 12,
};

//returns position of the least significant one in binary number
#define de_brujin(n) \
  (de_brujin_array[((uint64_t)(n&(-n))*0x022fdd63cc95386d) >> 58])

#define iterate_bin(t, bin, i, fun) ({ \
  t bin_cp = bin; \
  while(bin_cp > 0) { \
    i = de_brujin(bin_cp); \
    fun; \
    bin_cp &= (bin_cp-1); \
  } \
});

typedef struct {
  uint64_t total_cost;
  uint32_t size;
  uint32_t origin;
} Tensor;

void printbits(uint64_t v, char size) {
  for(int i = size-1; i >= 0; i--) putchar('0' + ((v >> i) & 1));
}

void printTensor(Tensor t) {
  printf("cost: %lld,\tsize: %d,\torigin:\t", t.total_cost, t.size);
  printbits(t.origin, 3);
  printf("\n");
}

static inline uint64_t contracted_dims_size(
  uint32_t origin1, uint32_t origin2,
  int** contracted_dims_sizes
) {
  uint64_t acc = 1;
  iterate_bin(uint32_t, origin1, int i, {
    iterate_bin(uint32_t, origin2, int j, {
      acc *= contracted_dims_sizes[i][j];
    });
  });
  return acc;
}

static inline char contractable(Tensor t1, Tensor t2) {
  return (t1.origin & t2.origin) == 0;
}

static inline Tensor contract(
  Tensor t1, Tensor t2, int** contracted_dims_sizes
) {
  uint64_t cds = contracted_dims_size(
    t1.origin, t2.origin, contracted_dims_sizes
  );
  // printf("%lld\n", cds);
  return (Tensor){
    .total_cost = max(t1.total_cost, t2.total_cost)
      + (uint64_t)(t1.size) * t2.size / cds,
    .size = (uint64_t)(t1.size) * t2.size / (cds * cds),
    .origin = t1.origin | t2.origin,
  };
}

uint64_t ord(int* tensors_sizes, int** contracted_dims_sizes, int tensor_cnt) {
  Tensor* best_contr_results[tensor_cnt];
  best_contr_results[0] = malloc(tensor_cnt*sizeof(Tensor));
  for(int i=0; i<tensor_cnt; i++) {
    best_contr_results[0][i] = (Tensor){
      .total_cost=0, .size=tensors_sizes[i], .origin=1<<i,
    };
  }
  uint64_t stage_sizes[tensor_cnt];
  stage_sizes[0] = tensor_cnt;
  for(int i=1; i<tensor_cnt; i++) {
    stage_sizes[i] = stage_sizes[i-1]*(tensor_cnt-i)/(i+1);
    //printf("%lld\n", stage_sizes[i]);
  }
  for(int stage=1; stage<tensor_cnt; stage++) {
    best_contr_results[stage] = malloc(stage_sizes[stage]*sizeof(Tensor));
    Tensor* stc = best_contr_results[stage];
    int i = 0;
    for(int st1=0; st1<=(stage-1)/2; st1++) {
      int st2 = stage - 1 - st1;
      printf("s: %d %d %d\n", stage, st1, st2);
      Tensor* stc1 = best_contr_results[st1];
      Tensor* stc2 = best_contr_results[st2];
      for(int j=0;j<stage_sizes[st1];j++)
        for(int k=0;k<stage_sizes[st2];k++) {
          printTensor(stc1[j]);
          printTensor(stc2[k]);
          if(contractable(stc1[j], stc2[k])) {
            stc[i++] = contract(stc1[j], stc2[k], contracted_dims_sizes);
            printTensor(stc[i-1]);
          }
          printf("\n");
        }
    }
  }
  return best_contr_results[tensor_cnt-1][0].total_cost;
}

int main() {
  int tensors_sizes[] = {12, 20, 30};
  int* contracted_dims_sizes[3];
  contracted_dims_sizes[0] = (int[]){0, 4, 3};
  contracted_dims_sizes[1] = (int[]){4, 0, 5};
  contracted_dims_sizes[2] = (int[]){3, 5, 0};
//   printf("%d\n", contracted_dims_sizes[0][1]);
// return 0;
  uint64_t res = ord(tensors_sizes, contracted_dims_sizes, 3);
  printf("%lld\n", res);
  return 0;
}
