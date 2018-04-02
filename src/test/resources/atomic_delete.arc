a = object
a.x = object
a.x.y = object
thread {
    b ~= a.x.y
}
sleep
a.x.y.z = object
// a should be dead here
dump b