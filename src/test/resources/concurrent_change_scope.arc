a = object

thread {
    b = a
}

thread {
    b = a
}

sleep

dump b
b.x = object
