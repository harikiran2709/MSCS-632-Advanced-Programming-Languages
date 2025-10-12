% Family Tree Program in Prolog
% Assignment 8 - Logical Programming with Prolog

% ===========================================
% BASIC RELATIONSHIPS (Facts)
% ===========================================

% Parent relationships
parent(john, mary).
parent(john, tom).
parent(jane, mary).
parent(jane, tom).
parent(mary, alice).
parent(mary, bob).
parent(tom, charlie).
parent(tom, diana).
parent(alice, eve).
parent(bob, frank).
parent(charlie, grace).
parent(diana, henry).

% Gender facts
male(john).
male(tom).
male(bob).
male(charlie).
male(frank).
male(henry).

female(jane).
female(mary).
female(alice).
female(diana).
female(eve).
female(grace).

% ===========================================
% DERIVED RELATIONSHIPS (Rules)
% ===========================================

% Grandparent relationship
grandparent(X, Y) :-
    parent(X, Z),
    parent(Z, Y).

% Sibling relationship
sibling(X, Y) :-
    parent(P, X),
    parent(P, Y),
    X \= Y.

% Cousin relationship
cousin(X, Y) :-
    parent(PX, X),
    parent(PY, Y),
    sibling(PX, PY).

% ===========================================
% RECURSIVE LOGIC
% ===========================================

% Ancestor relationship (recursive)
ancestor(X, Y) :-
    parent(X, Y).

ancestor(X, Y) :-
    parent(X, Z),
    ancestor(Z, Y).

% Descendant relationship (recursive)
descendant(X, Y) :-
    ancestor(Y, X).

% Alternative descendant definition for clarity
descendant2(X, Y) :-
    parent(X, Y).

descendant2(X, Y) :-
    parent(X, Z),
    descendant2(Z, Y).

% ===========================================
% ADDITIONAL RELATIONSHIPS
% ===========================================

% Father relationship
father(X, Y) :-
    parent(X, Y),
    male(X).

% Mother relationship
mother(X, Y) :-
    parent(X, Y),
    female(X).

% Uncle/Aunt relationship
uncle(X, Y) :-
    parent(P, Y),
    sibling(X, P),
    male(X).

aunt(X, Y) :-
    parent(P, Y),
    sibling(X, P),
    female(X).

% ===========================================
% QUERY HELPERS
% ===========================================

% Get all children of a person
children(X, Children) :-
    findall(Child, parent(X, Child), Children).

% Get all siblings of a person
siblings(X, Siblings) :-
    findall(Sibling, sibling(X, Sibling), Siblings).

% Get all descendants of a person
descendants(X, Descendants) :-
    findall(Descendant, descendant2(X, Descendant), Descendants).

% Get all ancestors of a person
ancestors(X, Ancestors) :-
    findall(Ancestor, ancestor(Ancestor, X), Ancestors).

% ===========================================
% FAMILY TREE DISPLAY
% ===========================================

% Display the family tree structure
show_family_tree :-
    write('=== FAMILY TREE STRUCTURE ==='), nl, nl,
    write('Generation 1:'), nl,
    write('  john (male) ---- jane (female)'), nl,
    write('  (parents of mary and tom)'), nl, nl,
    write('Generation 2:'), nl,
    write('  mary (female) ---- tom (male)'), nl,
    write('  (children of john and jane)'), nl, nl,
    write('Generation 3:'), nl,
    write('  alice (female)    charlie (male)'), nl,
    write('  bob (male)        diana (female)'), nl, nl,
    write('Generation 4:'), nl,
    write('  eve (female)      grace (female)'), nl,
    write('  frank (male)      henry (male)'), nl, nl,
    write('=== RELATIONSHIPS ==='), nl,
    write('Parents:'), nl,
    forall(parent(P, C), (write('  '), write(P), write(' is parent of '), write(C), nl)),
    nl,
    write('Siblings:'), nl,
    forall(sibling(X, Y), (write('  '), write(X), write(' and '), write(Y), write(' are siblings'), nl)),
    nl,
    write('Grandparents:'), nl,
    forall(grandparent(G, C), (write('  '), write(G), write(' is grandparent of '), write(C), nl)).
