// Problem with function-scoped var (old style)
var funcs = [];
for (var i = 0; i < 5; i++) {
  funcs.push(function () { console.log(i); });
}
funcs.forEach(f => f()); // 5 5 5 5 5

// Fix: block-scoped let creates a per-iteration binding
let funcs2 = [];
for (let j = 0; j < 5; j++) {
  funcs2.push(() => console.log(j));
}
funcs2.forEach(f => f()); // 0 1 2 3 4