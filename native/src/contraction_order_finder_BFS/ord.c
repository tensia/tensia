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
  *t = (Tensor){
      .total_cost=total_cost, .size=size, .origin=origin, .left=left
    };
  return t;
}

void debug_tensor(Tensor* t, int tensor_cnt) {
  debug("cost: %lld,\tsize: %d,\torigin: ", t->total_cost, t->size);
  debug_bits(t->origin, tensor_cnt);
  debug(",\tleft: ");
  debug_bits(t->left, tensor_cnt);
  debug(",\tright: ");
  debug_bits(t->origin&~t->left, tensor_cnt);
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
    g_hash_table_replace(ht, (gpointer)&(t->origin), (gpointer)t);
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
  int* order, int* i, uint64_t origin, GHashTable** best_contr_results, int tensor_cnt
) {
  Tensor *t = get_contr_results(best_contr_results, origin);
  if(hamming0(t->origin) == 1) {
    order[(*i)++] = de_brujin(t->origin);
  } else {
    int idx = *i;
    (*i)++;
    store_order_r(order, i, t->left, best_contr_results, tensor_cnt);
    order[idx] = *i + tensor_cnt;
    store_order_r(order, i, t->origin&~t->left, best_contr_results, tensor_cnt);
  }
}

/**
Stores contraction order tree in array
@return array with contraction order
*/
int* store_order(uint64_t origin, GHashTable** best_contr_results, int tensor_cnt, int total_tensor_cnt) {
  int* order = malloc(2*sizeof(int) + (2*tensor_cnt - 1) * sizeof(int));
  int i = 0;
  order[0] = 2*tensor_cnt;
  store_order_r(order+1, &i, origin, best_contr_results, total_tensor_cnt);
  return order;
}

void match_to_locked_r(GHashTable** best_contr_results, int tensor_cnt, int locked_cnt, int tensor_id, uint64_t* matchings, uint64_t* best_cost, uint64_t* best_matchings) {
  if(tensor_id < tensor_cnt) {
    uint64_t tensor_id_mask = 1 << tensor_id;
    for(int i = 0; i < locked_cnt; i++) {
      matchings[i] |= tensor_id_mask;
      debug("moving tensor %d to matching %d: ", tensor_id, i);
      debug_bits(matchings[i], tensor_cnt);
      debug("\n");
      match_to_locked_r(best_contr_results, tensor_cnt, locked_cnt, tensor_id+1, matchings, best_cost, best_matchings);
      matchings[i] &= ~tensor_id_mask;
    }
  } else {
    uint64_t max_cost = 0;
    for(int i = 0; i < locked_cnt; i++) {
      debug("checking matching %d: ", i);
      debug_bits(matchings[i], tensor_cnt);
      debug("\n");
      Tensor* res = get_contr_results(best_contr_results, matchings[i]);
      debug("res %p\n", res);
      if(!res) return;
      debug_tensor(res, tensor_cnt);
      if(res->total_cost > max_cost) max_cost = res->total_cost;
    }
    if(!*best_cost || max_cost < *best_cost) {
      memcpy(best_matchings, matchings, locked_cnt*sizeof(uint64_t));
      *best_cost = max_cost;
    }
  }
}

uint64_t match_to_locked(GHashTable** best_contr_results, int tensor_cnt, int locked_cnt, uint64_t** best_matchings) {
  uint64_t matchings[locked_cnt];
  for(int i=0; i<locked_cnt; i++) matchings[i] = 1 << i;
  uint64_t best_cost = 0;
  *best_matchings = malloc(locked_cnt*sizeof(uint64_t));
  match_to_locked_r(best_contr_results, tensor_cnt, locked_cnt, locked_cnt, matchings, &best_cost, *best_matchings);
  return best_cost;
}


uint64_t do_ord(
  int* tensors_sizes, int** contracted_dims_sizes, int locked_cnt,
  int tensor_cnt, int*** order, uint64_t limit
) {
  uint64_t lock_mask = (1 << locked_cnt) - 1;
  int unlocked_cnt = tensor_cnt - locked_cnt;
  // Array of hash tables, where ith table contains best results for all
  // combinations of contracting i tensors. Tables indices are Tensor origins.
  GHashTable* best_contr_results[unlocked_cnt+1];
  for(int i=0; i<=unlocked_cnt; i++)
    best_contr_results[i] = NULL;

  best_contr_results[0] = mk_hash_table();
  // Filling best_contr_results with initial tensors
  for(int i=0; i<tensor_cnt; i++) {
    Tensor* t =
      mk_tensor(0, tensors_sizes[i], ((int64_t)1) << i, 0);
    debug_tensor(t, tensor_cnt);
    g_hash_table_insert(best_contr_results[0], (gpointer)&(t->origin), (gpointer)t);
  }

  uint64_t min_rejected_size = ~(uint64_t)0;

  // Iterating through all stages, where ith stage computes best_contr_results[i]
  for(int stage=2; stage<=unlocked_cnt+1; stage++) {
    GHashTable* stage_content = best_contr_results[stage-1] = mk_hash_table();
    // Checking Tensors in all pairs of stages, which numbers sum up to current
    // stage numbers
    for(int st1=1; st1<=stage/2; st1++) {
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

        GHashTableIter st2_i;
        g_hash_table_iter_init(&st2_i, best_contr_results[st2-1]);
        Tensor* t2;
        while(g_hash_table_iter_next(&st2_i, NULL, (gpointer*)&t2)){
          debug("t2: ");
          debug_tensor(t2, tensor_cnt);
          if((t1->origin & t2->origin) == 0 && ((t1->origin & lock_mask) == 0 || (t2->origin & lock_mask) == 0)) {
            debug("fits\n");
            Tensor* c = contract(t1, t2, contracted_dims_sizes);
            debug("c: ");
            debug_tensor(c, tensor_cnt);
            if(c->total_cost <= limit) {
              reflect(stage_content, c);
            } else {
              if(min_rejected_size > c->total_cost) min_rejected_size = c->total_cost;
              free(c);
            }
          } else debug("doesnt fit\n");
        }
        debug("\n");
      }
    }
  }


  uint64_t result;

  if(locked_cnt == 0) {
    GList* result_data = g_list_first(g_hash_table_get_values(best_contr_results[tensor_cnt-1]));
    if(result_data == NULL) {
      *order = NULL;
      result = min_rejected_size;
    } else {
      Tensor* final_tensor = (Tensor*)result_data->data;
      // Storing contraction tree in array
      *order = malloc(sizeof(int*));
      **order = store_order(final_tensor->origin, best_contr_results, tensor_cnt, tensor_cnt);
      result = final_tensor->total_cost;
    }
  } else {
    uint64_t* origins;
    result = match_to_locked(best_contr_results, tensor_cnt, locked_cnt, &origins);
    debug("matching done\n");
    if(result == 0) {
      *order = NULL;
      result = min_rejected_size;
    } else {
      *order = malloc(locked_cnt*sizeof(int*));
      for(int i = 0; i < locked_cnt; i++) {
        debug("storing tree %d\n", i);
        debug_bits(origins[i], tensor_cnt);
        debug("\n");
        Tensor* t = get_contr_results(best_contr_results, origins[i]);
        debug_tensor(t, tensor_cnt);
        (*order)[i] = store_order(origins[i], best_contr_results, hamming0(origins[i]), tensor_cnt);
        debug("stored tree %d\n", i);
      }
    }
  }

  for(int i=1; i<=unlocked_cnt; i++) {
    debug("freeing %d %p\n", i, best_contr_results[i]);
    g_hash_table_destroy(best_contr_results[i]);
  }
  return result;
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
  int* tensors_sizes, int** contracted_dims_sizes, int locked_cnt,
  int tensor_cnt, int min_dim_size, int*** order
) {
  int total_size = 0;
  for(int i=0; i < tensor_cnt; i++) total_size += tensors_sizes[i];
  *order = NULL;
  uint64_t result = 0;
  for(uint64_t limit = 1; *order == NULL;){
    result = do_ord(tensors_sizes, contracted_dims_sizes, locked_cnt, tensor_cnt, order, limit);
    limit = max(result, min_dim_size*limit);
  }
  return result;
}
