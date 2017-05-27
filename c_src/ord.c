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
  uint64_t origin;
} Tensor;

static inline Tensor* mk_tensor(int64_t total_cost, int32_t size, int64_t origin) {
  Tensor* t = malloc(sizeof(Tensor));
  *t = (Tensor){.total_cost=total_cost, .size=size, .origin=origin};
  return t;
}

void printbits(uint64_t v, char size) {
  for(int i = size-1; i >= 0; i--) putchar('0' + ((v >> i) & 1));
}

void print_tensor(Tensor* t) {
  printf("cost: %lld,\tsize: %d,\torigin:\t", t->total_cost, t->size);
  printbits(t->origin, 3);
  printf("\n");
}

static inline uint64_t contracted_dims_size(
  uint64_t origin1, uint64_t origin2,
  int** contracted_dims_sizes
) {
  uint64_t acc = 1;
  iterate_bin(uint64_t, origin1, int i, {
    iterate_bin(uint64_t, origin2, int j, {
      acc *= contracted_dims_sizes[i][j];
    });
  });
  return acc;
}

static inline char contractable(Tensor* t1, Tensor* t2) {
  return (t1->origin & t2->origin) == 0;
}

static inline Tensor* contract(
  Tensor* t1, Tensor* t2, int** contracted_dims_sizes
) {
  uint64_t cds = contracted_dims_size(
    t1->origin, t2->origin, contracted_dims_sizes
  );
  // printf("%lld\n", cds);
  return mk_tensor(
    max(t1->total_cost, t2->total_cost) + (uint64_t)(t1->size) * t2->size / cds,
    (uint64_t)(t1->size) * t2->size / (cds * cds),
    t1->origin | t2->origin
  );
}

static inline GHashTable* mk_hash_table() {
  return g_hash_table_new_full(g_int_hash, g_int_equal, NULL, free);
}

static inline void reflect(GHashTable* ht, Tensor* t) {
  Tensor* ot = (Tensor*)g_hash_table_lookup(ht, (gconstpointer)&(t->origin));
  if(!ot || t->total_cost < ot->total_cost)
    g_hash_table_insert(ht, (gpointer)&(t->origin), (gpointer)t);
}

uint64_t ord(int* tensors_sizes, int** contracted_dims_sizes, int tensor_cnt) {
  GHashTable* best_contr_results[tensor_cnt];
  best_contr_results[0] = mk_hash_table();
  for(int i=0; i<tensor_cnt; i++) {
    Tensor* t = mk_tensor(0, tensors_sizes[i], ((int64_t)1) << i);
    g_hash_table_insert(best_contr_results[0], (gpointer)&(t->origin), (gpointer)t);
  }
  for(int stage=1; stage<tensor_cnt; stage++) {
    GHashTable* stage_content = best_contr_results[stage] = mk_hash_table();
    for(int st1=0; st1<=(stage-1)/2; st1++) {
      int st2 = stage - 1 - st1;
      printf("s: %d %d %d\n", stage, st1, st2);
      GHashTableIter st1_i, st2_i;
      g_hash_table_iter_init(&st1_i, best_contr_results[st1]);
      Tensor *t1, *t2;
      while(g_hash_table_iter_next(&st1_i, NULL, (gpointer*)&t1)){
        g_hash_table_iter_init(&st2_i, best_contr_results[st2]);
        while(g_hash_table_iter_next(&st2_i, NULL, (gpointer*)&t2)) {
          print_tensor(t1);
          print_tensor(t2);
          if(contractable(t1, t2)) {
            Tensor* c = contract(t1, t2, contracted_dims_sizes);
            print_tensor(c);
            reflect(stage_content, c);
          }
          printf("\n");
        }
      }
    }
  }

  Tensor* final_tensor = (Tensor*)g_list_first(g_hash_table_get_values(
      best_contr_results[tensor_cnt-1]
    ))->data;
  uint64_t total_cost = final_tensor->total_cost;
  for(int i=0; i<tensor_cnt; i++)
    g_hash_table_destroy(best_contr_results[i]);
  return total_cost;
}

int main() {
  int tensors_sizes[] = {12, 20, 30};
  int* contracted_dims_sizes[3];
  contracted_dims_sizes[0] = (int[]){0, 4, 3};
  contracted_dims_sizes[1] = (int[]){4, 0, 5};
  contracted_dims_sizes[2] = (int[]){3, 5, 0};
  uint64_t res = ord(tensors_sizes, contracted_dims_sizes, 3);
  printf("%lld\n", res);
  return 0;
}
