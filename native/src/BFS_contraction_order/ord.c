#include "ord.h"

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

// static inline char contractable(Tensor* t1, Tensor* t2) {
//   return (t1->origin & t2->origin) == 0;
// }

static inline Tensor* contract(
  Tensor* t1, Tensor* t2, int** contracted_dims_sizes
) {
  uint64_t cds = contracted_dims_size(
    t1->origin, t2->origin, contracted_dims_sizes
  );
  // printf("cds: %lld\n", cds);
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
  for(int stage=2; stage<=tensor_cnt; stage++) {
    GHashTable* stage_content = best_contr_results[stage-1] = mk_hash_table();
    for(int st1=1; st1<=(stage+1)/2; st1++) {
      int st2 = stage - st1;
      printf("s: %d %d %d\n", stage, st1, st2);
      GHashTableIter st1_i;
      g_hash_table_iter_init(&st1_i, best_contr_results[st1-1]);
      Tensor *t1;
      while(g_hash_table_iter_next(&st1_i, NULL, (gpointer*)&t1)){
        printf("t1: ");
        print_tensor(t1);
        for(
          uint64_t comb = init_bin_comb(st2);
          comb;
          comb=next_bin_comb(comb, tensor_cnt-st1)
        ) {
          printbits(comb, 10);
          printf("\n");
          uint64_t scaled_comb=comb;
          for(uint64_t o1=t1->origin; o1 > 0; o1&=(o1-1)) {
            uint64_t b = o1&(-o1);
            scaled_comb = ((scaled_comb&~(b-1))<<1)|(scaled_comb&(b-1));
          }
          printbits(scaled_comb, 10);
          printf("\n");
          Tensor* t2 = (Tensor*)g_hash_table_lookup(
            best_contr_results[st2-1], (gconstpointer)&scaled_comb
          );
          printf("t2: ");
          print_tensor(t2);
          Tensor* c = contract(t1, t2, contracted_dims_sizes);
          printf("c: ");
          print_tensor(c);
          reflect(stage_content, c);
        }
        printf("\n");
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
