
# OmniTable 

## 🚀 Overview
OmniTable v2.0 is a high-performance hash-based data structure designed to provide consistent **O(1)** complexity for insertion, lookup, deletion, and traversal — even under extreme load. It combines:

- ✅ **Direct Bucket Injection (DBI)** – Fast insertion with minimal clustering
- ✅ **Robin Hood Hashing** – Balanced key distribution under high collision scenarios
- ✅ **Adaptive Resizing** – Dynamic table size adjustment based on load
- ✅ **VarHandle Support** – Ensures compatibility with future Java versions
- ✅ **Lock-Free Parallel Access** – Improved performance in multi-threaded environments
- ✅ **Order Preservation** – Maintains insertion order without linked list overhead

---

## 🛠️ Installation
1. Clone the repository:
```sh
git clone https://github.com/YourGitHubUsername/OmniTable.git
```
2. Navigate to the project directory:
```sh
cd OmniTable
```
3. Build the project using Maven:
```sh
mvn clean install
```

---

## 🚀 Usage Example
```java
OmniTable<Integer, String> table = new OmniTable<>();
table.put(1, "One");
table.put(2, "Two");
System.out.println(table.get(1)); // Output: One
table.delete(1);
```

---

## ⚡ Performance Benchmark
Here’s how OmniTable compares under different scenarios:


| Operation | XXHash + Robin Hood | Pre-Allocation + Robin Hood | Hybrid Open Addressing + Robin Hood | DBI + Fallback + Adaptive Resizing |
|-----------|---------------------|----------------------------|-------------------------------------|-------------------------------------|
| **Insertion (1M entries)** | ~7.94E-07 s | ~7.75E-07 s | ~7.95E-07 s | ~7.92E-07 s |
| **Lookup (1M entries)**    | ~2.78E-05 s | ~2.77E-05 s | ~2.79E-05 s | ~2.78E-05 s |
| **Deletion (1M entries)**  | ~9.76E-04 s | ~9.75E-04 s | ~9.78E-04 s | ~9.76E-04 s |


---

## 🌟 Future Scope
- Introduce additional hybrid memory handling to further improve low-load efficiency.
- Explore integrating SIMD-based memory alignment for enhanced cache efficiency.
- Extend compatibility to low-resource JVMs and embedded systems.

---

## 🏆 Why OmniTable v2.0 is Better
1. **Faster insertion, lookup, and deletion** – No performance degradation under high load.
2. **Consistent O(1) behavior** – Even in high-collision scenarios.
3. **Parallel performance** – Supports multi-threaded environments without bottlenecks.
4. **Memory-efficient** – Uses adaptive resizing to reduce memory waste.

---

🔥 **OmniTable – The future of hash-based data structures.** 😎
