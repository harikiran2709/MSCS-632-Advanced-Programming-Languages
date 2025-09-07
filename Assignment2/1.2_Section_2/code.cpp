#include <iostream>
#include <vector>
#include <functional>
using namespace std;

int main() {
    vector<function<void()>> fs_ref, fs_val;
    for (int i = 0; i < 5; i++) {
        fs_ref.push_back([&](){ cout << i << "\n"; });  // capture by reference
        fs_val.push_back([i](){ cout << i << "\n"; });  // capture by value (snapshot)
    }
    for (auto& f : fs_ref) f(); // typically: 5 5 5 5 5 (i after loop)
    for (auto& f : fs_val) f(); // 0 1 2 3 4
}