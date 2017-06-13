#include "../../debug_mode.h"

#define max(A, B) ((A) >= (B) ? (A) : (B))

#ifdef DEBUG_ON
  #define debug(fmt, ...) printf(fmt, ##__VA_ARGS__)
#endif
#ifndef DEBUG_ON
  #define debug(...) (void*)NULL
#endif
