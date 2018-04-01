a = object
a.x = object
dump a
b ~= a
dump a // a should be dead here
c = b.x // NPE
