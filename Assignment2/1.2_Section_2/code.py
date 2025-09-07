# Closures over a loop variable: late binding
funcs = []
for i in range(5):
    funcs.append(lambda: print(i))  # captures name i (evaluated at call time)

for f in funcs:
    f()  # prints: 4 4 4 4 4

# Fix: snapshot i via default-arg
funcs2 = [(lambda i=i: print(i)) for i in range(5)]
for f in funcs2:
    f()  # prints: 0 1 2 3 4