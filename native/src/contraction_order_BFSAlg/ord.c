#include "ord.h"

/**
Struct holding information about tensor, used while computing
*/
typedef struct {
  uint64_t total_cost; // cost of constructing tensor from original tensors
  uint32_t size; // product of sizes of all dimensions
  uint64_t origin; // bitmask, where ith bit is one <=> tensor is descendant
                   // of ith original tensor
  uint64_t left; // origin of the left parent
} Tensor;

/**
Convinience function for creating Tensor structs
*/
static inline Tensor* mk_tensor(
  int64_t total_cost, int32_t size, int64_t origin, int64_t left
) {
  Tensor* t = malloc(sizeof(Tensor));
  *t = (Tensor){.total_cost=total_cost, .size=size, .origin=origin, .left=left};
  return t;
}

void debug_tensor(Tensor* t, int tensor_cnt) {
  debug("cost: %lld,\tsize: %d,\torigin:\t", t->total_cost, t->size);
  debug_bits(t->origin, tensor_cnt);
  debug("\n");
}

/**
@return product of sizes of contracted dimensions of two Tensors
        of origin1 and origin2
*/
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

/**
@return Tensor - result of contracting t1 and t2
*/
static inline Tensor* contract(
  Tensor* t1, Tensor* t2, int** contracted_dims_sizes
) {
  uint64_t cds = contracted_dims_size(
    t1->origin, t2->origin, contracted_dims_sizes
  );
  // debug("cds: %lld\n", cds);
  return mk_tensor(
    max(t1->total_cost, t2->total_cost) + (uint64_t)(t1->size) * t2->size / cds,
    (uint64_t)(t1->size) * t2->size / (cds * cds),
    t1->origin | t2->origin,
    t1->origin
  );
}

static inline GHashTable* mk_hash_table() {
  return g_hash_table_new_full(g_int_hash, g_int_equal, NULL, free);
}

/**
Replaces previous Tensor with new one with the same origin, if the new one
has lower total_cost
*/
static inline void reflect(GHashTable* ht, Tensor* t) {
  Tensor* ot = (Tensor*)g_hash_table_lookup(ht, (gconstpointer)&(t->origin));
  if(!ot || t->total_cost < ot->total_cost)
    g_hash_table_insert(ht, (gpointer)&(t->origin), (gpointer)t);
}

/**
@return Tensor of the lowest total_cost for given origin
assumes that best_contr_results are already calculated
*/
static inline Tensor* get_contr_results(
  GHashTable** best_contr_results, uint64_t origin
) {
  return (Tensor*)g_hash_table_lookup(
    best_contr_results[hamming0(origin)-1], (gconstpointer)&origin
  );
}

void store_order_r(
  int* order, int* i, Tensor* t, GHashTable** best_contr_results, int tensor_cnt
) {
  if(hamming0(t->origin) == 1)
    order[(*i)++] = de_brujin(t->origin);
  else {
    int idx = *i;
    (*i)++;
    Tensor* l = get_contr_results(best_contr_results, t->left);
    store_order_r(order, i, l, best_contr_results, tensor_cnt);
    order[idx] = *i + tensor_cnt;
    Tensor* r = get_contr_results(best_contr_results, t->origin&~t->left);
    store_order_r(order, i, r, best_contr_results, tensor_cnt);
  }
}

/**
Stores contraction order tree in array
@return array with contraction order
*/
int* store_order(Tensor* t, GHashTable** best_contr_results, int tensor_cnt) {
  int* order = malloc((2*tensor_cnt - 1) * sizeof(int));
  int i = 0;
  store_order_r(order, &i, t, best_contr_results, tensor_cnt);
  return order;
}

/**
Calculates best contraction order for parallel contraction computing
@param tensors_sizes  array of products of dimensions sizes of each tensor
@param contracted_dims_sizes  two-dimensional array of products of
  contracted dimensions sizes of each pair of tensors; if there are no contracted
  dimensions, size should equal 1
@param tensor_cnt amount of tensors
@param order  pointer to array of ints, through which contraction order tree
  is returned, encoded with store_order function; for sample parser in python
  see parse_ord.py
@return total cost of contraction
*/
uint64_t ord(
  int* tensors_sizes, int** contracted_dims_sizes, int tensor_cnt, int** order
) {
  // Array of hash tables, where ith table contains best results for all
  // combinations of contracting i tensors. Tables indices are Tensor origins.
  GHashTable* best_contr_results[tensor_cnt];
  best_contr_results[0] = mk_hash_table();
  // Filling best_contr_results with initial tensors
  for(int i=0; i<tensor_cnt; i++) {
    Tensor* t = mk_tensor(0, tensors_sizes[i], ((int64_t)1) << i, 0);
    g_hash_table_insert(best_contr_results[0], (gpointer)&(t->origin), (gpointer)t);
  }
  // Iterating through all stages, where ith stage computes best_contr_results[i]
  for(int stage=2; stage<=tensor_cnt; stage++) {
    GHashTable* stage_content = best_contr_results[stage-1] = mk_hash_table();
    // Checking Tensors in all pairs of stages, which numbers sum up to current
    // stage numbers
    for(int st1=1; st1<=(stage+1)/2; st1++) {
      int st2 = stage - st1;
      debug("s: %d %d %d\n", stage, st1, st2);
      GHashTableIter st1_i;
      g_hash_table_iter_init(&st1_i, best_contr_results[st1-1]);
      Tensor *t1;
      // Iterating through all tenosors in st1
      while(g_hash_table_iter_next(&st1_i, NULL, (gpointer*)&t1)){
        debug("t1: ");
        debug_tensor(t1, tensor_cnt);
        // Iterating through all tensors in st2 that can be contracted with st1
        // which means that t1.origin & t2.origin == 0 for each t1, t2
        for(
          uint64_t comb = init_bin_comb(st2);
          comb;
          comb=next_bin_comb(comb, tensor_cnt-st1)
        ) {
          uint64_t scaled_comb=comb;
          for(uint64_t o1=t1->origin; o1 > 0; o1&=(o1-1)) {
            uint64_t b = o1&(-o1);
            scaled_comb = ((scaled_comb&~(b-1))<<1)|(scaled_comb&(b-1));
          }
          Tensor* t2 = (Tensor*)g_hash_table_lookup(
            best_contr_results[st2-1], (gconstpointer)&scaled_comb
          );
          debug("t2: ");
          debug_tensor(t2, tensor_cnt);
          Tensor* c = contract(t1, t2, contracted_dims_sizes);
          debug("c: ");
          debug_tensor(c, tensor_cnt);
          reflect(stage_content, c);
        }
        debug("\n");
      }
    }
  }

  // Getting the tensor being result of contraction (the only tensor in last stage)
  Tensor* final_tensor = (Tensor*)g_list_first(g_hash_table_get_values(
      best_contr_results[tensor_cnt-1]
    ))->data;
  // Storing contraction tree in array
  *order = store_order(final_tensor, best_contr_results, tensor_cnt);
  uint64_t total_cost = final_tensor->total_cost;
  for(int i=0; i<tensor_cnt; i++)
    g_hash_table_destroy(best_contr_results[i]);
  return total_cost;
}
