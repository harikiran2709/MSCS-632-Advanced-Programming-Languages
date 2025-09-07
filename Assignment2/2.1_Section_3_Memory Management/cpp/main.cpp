#include <iostream>
#include <thread>
#include <chrono>

int leak_sum(size_t n){
    int* a = new int[n];                 // manual allocation
    for(size_t i=0;i<n;++i) a[i]=1;
    int s=0; for(size_t i=0;i<n;++i) s+=a[i];
    delete[] a;                // INTENTIONAL LEAK (commented out)
    return s;
}

int ok_sum(size_t n){
    int* a = new int[n];
    for(size_t i=0;i<n;++i) a[i]=1;
    int s=0; for(size_t i=0;i<n;++i) s+=a[i];
    delete[] a;                          // freed
    return s;
}

int main(){
    std::cout << leak_sum(5'000'000) << "\n";
    std::cout << ok_sum(5'000'000)   << "\n";
    std::this_thread::sleep_for(std::chrono::seconds(30)); // time to profile
}
