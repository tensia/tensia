#include "bitmask.h"

//de_brujin

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

int de_brujin(uint64_t n) {
  return de_brujin_array[((n&(-n))*0x022fdd63cc95386d) >> 58];
}

//printbits

void printbits(uint64_t v, char size) {
  for(int i = size-1; i >= 0; i--) putchar('0' + ((v >> i) & 1));
}

//combinations

uint64_t init_bin_comb(int k) {
  return (1 << k) - 1;
}

uint64_t next_bin_comb(int64_t comb, int n) {
  uint64_t x = comb & -comb;
  uint64_t y = comb + x;
  uint64_t z = (comb & ~y);
  comb = z / x;
  comb >>= 1;
  comb |= y;
  return (comb < 1<<n) ? comb : 0;
}
