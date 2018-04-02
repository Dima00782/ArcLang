a = object

thread {
    b = a
}

sleep

dump b
b.x = object
dump b // b should be deref here
a.y = object
// a will die here
