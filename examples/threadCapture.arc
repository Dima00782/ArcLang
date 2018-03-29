a = object
dump a
thread {
	dump a
	b ~= a
	dump a
}
sleep
sleep
sleep
sleep
dump a
sleep
a.x = object
c = object // a can be free here
