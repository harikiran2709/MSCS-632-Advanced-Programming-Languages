# Assignment 8: Family Tree Program in Prolog

## Prerequisites
Prolog interpreter (SWI-Prolog recommended)
- Install Prolog: `brew install swi-prolog
## Files
- `family_tree.pl` - Main Prolog program with family relationships and rules
- `sample_queries.txt` - Sample queries with expected outputs
- `README.md` - This file

## How to Run
```bash
cd Assignment8
swipl family_tree.pl
```
## Sample Queries
```prolog
% Show family tree structure
show_family_tree.

% Basic queries
children(john, Children).
siblings(mary, Siblings).
grandparent(john, alice).
cousin(alice, charlie).

% Recursive queries
ancestors(eve, Ancestors).
descendants(john, Descendants).
```

## Exit
```prolog
halt.
```

## Features Implemented

### Basic Relationships
- `parent(X, Y)` - X is parent of Y
- `male(X)` - X is male
- `female(X)` - X is female

### Derived Relationships
- `grandparent(X, Y)` - X is grandparent of Y
- `sibling(X, Y)` - X and Y are siblings
- `cousin(X, Y)` - X and Y are cousins

### Recursive Logic
- `ancestor(X, Y)` - X is ancestor of Y (recursive)
- `descendant(X, Y)` - X is descendant of Y (recursive)

### Additional Relationships
- `father(X, Y)` - X is father of Y
- `mother(X, Y)` - X is mother of Y
- `uncle(X, Y)` - X is uncle of Y
- `aunt(X, Y)` - X is aunt of Y

### Query Helpers
- `children(X, Children)` - Get all children of X
- `siblings(X, Siblings)` - Get all siblings of X
- `descendants(X, Descendants)` - Get all descendants of X
- `ancestors(X, Ancestors)` - Get all ancestors of X

