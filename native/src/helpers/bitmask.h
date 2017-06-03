#include<stdint.h>
#include<stdio.h>

/**
@return Hamming distance between `n` and 0 (equals number of ones in binary
representation of `n`)
*/
int hamming0(uint64_t n);

/**
@return position of the least significant one in binary representation of
a number
*/
int de_brujin(uint64_t n);

/**
Executes given function for each one in binary representation of a number,
passing 0-based position of this one from the least significant bit
@param t    type of number (must be some kind of integer)
@param bin  number to be iterated
@param i    type and name (or name, if value is declared earlier) of variable
that position is assigned to
@function   piece of code being executed for each `i`
*/
#define iterate_bin(t, bin, i, fun) ({ \
  t bin_cp = bin; \
  while(bin_cp > 0) { \
    i = de_brujin(bin_cp); \
    ({fun;}); \
    bin_cp &= (bin_cp-1); \
  } \
});

/**
Prints `size` least significant bits of binary representation of a number
*/
void printbits(uint64_t v, char size);

/**
@return bitmask of first `k`-element combination of set (of size in [k, 64]);
next combination can be obtained with `next_bin_comb`
*/
uint64_t init_bin_comb(int k);

/**
@return next bitmask of k-element combination of `n` element set; k is the same
as in `comb`, so does not need to be supplied
*/
uint64_t next_bin_comb(int64_t comb, int n);
