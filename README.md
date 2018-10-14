# Reddi

One key-value query engine used to query from static file.

## Usage

pass arguments to AccessPoint to execute program.

## Static file format

```
<key_size, key, value_size, value>
```
The basic tuple showed above in the file is out of order.
That means, the original writer of the file can append to the file sequencially, which will get the outstanding performance of the storage device.  

## Internal Logic

As we all know, the organized format is helpful to read from file efficiently. And the index can be seen as one part of the format. 
Without the helper info, we have to scan the entire file to find the requested value, O(n) time complexity here.

Usually, the index can be divided into 2 category, tree-like and hash-like separately.
Tree-like index can provide O(log(n)) time complexity at the cost of more space required.
Hash-like index has O(1) time complexity ideally when the hash confliction is negligible.

Apart from the index helper struct, we can sort the data firstly,
and then use binary search technology to accelerate the query.

## Specific Impl

Specific scenario needs specific opt and special policy and settings. 

Assuming the requirements as below.

### specific hardware restriction

8 cores cpu, 4 GiB mem, 4 TiB hdd.

### static file restriction

1 TiB

### Solution

#### persist helpful info 

If we have enough mem, then we can use hash-like struct to organize all the data,
and optimize cache, cpu related stuff to improve the performance. 
But the restricted resource force me to persist the necessary helpful struct.

* B-Tree/B+Tree
* meta generated after sort

#### keep important info in mem

#### enable multi-core advantage by leveraging multi-thread

#### key, value distribution considered



## Follow-up work

Integrating the write and read process is helpful to optimize the overall performance and reduce the cost.
Say, we construct the needed index when sequecially write key-value,
 then this project will evolve to one complete key-value store,
  such as [Redis](https://github.com/antirez/redis), [LevelDB](https://github.com/google/leveldb),
  [RocksDB](https://github.com/facebook/rocksdb).
  
Asynchronous API and impl which can improve the performance.

